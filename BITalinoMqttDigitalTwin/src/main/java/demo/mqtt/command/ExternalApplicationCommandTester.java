package demo.mqtt.command;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


/**
 * Emulated external application sending a command to the WLDT Digital Twin
 *
 * @author : Marco Picone, Ph.D. (marco.picone@unimore.it)
 * @created: 21/05/2021
 * @project: WLDT - MQTT Example
 */
public class ExternalApplicationCommandTester {

    private final static Logger logger = LoggerFactory.getLogger(ExternalApplicationCommandTester.class);

    //BROKER URL
    private static String BROKER_URL = "tcp://127.0.0.1:1884";

    private static final String COMMAND_TOPIC = "command/com:iot:dummy:dummyMqttDevice001";

    public static void main(String[] args) {

        logger.info("SimpleProducer started ...");

        try{

            String mqttClientId = UUID.randomUUID().toString();
            MqttClientPersistence persistence = new MemoryPersistence();
            IMqttClient client = new MqttClient(BROKER_URL,mqttClientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);

            logger.info("Connected ! Client Id: {}", mqttClientId);

            String demoCommand = "REBOOT_DEVICE";

            publishData(client, COMMAND_TOPIC, demoCommand);

            logger.info("Command {} correctly sent to: {}", demoCommand, COMMAND_TOPIC);

            //Disconnect from the broker and close the connection
            client.disconnect();
            client.close();

            logger.info("Disconnected !");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Send a target String Payload to the specified MQTT topic
     *
     * @param mqttClient
     * @param topic
     * @param msgString
     * @throws MqttException
     */
    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

        logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

        if (mqttClient.isConnected() && msgString != null && topic != null) {

            //Create an MQTT Message defining the required QoS Level and if the message is retained or not
            MqttMessage msg = new MqttMessage(msgString.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            mqttClient.publish(topic,msg);

            logger.info("Data Correctly Published to Topic: {} Data: {}", topic, msgString);
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }



}
