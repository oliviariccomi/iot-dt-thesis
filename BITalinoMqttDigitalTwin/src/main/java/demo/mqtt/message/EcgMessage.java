package demo.mqtt.message;

public class EcgMessage extends SensorMessage{

    public EcgMessage() {
    }

    public EcgMessage(String macAddress, String type, int seqId, int dig1, int dig2, int dig3, int dig4, int frequency, Double value, Long timeS) {
        super(macAddress, type, seqId, dig1, dig2, dig3, dig4, frequency, value, timeS);
    }

    public Double EcgTransformedMessage (Double v) {
        return ((((v / (Math.pow(2, 10))) - 0.5) * 3.3) / 1100) * 1000;
    }
}
