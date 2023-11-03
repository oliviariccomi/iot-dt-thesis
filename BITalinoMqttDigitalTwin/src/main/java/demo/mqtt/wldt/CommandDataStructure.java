package demo.mqtt.wldt;

/**
 * Simple data structure to show how a WLDT ProcessingStep can adapt
 * the payload af an incoming MQTT payload into a new format/content
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project wldt-core
 * @created 10/03/2021 - 12:48
 */
public class CommandDataStructure {

    private String type = "demo_command_data_structure";

    private long timestamp;

    private String originalMessage;

    public CommandDataStructure(byte[] originalPayload) {
        this.timestamp = System.currentTimeMillis();
        this.originalMessage = new String(originalPayload);
    }

    public CommandDataStructure(String originalMessage) {
        this.timestamp = System.currentTimeMillis();
        this.originalMessage = originalMessage;
    }

    public CommandDataStructure(String type, long timestamp, String originalMessage) {
        this.type = type;
        this.timestamp = timestamp;
        this.originalMessage = originalMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DemoDataStructure{");
        sb.append("type='").append(type).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", originalMessage='").append(originalMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
