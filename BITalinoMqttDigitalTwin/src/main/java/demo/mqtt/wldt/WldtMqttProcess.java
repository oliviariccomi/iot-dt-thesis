package demo.mqtt.wldt;

import demo.mqtt.wldt.processingsteps.MqttECGProcessingStep;
import demo.mqtt.wldt.processingsteps.MqttEDAProcessingStep;
//import demo.mqtt.wldt.processingsteps.processingstepprova;
import it.unimore.dipi.iot.wldt.engine.WldtConfiguration;
import it.unimore.dipi.iot.wldt.engine.WldtEngine;
import it.unimore.dipi.iot.wldt.processing.ProcessingPipeline;
import it.unimore.dipi.iot.wldt.processing.step.IdentityProcessingStep;
import it.unimore.dipi.iot.wldt.worker.MirroringListener;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttConfiguration;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttWorker;
import it.unimore.dipi.iot.wldt.worker.mqtt.MqttTopicDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * Demo of a WLDT enabled Digital Twin that mirrors an MQTT IoT Device
 * received telemetry data from the physical asset and command from
 * external applications according to the following schema:
 *
 * Telemetry:
 *
 * DEVICE ---- [msg] ----> BROKER-A ----> (DT) ---- [msg] ---- BROKER-B ----> CONSUMER(s)
 *                       (Port:1883)                          (Port:1884)
 *
 * Commands:
 *
 * DEVICE <---- [msg] ---- BROKER-A <---- (DT) <---- [msg] ---- BROKER-B <---- APP(s)
 *                        (Port:1883)                          (Port:1884)
 *
 * @author : Marco Picone, Ph.D. (marco.picone@unimore.it)
 * @created: 21/05/2021
 * @project: WLDT - MQTT Example
 */
public class WldtMqttProcess {

    private static final String TAG = "[WLDT-Process]";

    private static final Logger logger = LoggerFactory.getLogger(WldtMqttProcess.class);

    private static final String DEMO_ECG_TOPIC_ID = "ECG_topic";
    private static final String DEMO_ECG_RESOURCE_ID = "ECG";

    private static final String DEMO_EDA_TOPIC_ID = "EDA_topic";
    private static final String DEMO_EDA_RESOURCE_ID = "EDA";

    private static final String SOURCE_BROKER_ADDRESS = "127.0.0.1";
    private static final int SOURCE_BROKER_PORT = 1883;

    private static final String DESTINATION_BROKER_ADDRESS = "127.0.0.1";
    private static final int DESTINATION_BROKER_PORT = 1883;

    private static final String DEVICE_ID = "BITalinoBoard";

    public static void main(String[] args)  {

        try{

            logger.info("{} Initializing WLDT-Engine ... ", TAG);

            //Example loading everything from the configuration file
            //WldtEngine wldtEngine = new WldtEngine();
            //wldtEngine.startWorkers();

            //Manual creation of the WldtConfiguration
            WldtConfiguration wldtConfiguration = new WldtConfiguration();
            wldtConfiguration.setDeviceNameSpace("it.unimore.dipi.things");
            wldtConfiguration.setWldtBaseIdentifier("wldt");
            wldtConfiguration.setWldtStartupTimeSeconds(10);
            wldtConfiguration.setApplicationMetricsEnabled(false);

            WldtEngine wldtEngine = new WldtEngine(wldtConfiguration);

            Mqtt2MqttWorker mqtt2MqttWorker = new Mqtt2MqttWorker(
                    wldtEngine.getWldtId(), getMqttComplexProtocolConfiguration());

            //Add Processing Pipeline for target topics
            mqtt2MqttWorker.addTopicProcessingPipeline(DEMO_ECG_TOPIC_ID,
                    new ProcessingPipeline(
                            new IdentityProcessingStep(),
                            new MqttECGProcessingStep(),
                            new MqttTopicChangeStep()
                    )
            );

            mqtt2MqttWorker.addTopicProcessingPipeline(DEMO_EDA_TOPIC_ID,
                    new ProcessingPipeline(
                            new IdentityProcessingStep(),
                            new MqttEDAProcessingStep(),
                            new MqttTopicChangeStep()
                    )
            );

            /*mqtt2MqttWorker.addTopicProcessingPipeline(DEMO_COMMAND_TOPIC_ID,
                    new ProcessingPipeline(
                            new IdentityProcessingStep(),
                            new MqttPayloadChangeStep(),
                            new MqttCommandTopicChangeStep()
                    )
            );*/

            //Add Mirroring Listener
            mqtt2MqttWorker.addMirroringListener(new MirroringListener() {
                @Override
                public void onDeviceMirrored(String deviceId, Map<String, Object> metadata) {
                    logger.info("onDeviceMirrored() callback ! DeviceId: {} -> Metadata: {}",
                            deviceId, metadata);
                }
                @Override
                public void onDeviceMirroringError(String deviceId, String errorMsg) {
                    logger.info("onDeviceMirroringError() callback ! DeviceId: {} -> ErrorMsg: {}",
                            deviceId, errorMsg);
                }
                @Override
                public void onResourceMirrored(String resourceId, Map<String, Object> metadata) {
                    logger.info("onResourceMirrored() callback ! ResourceId: {} -> Metadata: {}",
                            resourceId, metadata);
                }
                @Override
                public void onResourceMirroringError(String resourceId, String errorMsg) {
                    logger.info("onResourceMirroringError() callback ! ResourceId: {} -> ErrorMsg: {}",
                            resourceId, errorMsg);
                }

            });

            wldtEngine.addNewWorker(mqtt2MqttWorker);
            wldtEngine.startWorkers();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Mqtt2MqttConfiguration getMqttComplexProtocolConfiguration(){

        //Configuration associated to the MQTT experimental use-case available in the dedicated project
        //Demo Telemetry topic -> telemetry/com:iot:dummy:dummyMqttDevice001/resource/dummy_string_resource

        Mqtt2MqttConfiguration mqtt2MqttConfiguration = new Mqtt2MqttConfiguration();

        mqtt2MqttConfiguration.setDtPublishingQoS(0); //QoS del broker in uscita verso gli oggetti digitali
        mqtt2MqttConfiguration.setBrokerAddress(SOURCE_BROKER_ADDRESS); // del broker di desinazione
        mqtt2MqttConfiguration.setBrokerPort(SOURCE_BROKER_PORT);
        mqtt2MqttConfiguration.setDestinationBrokerAddress(DESTINATION_BROKER_ADDRESS);
        mqtt2MqttConfiguration.setDestinationBrokerPort(DESTINATION_BROKER_PORT);
        mqtt2MqttConfiguration.setDeviceId(DEVICE_ID); // automaticamente il worker può utilizzare id devide e id risorse per costruire i topic (si specifica l'id dell'oggetto a cui è riferito il dt)
        //If Required Specify the ClientId
        mqtt2MqttConfiguration.setBrokerClientId("physicalBrokerTestClientId");
        mqtt2MqttConfiguration.setDestinationBrokerClientId("digitalBrokerTestClientId");


        //Specify Topic List Configuration
        mqtt2MqttConfiguration.setTopicList(
                Arrays.asList(
                        new MqttTopicDescriptor(DEMO_ECG_TOPIC_ID,
                                DEMO_ECG_RESOURCE_ID,
                                "/iot/biosensors/88:6B:0F:F1:94:16/ECG",
                                MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_OUTGOING),
                        new MqttTopicDescriptor(DEMO_EDA_TOPIC_ID,
                                DEMO_EDA_RESOURCE_ID,
                                "/iot/biosensors/88:6B:0F:F1:94:16/EDA",
                                MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_OUTGOING)
                )
        );

        return mqtt2MqttConfiguration;
    }

}

