package bitalinoConsumer;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AndroidTestConsumer {

    private final static Logger logger = LoggerFactory.getLogger(AndroidTestConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    private static final String TARGET_TOPIC = "bitalino/sensors";


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
                        //BITalinoFrame biTalinoFrame = mapper.readValue(new String(msg.getPayload()), new TypeReference<BITalinoFrame>() {
                        //});
                        //logger.info("Message Received -> Topic: {} - Payload: {}", topic, biTalinoFrame);
                    }
                }).start();
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
