package resources.raw;

import message.SensorMessage;

public interface ResourceDataListener {

    public void onDataChanged(SensorResource<SensorMessage> resource, SensorMessage updatedValue) throws InterruptedException;
}
