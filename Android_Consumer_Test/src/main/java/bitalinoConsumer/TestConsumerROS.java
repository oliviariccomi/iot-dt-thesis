package bitalinoConsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class TestConsumerROS {

    private final static Logger logger = LoggerFactory.getLogger(TestConsumerROS.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "155.185.228.19";
    private static String USERNAME = "254439";
    private static String PASSWORD = "gmhvccmp";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 7883;

    private static final String TARGET_TOPIC = "/iot/user/254439";


    public static ObjectMapper mapper;

    public static void main(String[] args) throws MqttException {

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
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);


            logger.info("Connected ! Client Id: {}", clientId);
            mapper = new ObjectMapper();
            client.subscribe(TARGET_TOPIC, (topic, msg) -> {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logger.info(msg.toString());
                        try {
                            String value = mapper.readValue(new String(msg.getPayload()), new TypeReference<String>() {
                            });
                            logger.info("Message Received -> Topic: {} - Payload: {}", topic, value);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            });

            /*for(int i=0; i<70; i++) {
                Thread.sleep(1000);
                //RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
                //logger.info(randomDataGenerator.getBiTalinoFrame().toString());
                //publishMessage(client, TARGET_TOPIC, randomDataGenerator.getBiTalinoFrame());
                publish(client, TARGET_TOPIC, "ciao");

            }*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void publish (IMqttClient mqttClient, String topic, String number) throws MqttException, JsonProcessingException, InterruptedException {

        Thread.sleep(1000);
        logger.info("Sending to topic: {} -> Data: {}", topic, number);

        if (mqttClient != null &&
                mqttClient.isConnected() &&
                topic != null) {
            mapper = new ObjectMapper();
            String messagePayload = mapper.writeValueAsString(number);
            //System.out.println(messagePayload);
            MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
            mqttMessage.setQos(0);
            mqttClient.publish(topic, mqttMessage);

            logger.info("Data Correctly Published to topic: {}", topic);
        }
        else {
            logger.error("Error: Topic or Msg = Null or MQTT CLient is not Connected !");
        }
    }
}
