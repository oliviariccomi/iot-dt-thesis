import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bitalinoConsumer.BITFrame;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RandomDataGenerator.class);
    // "analog":[0,1022,3,0,0,0],"identifier":"88:6B:0F:F1:94:16","digital":[1,1,0,0],"seq":1,
    private BITFrame biTalinoFrame;
    private static String BROKER_ADDRESS = "127.0.0.1";
    private static String TARGET_TOPIC = "/iot/user/254439";
    private static int BROKER_PORT = 1883;
    private static String USERNAME = "254439";
    private static String PASSWORD = "gmhvccmp";
    private static ObjectMapper mapper;
    private int[] analog = new int[6];
    private int[] digital = new int[4];
    int sequence;


    //PORT of the target MQTT Broker



    public RandomDataGenerator() {
        biTalinoFrame = new BITFrame("88:6B:0F:F1:94:16");
        sequence++;
        init();
        biTalinoFrame.setSeq(sequence);

    }

    private void init(){

        try{
            for(int i=0;i<6;i++)
            {
                analog[i] = ThreadLocalRandom.current().nextInt(1, 800 + 1);
                biTalinoFrame.setAnalog(analog);
            }
            for(int i=0;i<4;i++)
            {
                digital[i] = ThreadLocalRandom.current().nextInt(0, 1 + 1);
                biTalinoFrame.setDigital(digital);
            }

        }catch (Exception e){
            logger.error("Error init Resource Object ! Msg: {}", e.getLocalizedMessage());
        }

    }

    public BITFrame getBiTalinoFrame() {
        return biTalinoFrame;
    }

    public void setBiTalinoFrame(BITFrame biTalinoFrame) {
        this.biTalinoFrame = biTalinoFrame;
    }

    @Override
    public String toString() {
        return "RandomDataGenerator{" +
                "biTalinoFrame=" + biTalinoFrame +
                '}';
    }

    private static void publishMessage (IMqttClient mqttClient, String topic, BITFrame biTalinoFrame) throws MqttException, JsonProcessingException, InterruptedException {

        Thread.sleep(1000);
        logger.info("Sending to topic: {} -> Data: {}", topic, biTalinoFrame);

        if (mqttClient != null &&
                mqttClient.isConnected() &&
                biTalinoFrame != null &&
                topic != null) {
            mapper = new ObjectMapper();
            String messagePayload = mapper.writeValueAsString(biTalinoFrame);
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

    public static void main(String[] args) throws MqttException, JsonProcessingException, InterruptedException {

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
        for(int i=0; i<70; i++) {
            Thread.sleep(1000);
            //RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
            //logger.info(randomDataGenerator.getBiTalinoFrame().toString());
            //publishMessage(client, TARGET_TOPIC, randomDataGenerator.getBiTalinoFrame());
            publish(client, TARGET_TOPIC, "ciao");

        }

    }
}
