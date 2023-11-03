package test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.mqtt.message.EcgMessage;
import demo.mqtt.message.EdaMessage;
import demo.mqtt.wldt.augmentation.csvwriter.CsvFileWriter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    private static final String TARGET_TOPIC = "/iot/biosensors/88:6B:0F:F1:94:16/EDA";

    public static ObjectMapper mapper;

    public static ArrayList<EdaMessage> ECGlist;

    private static void writeData2Csv(String fileName, ArrayList<EdaMessage> valueList) {
        CsvFileWriter csvFileWriter = new CsvFileWriter(fileName);
        //logger.info("Writing data {} to csv file ...", valueList.size());
        for (EdaMessage edaMessage : valueList) {
            csvFileWriter.writeToFile(edaMessage);
        }
        csvFileWriter.closeCsvFile();
    }


    public static void main(String[] args) {

        logger.info("MQTT Consumer Tester Started ...");
        ECGlist = new ArrayList<>();
        try {
            String clientId = UUID.randomUUID().toString();

            MqttClientPersistence persistence = new MemoryPersistence();

            IMqttClient client = new MqttClient(
                    String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT), //Create the URL from IP and PORT
                    clientId,
                    persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);


            logger.info("Connected ! Client Id: {}", clientId);
            //CsvFileWriter csvFileWriter = new CsvFileWriter("nuovo");
            mapper = new ObjectMapper();
            client.subscribe(TARGET_TOPIC, (topic, msg) -> {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EdaMessage edaMessage = mapper.readValue(new String(msg.getPayload()), new TypeReference<EdaMessage>() {
                                });
                                ECGlist.add(edaMessage);
                                logger.info(String.valueOf(edaMessage));
                                logger.info("Cache size {} ", ECGlist.size());
                                if (ECGlist.size() == 4) {
                                    for (int i = 0; i < ECGlist.size() - 1; i++) {
                                        if (ECGlist.get(i + 1).getSeqId() - ECGlist.get(i).getSeqId() > 1) {
                                            int missingSeqId = ECGlist.get(i).getSeqId() + 1;
                                            EdaMessage missing = new EdaMessage(ECGlist.get(1).getMacAddress(),
                                                    ECGlist.get(1).getType(),
                                                    missingSeqId,
                                                    ECGlist.get(1).getDig1(),
                                                    ECGlist.get(1).getDig2(),
                                                    ECGlist.get(1).getDig3(),
                                                    ECGlist.get(1).getDig4(),
                                                    ECGlist.get(1).getFrequency(),
                                                    (ECGlist.get(i + 1).getValue() + ECGlist.get(i).getValue()) / 2,
                                                    (ECGlist.get(i + 1).getTimeS() + ECGlist.get(i).getTimeS()) / 2
                                            );
                                            ECGlist.add(missingSeqId, missing);
                                        }
                                        writeData2Csv("prova", ECGlist);
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /*
    for (int i = 0; i < ECGlist.size(); i++) {
                                        if (i != ECGlist.size() - 1) {
                                            if (ECGlist.get(i + 1).getSeqId() - ECGlist.get(i).getSeqId() > 1) {
                                                int missingSeqId = ECGlist.get(i).getSeqId() + 1;
                                                EcgMessage missing = new EcgMessage(ECGlist.get(1).getMacAddress(),
                                                        ECGlist.get(1).getType(),
                                                        missingSeqId,
                                                        ECGlist.get(1).getDig1(),
                                                        ECGlist.get(1).getDig2(),
                                                        ECGlist.get(1).getDig3(),
                                                        ECGlist.get(1).getDig4(),
                                                        ECGlist.get(1).getFrequency(),
                                                        (ECGlist.get(i + 1).getValue() + ECGlist.get(i).getValue()) / 2,
                                                        (ECGlist.get(i + 1).getTimeS() + ECGlist.get(i).getTimeS()) / 2
                                                );
                                                ECGlist.add(missingSeqId, missing);
                                            }
                                        }
                                        if (i == ECGlist.size() - 1) {
                                            int missingSeqId = ECGlist.get(i).getSeqId() + 1;
                                            EcgMessage missing = new EcgMessage(ECGlist.get(1).getMacAddress(),
                                                    ECGlist.get(1).getType(),
                                                    missingSeqId,
                                                    ECGlist.get(1).getDig1(),
                                                    ECGlist.get(1).getDig2(),
                                                    ECGlist.get(1).getDig3(),
                                                    ECGlist.get(1).getDig4(),
                                                    ECGlist.get(1).getFrequency(),
                                                    (ECGlist.get(i - 2).getValue() + ECGlist.get(i - 1).getValue()) / 2,
                                                    (ECGlist.get(i - 2).getTimeS() + ECGlist.get(i - 1).getTimeS()) / 2
                                            );
                                            ECGlist.add(missingSeqId, missing);
                                        }
                                    }
     */
}
