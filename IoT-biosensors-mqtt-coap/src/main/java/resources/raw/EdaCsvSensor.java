package resources.raw;
import message.EdaMessage;
import message.SensorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class EdaCsvSensor extends SensorResource<EdaMessage> {

    private static final Logger logger = LoggerFactory.getLogger(EdaCsvSensor.class);

    public static final String FILE_EDA = "/Users/olivia1/Desktop/EDA_prova.csv";

    public static final String RESOURCE_TYPE = "EDA";

    public static final Integer FREQUENCY = 1;

    private static final Integer COL_NUMBER = 8;

    public static final long UPDATE_PERIOD = FREQUENCY*1000; // QUA METTERE IN BASE ALLA FREQUENZA

    private String filename;

    private Timer updateTimer = null;

    private ListIterator<String> dataListIterator;

    private EdaMessage updateEdaMessage = null;;

    ArrayList<String> lines = new ArrayList<>();

    public Integer count = 0;


    public EdaCsvSensor() {
    }

    public EdaCsvSensor(String filename) {
        super(filename, COL_NUMBER, FREQUENCY, RESOURCE_TYPE);
        this.filename = filename;
        init();
    }

    private void init() {

        try {

            this.updateEdaMessage = new EdaMessage();

            BufferedReader cvsReader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = cvsReader.readLine()) != null) {
                lines.add(line);

            }
            this.dataListIterator = this.lines.listIterator();
            startPeriodicEventValueUpdateTask();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startPeriodicEventValueUpdateTask() {

        try {
            logger.info("Starting periodic Update Task with Period: {} ms", UPDATE_PERIOD);
            this.updateTimer = new Timer();
            this.updateTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    count++;
                    if (count<lines.size()) {
                        for (String line : lines) {
                            ArrayList<String[]> newValues = new ArrayList<>();
                            newValues.add(line.split(";"));
                            for (int i = 0; i < newValues.size(); i++) {
                                updateEdaMessage = new EdaMessage((newValues.get(i)[0]),
                                        RESOURCE_TYPE,
                                        Integer.parseInt(newValues.get(i)[1]),
                                        Integer.parseInt(newValues.get(i)[2]),
                                        Integer.parseInt(newValues.get(i)[3]),
                                        Integer.parseInt(newValues.get(i)[4]),
                                        Integer.parseInt(newValues.get(i)[5]),
                                        FREQUENCY,
                                        Double.parseDouble(newValues.get(i)[COL_NUMBER]),
                                        Long.parseLong(newValues.get(i)[12]));
                            }
                            notifyUpdate(updateEdaMessage);
                        }
                        //logger.info("EDADESCR:"+(updateEdaDescriptor));
                    }
                    updateTimer.cancel();
                    //logger.info("All {} packets received !", lines.size());
                    logger.info("All {} packets sent !", lines.size());
                }
            }, UPDATE_PERIOD, UPDATE_PERIOD);

        } catch (Exception e) {
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }


    }


    public EdaMessage loadUpdatedValues() {
        return this.updateEdaMessage;
    }


    public static void main(String[] args){
        EdaCsvSensor edaCsvSensor = new EdaCsvSensor(FILE_EDA);
        edaCsvSensor.addDataListener(new ResourceDataListener() {
            @Override
            public void onDataChanged(SensorResource<SensorMessage> resource, SensorMessage updatedValue) throws InterruptedException {
                if(resource != null && updatedValue != null)
                    try {
                        Thread.sleep(1000);
                        logger.info("Device with MacAddress: {} --> New Value Recived: {}", resource.getMacAddress(), updatedValue.getValue());
                    } catch (InterruptedException e) {

                    }
                else
                    logger.error("onDataChangedCallback --> Null Resource or Update Value");

            }

        });
    }

}
