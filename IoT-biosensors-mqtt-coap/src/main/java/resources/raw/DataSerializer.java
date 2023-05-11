package resources.raw;
import java.util.List;

public class DataSerializer {
    String macaddress;
    String type;
    List<SensorsData> data;

    DataSerializer(String macAddress, String type, List<SensorsData> data){
        this.macaddress = macAddress;
        this.type = type;
        this.data = data;
    }

}
