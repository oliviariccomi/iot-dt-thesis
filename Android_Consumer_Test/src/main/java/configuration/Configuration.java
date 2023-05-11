package configuration;

public class Configuration {
    /**
     * -topic
     * -configuration
     * returns the right topic on the given configuration
     *         (if configuration = x return that specific topic
     */
    private String topic;
    private int signalColumn;

    public Configuration(int signalColumn) {
        this.signalColumn = signalColumn;
        if(signalColumn == 6)
            this.topic = "EMG";
        if(signalColumn == 7)
            this.topic = "ECG";
        if(signalColumn == 8)
            this.topic = "EDA";
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getSignalColumn() {
        return signalColumn;
    }

    public void setSignalColumn(int signalColumn) {
        this.signalColumn = signalColumn;
    }
}
