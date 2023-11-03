package demo.mqtt.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.mqtt.message.EcgMessage;
import demo.mqtt.message.EdaMessage;
import demo.mqtt.message.SensorMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

//import static process.MqttSmartObjectProcess.MQTT_PASSWD;
//import static process.MqttSmartObjectProcess.MQTT_USERNAME;

/**
 *
 *  Simple MQTT Forwarder:
 *      - receives msg payload from publisher
 *      - deserialize it
 *      - creates SensorMessage
 *      - publish on the right topic
 *
 */
public class SimpleMqttForewarder {

    private final static Logger logger = LoggerFactory.getLogger(SimpleMqttForewarder.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    private static final String BASIC_TOPIC = "/iot/biosensors/88:6B:0F:F1:94:16/";
    private static final String TARGET_TOPIC = "/iot/biosensors/88:6B:0F:F1:94:16/#";
    private static final String EDA_TOPIC = "/iot/biosensors/88:6B:0F:F1:94:16/EdaMessage";
    private static final String ECG_TOPIC = "/iot/biosensors/88:6B:0F:F1:94:16/EcgMessage";

    public static ObjectMapper mapper;

    public static void main(String[] args) {

        logger.info("MQTT Consumer Tester Started ...");

        try {

            //Generate a random MQTT client ID using the UUID class
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

            mapper = new ObjectMapper();
            client.subscribe(TARGET_TOPIC, (topic, msg) -> {
                try {
                    if (topic.contains("EDA")) {
                        //System.out.println(new String(msg.getPayload()));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EdaMessage edaMessage = mapper.readValue(new String(msg.getPayload()), new TypeReference<EdaMessage>() {
                                    });
                                    logger.info(String.valueOf(edaMessage));
                                    publishSensorMessage(client, EDA_TOPIC, edaMessage);

                                } catch (MqttException | InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    if (topic.contains("ECG")) {
                        //System.out.println(new String(msg.getPayload()));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EcgMessage ecgMessage = mapper.readValue(new String(msg.getPayload()), new TypeReference<EcgMessage>() {
                                    });
                                    publishSensorMessage(client, ECG_TOPIC, ecgMessage);

                                } catch (MqttException | InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void publishSensorMessage(IMqttClient mqttClient, String topic, SensorMessage sensorMessage) throws MqttException, JsonProcessingException, InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    logger.info("Sending to topic: {} -> Data: {}", topic, mapper.writeValueAsString(sensorMessage));

                    if(mqttClient != null && mqttClient.isConnected() && sensorMessage != null && topic != null){

                        String messagePayload = mapper.writeValueAsString(sensorMessage);

                        MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                        mqttMessage.setQos(0);

                        mqttClient.publish(topic, mqttMessage);

                        logger.info("Data Correctly Published to topic: {}", topic);

                    }
                    else
                        logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
