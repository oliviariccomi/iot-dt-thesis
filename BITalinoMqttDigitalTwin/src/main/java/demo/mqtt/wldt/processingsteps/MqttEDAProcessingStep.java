package demo.mqtt.wldt.processingsteps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.mqtt.message.EcgMessage;
import demo.mqtt.message.EdaMessage;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.CommandLineExecutor;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.CommandLineResult;
import demo.mqtt.wldt.augmentation.cliexecutor.utils.LinuxCliExecutor;
import demo.mqtt.wldt.augmentation.csvwriter.CsvFileWriter;
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

public class MqttEDAProcessingStep implements ProcessingStep {

    private static final Logger logger = LoggerFactory.getLogger(MqttEDAProcessingStep.class);

    private final static String PIPELINE_CACHE_VALUE_LIST = "value_list";

    public static ObjectMapper mapper;

    public MqttEDAProcessingStep() {
    }
    @Override
    public void execute(PipelineCache pipelineCache, PipelineData pipelineData, ProcessingStepListener processingStepListener) {
        MqttPipelineData data = null;
        if(pipelineData instanceof MqttPipelineData)
            data = (MqttPipelineData)pipelineData;
        else if(processingStepListener != null)
            processingStepListener.onStepError(this, pipelineData, String.format("Wrong PipelineData for MqttSenmlProcessingStep ! Data type: %s", pipelineData.getClass()));
        else
            logger.error("Wrong PipelineData for MqttSenmlProcessingStep ! Data type: {}", pipelineData.getClass());
        try {
            String fileName = "EDA_frames";
            mapper = new ObjectMapper();
            if (processingStepListener != null && data.getPayload() != null && data.getTopic().contains("EDA")) {

                // Starting getting data
                EdaMessage edaMessage = mapper.readValue(new String(data.getPayload()), new TypeReference<EdaMessage>() {
                });

                //Init Pipeline Cache
                if (pipelineCache.getData(this, PIPELINE_CACHE_VALUE_LIST) == null)
                    pipelineCache.addData(this, PIPELINE_CACHE_VALUE_LIST, new ArrayList<EdaMessage>());

                ArrayList<EdaMessage> valueList = (ArrayList<EdaMessage>) pipelineCache.getData(this, PIPELINE_CACHE_VALUE_LIST);
                valueList.add(edaMessage);

                //Retrieve the first element of the list
                // EdaMessage firstElement = valueList.get(0);

                logger.info("Cached list size: {}", valueList.size());

                // Maximum size of pipeline
                if (valueList.size() == 10) {
                    if(isCachedDataValid(valueList)) {
                        // Write data to csvFile
                        writeData2CsvEda(fileName, valueList);
                        // Execute Python file
                        String command = "python /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/csvReaders/csvReader.py demo/data/real/AriannaC_100Hz.csv demo/data/real/AriannaC_100Hz_stressors.csv /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ecg/normal/ecg_ariannaC_100Hz.txt /Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ecg/stressed/ecg_ariannaC_100Hz_stressors.txt";

                        CommandLineExecutor commandLineExecutor = new LinuxCliExecutor();
                        CommandLineResult cliResult = commandLineExecutor.executeCommand(command);
                        if(cliResult.getErrorLog() != null)
                            logger.info(cliResult.getErrorLog());
                        logger.info(cliResult.getOutputLog());
                    }

                    valueList.clear();
                    pipelineCache.addData(this, PIPELINE_CACHE_VALUE_LIST, valueList);
                    processingStepListener.onStepDone(this, Optional.of(new MqttPipelineData(String.format("%s/%s", data.getTopic(), "CSV"), data.getMqttTopicDescriptor(), String.valueOf(valueList).getBytes(), data.isRetained())));
                }
                else {
                    pipelineCache.addData(this, PIPELINE_CACHE_VALUE_LIST, valueList);
                    processingStepListener.onStepDone(this, Optional.of(new MqttPipelineData(String.format("%s/%s", data.getTopic(), "SenML"), data.getMqttTopicDescriptor(), String.valueOf(forwardSenmlData(valueList)).getBytes(), data.isRetained())));
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

    private boolean isCachedDataValid(ArrayList<EdaMessage> valueList) {
        logger.info("Validating data ...");

        // Check if we have the correct amount of samples with respect to the sampling frequency and the target time range
        // Check for missing values; If there are missing packets -> Fix with averaged values
        ArrayList<EdaMessage> ordered = ordering(valueList);
        logger.info("Ordering done. Proceeding with fixing values...");
        valueList = fixValues(ordered);
        logger.info("Fixing done. Proceeding with writing on .csv file...");
        return true;
    }

    private void writeData2CsvEda (String fileName, ArrayList<EdaMessage> valueList) {
        CsvFileWriter csvFileWriter = new CsvFileWriter(fileName);

        int lastValue = valueList.size();
        long milliseconds = Math.abs(valueList.get(0).getTimeS() - valueList.get(lastValue - 1).getTimeS());

        // csvFileWriter.writeTime(milliseconds);

        logger.info("Writing data {} to csv file ...", valueList.size());
        for (EdaMessage edaMessage : valueList) {
            csvFileWriter.writeToFile(edaMessage);
        }
        csvFileWriter.closeCsvFile();
    }

    private SenMLPack forwardSenmlData(ArrayList<EdaMessage> valueList) {
        SenMLPack senMLPack = new SenMLPack();
        for (EdaMessage edaMessage : valueList) {
            SenMLRecord senMLRecord = new SenMLRecord();
            senMLRecord.setN(edaMessage.getType());
            senMLRecord.setV(edaMessage.getValue());
            senMLRecord.setU("W");
            senMLRecord.setT(edaMessage.getTimeS());
            senMLPack.add(senMLRecord);
        }
        return senMLPack;
    }

    private ArrayList<EdaMessage> ordering (ArrayList<EdaMessage> valueList){
        int j = valueList.size() / 15;
        int k = 0;
        for(int i=0; i< j-1; i++) {
            valueList.subList(k, k + 15).sort(Comparator.comparing(EdaMessage::getSeqId));
            k = k + 15;
        }
        valueList.subList((valueList.size() - valueList.size() % 15), valueList.size()).sort(Comparator.comparing(EdaMessage::getSeqId));
        return valueList;
    }

    private ArrayList<EdaMessage> fixValues(ArrayList<EdaMessage> valueList) {
        for (int i = 0; i < valueList.size() - 1; i++) {
            if (valueList.get(i + 1).getSeqId() - valueList.get(i).getSeqId() > 1) {
                int missingSeqId = valueList.get(i).getSeqId() + 1;
                EdaMessage missing = new EdaMessage(valueList.get(1).getMacAddress(),
                        valueList.get(1).getType(),
                        missingSeqId,
                        valueList.get(1).getDig1(),
                        valueList.get(1).getDig2(),
                        valueList.get(1).getDig3(),
                        valueList.get(1).getDig4(),
                        valueList.get(1).getFrequency(),
                        (valueList.get(i + 1).getValue() +
                                valueList.get(i).getValue()) / 2,
                        (valueList.get(i + 1).getTimeS() +
                                valueList.get(i).getTimeS()) / 2
                );
                valueList.add(missingSeqId, missing);
            }
        }
        return valueList;
    }

}