package demo.mqtt.resources.raw;

import demo.mqtt.message.SensorMessage;

public interface ResourceDataListener {

    public void onDataChanged(SensorResource<SensorMessage> resource, SensorMessage updatedValue) throws InterruptedException;
}
