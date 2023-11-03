package demo.mqtt.wldt.augmentation.csvwriter;

import demo.mqtt.message.EcgMessage;
import demo.mqtt.message.SensorMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CsvFileWriter {

    private FileWriter csvWriter;
    private String separator = "\t";
    private String lineSeparator = "\n";
    private final static String FILE_PATH = "/Users/olivia1/Desktop/";

    // Definition/Initialisation of class constants
    private final String filePrefix = "BITalino_record_";
    private final String fileExtension = ".csv";
    private final String macAddress = "88:6B:0F:F1:94:16";
    private final int nbrFixedCols = 5;

    public CsvFileWriter() {
    }

    public CsvFileWriter(String filename) {

        // Initialisation of OpenCSV writer object.
        try {
            csvWriter = new FileWriter(FILE_PATH + File.separator + filename + fileExtension);
            csvWriter.write("MacAddress" + separator +
                    "Seq Id" + separator +
                    "Dig 1" + separator +
                    "Dig 2" + separator +
                    "Dig 3" + separator +
                    "Dig 4" + separator +
                    "Value" + separator +
                    "Frequency" + separator +
                    "Timestamp" + lineSeparator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTime(long milliseconds) {
        try {
            csvWriter.write("Experiement total timing (ms): " + milliseconds + lineSeparator);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public void writeToFile(SensorMessage sensorMessage){
        try {
            // [Sequence number - which reboots every 15 samples]
            csvWriter.write(sensorMessage.getMacAddress() + separator);
            csvWriter.write(sensorMessage.getSeqId() + separator);
            csvWriter.write(sensorMessage.getDig1() + separator); // I1
            csvWriter.write(sensorMessage.getDig2() + separator); // I2
            csvWriter.write(sensorMessage.getDig3() + separator); // O1
            csvWriter.write(sensorMessage.getDig4() + separator); // O2
            csvWriter.write(sensorMessage.getValue() + separator);
            csvWriter.write(sensorMessage.getFrequency() + separator);
            csvWriter.write(sensorMessage.getTimeS() + lineSeparator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*public void writeToFile(String ecgMessage) {
        try {
            // [Sequence number - which reboots every 15 samples]
            csvWriter.write(ecgMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void closeCsvFile() {
        try {
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
