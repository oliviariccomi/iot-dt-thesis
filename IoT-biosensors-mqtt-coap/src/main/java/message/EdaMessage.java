package message;

public class EdaMessage extends SensorMessage{

    Double value;

    public EdaMessage() {
    }

    public EdaMessage(String macAddress, String type, int seqId, int dig1, int dig2, int dig3, int dig4, int frequency, Double value, Long timeS) {
        super(macAddress, type, seqId, dig1, dig2, dig3, dig4, frequency, value, timeS);
        this.value = (((value / (Math.pow(2, 10))) * 3.3) / 0.132) * Math.pow(10,-6);
    }
}
