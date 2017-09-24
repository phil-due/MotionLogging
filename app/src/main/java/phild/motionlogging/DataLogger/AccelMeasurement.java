package phild.motionlogging.DataLogger;

/**
 * Created by phil on 12-8-17.
 */

public class AccelMeasurement extends SensorMeasurement {

    public AccelMeasurement(float[] measurements) {
        values = new float[measurements.length];
        System.arraycopy(measurements, 0, values, 0, measurements.length);
    }

}
