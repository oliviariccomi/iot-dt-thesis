package device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.EcgMessage;
import message.EdaMessage;
import message.SensorMessage;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.raw.*;

import java.util.Map;

public class MqttSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(MqttSmartObject.class);

    public static final String BASIC_TOPIC = "/iot/biosensors";

    private String boardId;

    // serve per serializzare e deserializzare
    private ObjectMapper mapper;

    private IMqttClient mqttClient;

    private Map<String, SensorResource<?>> resourceMap;

    boolean finished = false;



    public MqttSmartObject() {
        this.mapper = new ObjectMapper();
    }

    // Init the smartObject
    public void init(String boardId, IMqttClient mqttClient, Map<String, SensorResource<?>> resourceMap){
        this.boardId = boardId;
        this.mqttClient = mqttClient;
        this.resourceMap = resourceMap; // per prendere i parametri che mi interessano

        logger.info("Smart Object created. Resource number {}: ", resourceMap.keySet().size());
    }
    // Register to each available resource
    public void start() {
        try {
            // Control + registration to every available resource
            if(this.mqttClient != null && this.boardId != null && this.resourceMap != null && resourceMap.keySet().size() > 0){
                logger.info("Starting to send packets");
                registerToAvailableResources();

            }
        }catch(Exception e){
            logger.error("Error starting the Smart object. Mas: {}", e.getLocalizedMessage());
        }
    }

    private void registerToAvailableResources(){
        try{
            this.resourceMap.entrySet().forEach(resourceEntry -> {
                if(resourceEntry.getKey() != null && resourceEntry.getValue() != null) {
                    finished=true;
                    SensorResource sensorResource = resourceEntry.getValue();
                    logger.info("Registering to resource type {} with id {}",
                            sensorResource.getType(),
                            sensorResource.getMacAddress());

                    if(sensorResource.getType().equals(EcgCsvSensor.RESOURCE_TYPE)) {
                        EcgCsvSensor ecgCsvSensor = (EcgCsvSensor)sensorResource;
                        ecgCsvSensor.addDataListener(new ResourceDataListener() {
                            @Override
                            public void onDataChanged(SensorResource<SensorMessage> resource, SensorMessage updatedValue) throws InterruptedException {
                                try{
                                    EdaMessage edaMessage = new EdaMessage(
                                            updatedValue.getMacAddress(),
                                            updatedValue.getType(),
                                            updatedValue.getSeqId(),
                                            updatedValue.getDig1(),
                                            updatedValue.getDig2(),
                                            updatedValue.getDig3(),
                                            updatedValue.getDig4(),
                                            updatedValue.getFrequency(),
                                            updatedValue.getValue(),
                                            updatedValue.getTimeS()
                                    );
                                    publishTelemetryData(
                                            // TOPIC: /iot/biosensors/telemetry/ECG
                                            String.format("%s/%s/%s", BASIC_TOPIC, resource.getMacAddress(), resourceEntry.getKey()), edaMessage);

                                }catch (MqttException | JsonProcessingException | InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if(sensorResource.getType().equals(EdaCsvSensor.RESOURCE_TYPE)) {
                        EdaCsvSensor edaCsvSensor = (EdaCsvSensor)sensorResource;
                        edaCsvSensor.addDataListener(new ResourceDataListener() {
                            @Override
                            public void onDataChanged(SensorResource<SensorMessage> resource, SensorMessage updatedValue) throws InterruptedException {
                                try{
                                    EcgMessage ecgMessage = new EcgMessage(
                                            updatedValue.getMacAddress(),
                                            updatedValue.getType(),
                                            updatedValue.getSeqId(),
                                            updatedValue.getDig1(),
                                            updatedValue.getDig2(),
                                            updatedValue.getDig3(),
                                            updatedValue.getDig4(),
                                            updatedValue.getFrequency(),
                                            updatedValue.getValue(),
                                            updatedValue.getTimeS()
                                    );
                                    publishTelemetryData(
                                            // TOPIC: /iot/biosensors/telemetry/ECG
                                            String.format("%s/%s/%s", BASIC_TOPIC, resource.getMacAddress(), resourceEntry.getKey()), ecgMessage);
                                }catch (
                                        MqttException | JsonProcessingException | InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }

            });
        }catch(Exception e){
            logger.error("Error Registration to the available resources. Mas: {}", e.getLocalizedMessage());
        }
    }

    private void publishTelemetryData(String topic, SensorMessage sensorMessage) throws MqttException, JsonProcessingException, InterruptedException {
        Thread.sleep(1000);
        logger.info("Sending to topic: {} -> Data: {}", topic, sensorMessage);

        if (mqttClient != null &&
                this.mqttClient.isConnected() &&
                sensorMessage != null &&
                topic != null) {

            String messagePayload = mapper.writeValueAsString(sensorMessage);
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

    public void stop() throws MqttException {
        if(!this.finished) {
            mqttClient.disconnect();
            mqttClient.close();
        }
    }

}
