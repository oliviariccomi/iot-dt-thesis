package message;

public class EcgMessage extends SensorMessage{

    private Double value;

    public EcgMessage() {
    }

    public EcgMessage(String macAddress, String type, int seqId, int dig1, int dig2, int dig3, int dig4, int frequency, Double value, Long timeS) {
        super(macAddress, type, seqId, dig1, dig2, dig3, dig4, frequency, value, timeS);
        this.value = ((((value / (Math.pow(2, 10))) - 0.5) * 3.3) / 1100) * 1000;
    }
}
