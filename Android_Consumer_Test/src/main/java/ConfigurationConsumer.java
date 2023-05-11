
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.BitSet;
import java.util.UUID;

/**
 * EMG: 6th column
 * ECG: 7th column
 * EDA: 8th column
 *
 */

public class ConfigurationConsumer {
    /**
     * subscribe to the general topic and then
     * publish the data to the confuguration given topic
     */


    /**private final static Logger logger = LoggerFactory.getLogger(AndroidTestConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    private static final String TARGET_TOPIC = "bitalino/generator";


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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BITalinoFrame biTalinoFrame = mapper.readValue(new String(msg.getPayload()), new TypeReference<BITalinoFrame>() {
                            });
                            //logger.info(String.valueOf(arrayList));

                            // Changing configuration based on signal
                            Configuration configuration = new Configuration(6);
                            publishConfiguration(client, configuration.getTopic(), biTalinoFrame);

                        } catch (MqttException | InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            });
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    private static void publishConfiguration (IMqttClient mqttClient, String topic, BITalinoFrame biTalinoFrame) throws MqttException, JsonProcessingException, InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    logger.info("Sending to topic: {} -> Data: {}", topic, biTalinoFrame);

                    if(mqttClient != null && mqttClient.isConnected() && biTalinoFrame != null && topic != null){

                        String messagePayload = mapper.writeValueAsString(biTalinoFrame);

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
    }*/

}
