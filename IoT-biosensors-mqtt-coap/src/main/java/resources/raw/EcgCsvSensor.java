package resources.raw;


import message.EcgMessage;
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

public class EcgCsvSensor extends SensorResource<EcgMessage> {

    private static final Logger logger = LoggerFactory.getLogger(EcgCsvSensor.class);

    public static final String FILE_ECG = "/Users/olivia1/Desktop/ECG_file.csv";

    public static final String RESOURCE_TYPE = "ECG";

    public static final Integer FREQUENCY = 10;

    private static final Integer COL_NUMBER = 7;

    public static final long UPDATE_PERIOD = FREQUENCY*1000; //5 Seconds

    private String filename;

    private Timer updateTimer = null;

    private ListIterator<String> dataListIterator;

    private EcgMessage updateEcgMessage = null;

    ArrayList<String> lines = new ArrayList<>();

    public static Integer count = 0;

    public EcgCsvSensor() {
        this.filename = FILE_ECG;
        init();
    }

    public EcgCsvSensor(String filename){
        super(filename, COL_NUMBER, FREQUENCY, RESOURCE_TYPE);
        this.filename = filename;
        init();
    }

    private void init () {

        try {
            this.updateEcgMessage = new EcgMessage();

            BufferedReader cvsReader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = cvsReader.readLine()) != null) {
                lines.add(line);
            }
            logger.info("File correctly loaded! Size: {}", this.lines.size());

            this.dataListIterator = this.lines.listIterator();

            startPeriodicEventValueUpdateTask();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startPeriodicEventValueUpdateTask() {

        try {
            //updatedEdaValue = updatedEdaValue - (MIN_VARIATION + MAX_VARIATION * random.nextDouble());
            logger.info("Starting periodic Update Task with Period: {} ms", UPDATE_PERIOD);

            this.updateTimer = new Timer();
            this.updateTimer.schedule(new TimerTask() {

                public void run() {
                    count++;
                    if (count<lines.size()) {
                        for (String line : lines) {
                            ArrayList<String[]> newValues = new ArrayList<>();
                            newValues.add(line.split(";"));
                            for (int i = 0; i < newValues.size(); i++) {
                                updateEcgMessage = new EcgMessage((newValues.get(i)[0]),
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
                            notifyUpdate(updateEcgMessage);
                        }
                    }
                    updateTimer.cancel();
                    logger.info("All {} packets sent !", lines.size());
                }
            }, UPDATE_PERIOD, UPDATE_PERIOD);

        } catch (Exception e) {
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }
    }


    public EcgMessage loadUpdatedValues() {
        return this.updateEcgMessage;
    }


    public static void main(String[] args){
        EcgCsvSensor ecgCsvSensor = new EcgCsvSensor(FILE_ECG);
        ecgCsvSensor.addDataListener(new ResourceDataListener() {
            @Override
            public void onDataChanged(SensorResource<SensorMessage> resource, SensorMessage updatedValue) throws InterruptedException {
                if(resource != null && updatedValue != null) {
                    logger.info("Device with MacAddress: {} --> New Value Recived: {}", resource.getMacAddress(), updatedValue.getValue());

                }
                else
                    logger.error("onDataChangedCallback --> Null Resource or Update Value");

            }
        });
    }


}