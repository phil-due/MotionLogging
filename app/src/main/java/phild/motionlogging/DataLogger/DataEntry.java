package phild.motionlogging.DataLogger;

import java.util.Locale;

/**
 * Created by phil on 12-8-17.
 */

public class DataEntry {
    private long timestamp;
    private AccelMeasurement accelMeasurment;
    private GyroMeasurement gyroData;
    private String label;

    public DataEntry(long timestamp, AccelMeasurement accelMeasurment, GyroMeasurement gyroData, String label) {
        this.timestamp = timestamp;
        this.accelMeasurment = accelMeasurment;
        this.gyroData = gyroData;
        this.label = label;
    }

    public String toCsv(){
        return String.format(Locale.ENGLISH,"%d,%s,%s,%s\r\n",this.timestamp, accelMeasurment.toCsv(),gyroData.toCsv(),label);
    }
}
