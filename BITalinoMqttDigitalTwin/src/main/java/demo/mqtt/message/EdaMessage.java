package demo.mqtt.message;

public class EdaMessage extends SensorMessage{

    public EdaMessage() {
    }

    public EdaMessage(String macAddress, String type, int seqId, int dig1, int dig2, int dig3, int dig4, int frequency, Double value, Long timeS) {
        super(macAddress, type, seqId, dig1, dig2, dig3, dig4, frequency, value, timeS);
    }

    public Double EdaTransformedMessage (Double v) {
        return (((v / (Math.pow(2, 10))) * 3.3) / 0.132) * Math.pow(10,-6);
    }
}
