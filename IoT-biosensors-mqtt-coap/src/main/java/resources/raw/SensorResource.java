package resources.raw;

import message.SensorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class SensorResource<SensorMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SensorResource.class);

    private String fileName;

    private String macAddress;

    private Integer seqNumber;

    private Integer dig1;

    private Integer dig2;

    private Integer dig3;

    private Integer dig4;

    private Double value;

    private Long timeS;

    private String type;

    private Integer colNumber;

    private Integer frequency;

    private Integer fileSize;

    List<SensorsData> data;

    protected List<ResourceDataListener> resourceListenerList;


    public SensorResource(){
        this.resourceListenerList = new ArrayList<>();
    }

    public SensorResource(String filename, Integer column, Integer frequency, String type) {
        this.fileName = filename;
        this.resourceListenerList = new ArrayList<>();
        this.colNumber = column;
        this.frequency = frequency;
        this.type = type;

        try {

            BufferedReader cvsReader = new BufferedReader(new FileReader(filename));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = cvsReader.readLine()) != null) {
                lines.add(line);
            }
            //System.out.println(lines.get(0));

            for(int i = 1; i<lines.size(); i++ ) {
                String[] values = lines.get(0).split(";");
                macAddress = values[0];
                seqNumber = Integer.parseInt(values[1]);
                dig1 = Integer.parseInt(values[2]);
                dig2 = Integer.parseInt(values[3]);
                dig3 = Integer.parseInt(values[4]);
                dig4 = Integer.parseInt(values[5]);
                value = Double.parseDouble(values[colNumber]);
                timeS = Long.parseLong(values[12]);
            }
            this.fileSize = lines.size();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract message.SensorMessage loadUpdatedValues();

    public void addDataListener(ResourceDataListener resourceListenerList){
        if(this.resourceListenerList!=null )
            this.resourceListenerList.add(resourceListenerList);
    }

    public void removeDataListener(ResourceDataListener resourceDataListener){
        if(this.resourceListenerList != null && this.resourceListenerList.contains(resourceDataListener))
            this.resourceListenerList.remove(resourceDataListener);

    }

    protected void notifyUpdate(message.SensorMessage updatedValue){
        if(this.resourceListenerList != null && this.resourceListenerList.size() > 0)
            this.resourceListenerList.forEach(resourceDataListener -> {
                if(resourceDataListener != null) {
                    try {
                        resourceDataListener.onDataChanged((SensorResource<message.SensorMessage>) this, updatedValue);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        else
            logger.error("Empty of null resourceDataListener");
    }

    public List getSensorDataList() { return data; }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Integer seqNumber) {
        this.seqNumber = seqNumber;
    }

    public Integer getDig1() {
        return dig1;
    }

    public void setDig1(Integer dig1) {
        this.dig1 = dig1;
    }

    public Integer getDig2() {
        return dig2;
    }

    public void setDig2(Integer dig2) {
        this.dig2 = dig2;
    }

    public Integer getDig3() {
        return dig3;
    }

    public void setDig3(Integer dig3) {
        this.dig3 = dig3;
    }

    public Integer getDig4() {
        return dig4;
    }

    public void setDig4(Integer dig4) {
        this.dig4 = dig4;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getColNumber() {
        return colNumber;
    }

    public void setColNumber(Integer colNumber) {
        this.colNumber = colNumber;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize() {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "SensorResource{" +
                "fileName='" + fileName + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", seqNumber=" + seqNumber +
                ", dig1=" + dig1 +
                ", dig2=" + dig2 +
                ", dig3=" + dig3 +
                ", dig4=" + dig4 +
                ", value=" + value +
                ", timeS=" + timeS +
                ", type='" + type + '\'' +
                ", colNumber=" + colNumber +
                ", frequency=" + frequency +
                ", fileSize=" + fileSize +
                ", data=" + data +
                ", resourceListenerList=" + resourceListenerList +
                '}';
    }
}
