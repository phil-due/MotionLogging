package phild.motionlogging.DataLogger;

import phild.motionlogging.Globals;

/**
 * Created by phil on 12-8-17.
 */

public class AccelMeasurement extends SensorMeasurement {

    public AccelMeasurement(float[] measurements) {
        values = new int[measurements.length];
        for (int i = 0; i < measurements.length; i++){
            //TODO Find appropriate conversion
            values[i] = Math.round(measurements[i]);
        }
    }

}
