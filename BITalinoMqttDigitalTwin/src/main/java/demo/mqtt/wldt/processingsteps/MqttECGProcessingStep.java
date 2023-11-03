package demo.mqtt.wldt.processingsteps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.mqtt.message.EcgMessage;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.CommandLineExecutor;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.CommandLineResult;
import demo.mqtt.wldt.augmentation.csvwriter.CsvFileWriter;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.LinuxCliExecutor;
import demo.mqtt.utils.SenMLPack;
import demo.mqtt.utils.SenMLRecord;
import it.unimore.dipi.iot.wldt.processing.PipelineData;
import it.unimore.dipi.iot.wldt.processing.ProcessingStep;
import it.unimore.dipi.iot.wldt.processing.ProcessingStepListener;
import it.unimore.dipi.iot.wldt.processing.cache.PipelineCache;
import it.unimore.dipi.iot.wldt.worker.mqtt.MqttPipelineData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class MqttECGProcessingStep implements ProcessingStep {

    private static final Logger logger = LoggerFactory.getLogger(MqttECGProcessingStep.class);

    private final static String PIPELINE_CACHE_VALUE_LIST = "value_list";

    private final static long TIME_THRESHOLD_SECONDS = 2 * 60;


    public static ObjectMapper mapper;

    public MqttECGProcessingStep() {
    }

    @Override
    public void execute(PipelineCache pipelineCache, PipelineData pipelineData, ProcessingStepListener processingStepListener) {
        MqttPipelineData data = null;
        if (pipelineData instanceof MqttPipelineData)
            data = (MqttPipelineData) pipelineData;
        else if (processingStepListener != null)
            processingStepListener.onStepError(this, pipelineData, String.format("Wrong PipelineData for MqttSenmlProcessingStep ! Data type: %s", pipelineData.getClass()));
        else
            logger.error("Wrong PipelineData for MqttSenmlProcessingStep ! Data type: {}", pipelineData.getClass());

        try {
            String fileName = "ECG_frames";
            mapper = new ObjectMapper();
            if (processingStepListener != null && data.getPayload() != null && data.getTopic().contains("ECG")) {

                // Starting getting data
                EcgMessage ecgMessage = mapper.readValue(new String(data.getPayload()), new TypeReference<EcgMessage>() {
                });

                //Init Pipeline Cache
                if (pipelineCache.getData(this, PIPELINE_CACHE_VALUE_LIST) == null)
                    pipelineCache.addData(this, PIPELINE_CACHE_VALUE_LIST, new ArrayList<EcgMessage>());

                ArrayList<EcgMessage> valueList = (ArrayList<EcgMessage>) pipelineCache.getData(this, PIPELINE_CACHE_VALUE_LIST);
                valueList.add(ecgMessage);

                //Retrieve the first element of the list
                // EcgMessage firstElement = valueList.get(0);

                logger.info("Cached list size: {}", valueList.size());
                // Maximum size of pipeline
                if (valueList.size() == 10) {
                    if(isCachedDataValid(valueList)) {
                        // Write data to csvFile
                        writeData2CsvEcg(fileName, valueList);
                        // Execute Python file
                        String command = "python csvReaders/csvReader.py demo/data/real/AriannaC_100Hz.csv demo/data/real/AriannaC_100Hz_stressors.csv /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ecg/normal/ecg_ariannaC_100Hz.txt /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ecg/stressed/ecg_ariannaC_100Hz_stressors.txt";
                        CommandLineExecutor commandLineExecutor = new LinuxCliExecutor();
                        CommandLineResult cliResult = commandLineExecutor.executeCommand(command);
                        if(cliResult.getErrorLog() != null)
                            logger.info(cliResult.getErrorLog());
                        logger.info(cliResult.getOutputLog());
                    }
                    pipelineCache.addData(this, PIPELINE_CACHE_VALUE_LIST, valueList);
                    processingStepListener.onStepDone(this, Optional.of(new MqttPipelineData(
                            String.format("%s/%s", data.getTopic(), "CSV"),
                            data.getMqttTopicDescriptor(),
                            String.valueOf(valueList).getBytes(),
                            data.isRetained())));
                    valueList.clear();
                }
                else {
                    pipelineCache.addData(this, PIPELINE_CACHE_VALUE_LIST, valueList);
                    processingStepListener.onStepDone(this, Optional.of(new MqttPipelineData(
                            String.format("%s/%s", data.getTopic(), "SenML"),
                            data.getMqttTopicDescriptor(),
                            String.valueOf(forwardSenmlData(ecgMessage)).getBytes(),
                            data.isRetained())));
                }

            } else {
                assert processingStepListener != null;
                processingStepListener.onStepError(this, data, "Provided Payload is not a Number ! Skipping processing ....");
            }

        } catch (JsonProcessingException jsonProcessingException) {
            logger.error("Processing Step Listener or MqttProcessingInfo Data = Null ! Skipping processing step");
            jsonProcessingException.printStackTrace();
        }catch (Exception e){
            logger.error("MQTT Processing Step Error: {}", e.getLocalizedMessage());

            if(processingStepListener != null)
                processingStepListener.onStepError(this, data, e.getLocalizedMessage());
        }
    }

    private boolean isCachedDataValid(ArrayList<EcgMessage> valueList) {
        logger.info("Validating data ...");
        // Check if we have the correct amount of samples with respect t
        // o the sampling frequency and the target time range
        // Check for missing values; If there are missing packets -> Fix with averaged values
        ArrayList<EcgMessage> ordered = ordering(valueList);
        logger.info("Ordering done. Proceeding with fixing values...");
        valueList = fixValues(ordered);
        logger.info("Fixing done. Proceeding with writing on .csv file...");
        return true;
    }

    private void writeData2CsvEcg (String fileName, ArrayList<EcgMessage> valueList) {
        CsvFileWriter csvFileWriter = new CsvFileWriter(fileName);

        int lastValue = valueList.size();
        long milliseconds = Math.abs(valueList.get(0).getTimeS() - valueList.get(lastValue - 1).getTimeS());

        // csvFileWriter.writeTime(milliseconds);

        logger.info("Writing data {} to csv file ...", valueList.size());
        for (EcgMessage ecgMessage : valueList) {
            csvFileWriter.writeToFile(ecgMessage);
        }

        csvFileWriter.closeCsvFile();
    }

    private SenMLPack forwardSenmlData(EcgMessage ecgMessage) {
        SenMLPack senMLPack = new SenMLPack();

            SenMLRecord senMLRecord = new SenMLRecord();
            senMLRecord.setN(ecgMessage.getType());
            senMLRecord.setV(ecgMessage.getValue());
            senMLRecord.setU("W");
            senMLRecord.setT(ecgMessage.getTimeS());
            senMLPack.add(senMLRecord);

        return senMLPack;
    }

    private ArrayList<EcgMessage> ordering (ArrayList<EcgMessage> valueList){

        int j = valueList.size() / 15;
        int k = 0;
        for(int i=0; i< j-1; i++) {
            valueList.subList(k, k + 15).sort(Comparator.comparing(EcgMessage::getSeqId));
            k = k + 15;
        }
        valueList.subList((valueList.size() - valueList.size() % 15), valueList.size()).sort(Comparator.comparing(EcgMessage::getSeqId));
        return valueList;
    }

    private ArrayList<EcgMessage> fixValues(ArrayList<EcgMessage> valueList) {
        for (int i = 0; i < valueList.size() - 1; i++) {
            if (valueList.get(i + 1).getSeqId() - valueList.get(i).getSeqId() > 1) {
                int missingSeqId = valueList.get(i).getSeqId() + 1;
                EcgMessage missing = new EcgMessage(valueList.get(1).getMacAddress(),
                        valueList.get(1).getType(),
                        missingSeqId,
                        valueList.get(1).getDig1(),
                        valueList.get(1).getDig2(),
                        valueList.get(1).getDig3(),
                        valueList.get(1).getDig4(),
                        valueList.get(1).getFrequency(),
                        (valueList.get(i + 1).getValue() + valueList.get(i).getValue()) / 2,
                        (valueList.get(i + 1).getTimeS() + valueList.get(i).getTimeS()) / 2
                );
                valueList.add(missingSeqId, missing);
            }
        }
        return valueList;
    }

}

