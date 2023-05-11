package bitalinoConsumer;

import java.util.Arrays;

public class BITalinoFrame {
    private String identifier;
    private int seq;
    private int[] analog = new int[6];
    private int[] digital = new int[4];
    private long timestamp = System.currentTimeMillis() / 1000;

    public BITalinoFrame () {
    }

    public BITalinoFrame(String identifier, int seq, int[] analog, int[] digital) {
        this.identifier = identifier;
        this.seq = seq;
        this.analog = analog;
        this.digital = digital;
    }

    public BITalinoFrame(String identifier) {
        this.identifier = identifier;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int[] getAnalog() {
        return analog;
    }

    public void setAnalog(int[] analog) {
        this.analog = analog;
    }

    public int[] getDigital() {
        return digital;
    }

    public void setDigital(int[] digital) {
        this.digital = digital;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BITalinoFrame{" +
                "identifier='" + identifier + '\'' +
                ", seq=" + seq +
                ", analog=" + Arrays.toString(analog) +
                ", digital=" + Arrays.toString(digital) +
                ", timestamp=" + timestamp +
                '}';
    }
}
