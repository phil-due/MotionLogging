package phild.motionlogging.DataLogger;

import phild.motionlogging.Globals;

/**
 * Created by phil on 12-8-17.
 */

public class GyroMeasurement extends SensorMeasurement {

    public GyroMeasurement(){
        values = new float[Globals.GYRO_DATA_SIZE];
    }

    public GyroMeasurement(float[] measurements) {
        values = new float[measurements.length];

        System.arraycopy(measurements, 0, values, 0, measurements.length);
    }


}
