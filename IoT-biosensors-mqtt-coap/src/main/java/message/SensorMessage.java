package message;

public abstract class SensorMessage {
    private String macAddress, type;

    private int seqId, dig1, dig2, dig3, dig4, frequency;

    private Double value;

    private Long timeS;

    public SensorMessage() {
    }

    public SensorMessage(String macAddress, String type, int seqId, int dig1, int dig2, int dig3, int dig4, int frequency, Double value, Long timeS) {
        this.macAddress = macAddress;
        this.type = type;
        this.seqId = seqId;
        this.dig1 = dig1;
        this.dig2 = dig2;
        this.dig3 = dig3;
        this.dig4 = dig4;
        this.frequency = frequency;
        this.value = value;
        this.timeS = timeS;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

    public int getDig1() {
        return dig1;
    }

    public void setDig1(int dig1) {
        this.dig1 = dig1;
    }

    public int getDig2() {
        return dig2;
    }

    public void setDig2(int dig2) {
        this.dig2 = dig2;
    }

    public int getDig3() {
        return dig3;
    }

    public void setDig3(int dig3) {
        this.dig3 = dig3;
    }

    public int getDig4() {
        return dig4;
    }

    public void setDig4(int dig4) {
        this.dig4 = dig4;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getTimeS() {
        return timeS;
    }

    public void setTimeS(Long timeS) {
        this.timeS = timeS;
    }

    @Override
    public String toString() {
        return "SensorMessage{" +
                "macAddress='" + macAddress + '\'' +
                ", type='" + type + '\'' +
                ", seqId=" + seqId +
                ", dig1=" + dig1 +
                ", dig2=" + dig2 +
                ", dig3=" + dig3 +
                ", dig4=" + dig4 +
                ", frequency=" + frequency +
                ", value=" + value +
                ", timeS=" + timeS +
                '}';
    }
}
