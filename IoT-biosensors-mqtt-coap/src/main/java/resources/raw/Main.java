package resources.raw;
import java.util.List;

public class Main {

        String filenameECG = "/Users/olivia1/Desktop/ECG_normal_conrespgiusta_sa1_30sec.csv";
        //String filenameEMG = "/Users/olivia1/Desktop/ECG_normal_conrespgiusta_sa1_30sec.csv";
        String filenameEDA = "/Users/olivia1/Desktop/EDA_apnea_sa10_30sec.csv";

        EcgCsvSensor ecgCsvSensor = new EcgCsvSensor(filenameECG);
        //EmgCsvSensor emgCsvSensor = new EmgCsvSensor(filenameEMG, "3", 10);
        EdaCsvSensor edaCsvSensor = new EdaCsvSensor(filenameEDA);

        List<SensorsData> ecgData = ecgCsvSensor.getSensorDataList();
        //List<SensorsData> emgData = emgCsvSensor.getSensorDataList();
        List<SensorsData> edaData = edaCsvSensor.getSensorDataList();

        // aggiungere le list delle classi EMG, EDA, ...
        /*emgCsvSensor.transformFunctionEMG();
        String emgJSON = emgCsvSensor.dataToJson();
        System.out.println(emgJSON);*/

        /*ecgCsvSensor.loadUpdatedValues();
        String ecgJSON = ecgCsvSensor.dataToJson();
        System.out.println(ecgJSON);

        edaCsvSensor.loadUpdatedValues();
        String edaJSON = edaCsvSensor.dataToJson();
        System.out.println(edaJSON);*/



}
