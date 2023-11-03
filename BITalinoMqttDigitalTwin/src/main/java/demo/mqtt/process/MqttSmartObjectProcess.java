package demo.mqtt.process;

/*
        TESTING MQTT SMART OBJECT
 */

import demo.mqtt.device.MqttSmartObject;
import demo.mqtt.resources.raw.EcgCsvSensor;
import demo.mqtt.resources.raw.EdaCsvSensor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class MqttSmartObjectProcess {

    private static final Logger logger = LoggerFactory.getLogger(MqttSmartObjectProcess.class);

    private static String MQTT_BROKER_IP = "127.0.0.1";

    private static int MQTT_BROKER_PORT = 1883;

    //public static String MQTT_USERNAME = "254439";

    //public static String MQTT_PASSWD = "gmhvccmp";

    public static void main (String[] args){


        try{
            // Random boardID to test
            String boardID = UUID.randomUUID().toString();

            // Create MQTT Client
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d",
                    MQTT_BROKER_IP,
                    MQTT_BROKER_PORT),
                    boardID,
                    persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            //options.setUserName(MQTT_USERNAME);
            //options.setPassword(MQTT_PASSWD.toCharArray());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(100);


            // Connect
            mqttClient.connect(options);

            logger.info("MQTT Client Connected with ID {}: ", boardID);
            MqttSmartObject mqttSmartObject = new MqttSmartObject();
            mqttSmartObject.init(boardID, mqttClient, new HashMap<>(){
                {
                    put("ECG", new EcgCsvSensor("/Users/olivia1/Desktop/thesis-management-master/BITalinoMqttDigitalTwin/src/main/java/demo/data/examples/ECG_file.csv"));
                    put("EDA", new EdaCsvSensor("/Users/olivia1/Desktop/thesis-management-master/BITalinoMqttDigitalTwin/src/main/java/demo/data/examples/EDA_file.csv"));
                }
            });

            mqttSmartObject.start();
            mqttSmartObject.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
