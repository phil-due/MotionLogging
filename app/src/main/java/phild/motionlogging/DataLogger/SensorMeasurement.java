package phild.motionlogging.DataLogger;

/**
 * Created by phil on 12-9-17.
 */

public abstract class SensorMeasurement {

    protected float[] values = null;

    public String toCsv(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < values.length-1; i++){
            sb.append(values[i]).append(",");
        }
        sb.append(values[values.length-1]);
        return sb.toString();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < values.length-1; i++){
            sb.append(String.valueOf(values[i])).append(":");
        }
        sb.append(String.valueOf(values[values.length-1]));
        return sb.toString();
    }
}
