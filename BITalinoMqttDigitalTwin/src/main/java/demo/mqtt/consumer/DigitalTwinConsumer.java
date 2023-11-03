package demo.mqtt.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.mqtt.message.EdaMessage;
import demo.mqtt.message.SenMLMessage;
import demo.mqtt.message.SensorMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class DigitalTwinConsumer {
    private final static Logger logger = LoggerFactory.getLogger(DigitalTwinConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "127.0.0.1";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 1883;

    private static final String TARGET_TOPIC = "pipeline/iot/biosensors/88:6B:0F:F1:94:16/ECG/SenML";

    public static ObjectMapper mapper;

    public static void main(String[] args) {

        logger.info("MQTT Consumer Tester Started ...");

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

            mapper = new ObjectMapper();
            client.subscribe(TARGET_TOPIC, (topic, msg) -> {
                try {
                        System.out.println(new String(msg.getPayload()));
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SenMLMessage senMLMessage = mapper.readValue(new String(msg.getPayload()), new TypeReference<SenMLMessage>() {
                                    });
                                    logger.info(String.valueOf(senMLMessage));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
