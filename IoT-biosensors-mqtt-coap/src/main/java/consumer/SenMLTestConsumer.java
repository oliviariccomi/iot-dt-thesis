package consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

//import static process.MqttSmartObjectProcess.MQTT_PASSWD;
//import static process.MqttSmartObjectProcess.MQTT_USERNAME;

/**
 *
 *  Simple MQTT Forwarder using the library Eclipse Paho
 *      - receives msg payload from publisher
 *      - deserialize it
 *      - creates SenML package
 *      - foreward to topic /BASE_TOPIC/SenML
 *
 */
public class SenMLTestConsumer {

    private final static Logger logger = LoggerFactory.getLogger(SenMLTestConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    private static final String ECG_TOPIC = "SenMLPackECG";
    private static final String EDA_TOPIC = "SenMLPackEDA";

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

            client.subscribe(ECG_TOPIC, (topic, msg) -> {
                try {
                    System.out.println(new String(msg.getPayload()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            /*client.subscribe(EDA_TOPIC, (topic, msg) -> {
                try {
                    System.out.println(new String(msg.getPayload()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
