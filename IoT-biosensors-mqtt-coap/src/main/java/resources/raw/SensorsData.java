package resources.raw;

import com.opencsv.bean.CsvBindByPosition;

public class SensorsData {
    @CsvBindByPosition(position = 0)
    public String macAddress;

    @CsvBindByPosition(position = 1)
    public int seqID;

    @CsvBindByPosition(position = 2)
    public Integer digital1;

    @CsvBindByPosition(position = 3)
    public Integer digital2;

    @CsvBindByPosition(position = 4)
    public Integer digital3;

    @CsvBindByPosition(position = 5)
    public Integer digital4;

    @CsvBindByPosition(position = 6)
    public Double col1;

    @CsvBindByPosition(position = 7)
    public Double col2;

    @CsvBindByPosition(position = 8)
    public Double col3;

    @CsvBindByPosition(position = 9)
    public Double col4;

    @CsvBindByPosition(position = 10)
    public Double col5;

    @CsvBindByPosition(position = 11)
    public Double col6;

    @CsvBindByPosition(position = 12)
    public Long timeStamp;

}
