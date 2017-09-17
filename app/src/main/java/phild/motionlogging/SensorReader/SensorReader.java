package phild.motionlogging.SensorReader;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import phild.motionlogging.DataLogger.AccelMeasurement;
import phild.motionlogging.DataLogger.DataEntry;
import phild.motionlogging.DataLogger.DataLogger;
import phild.motionlogging.DataLogger.GyroMeasurement;
import phild.motionlogging.Tuple;

import static phild.motionlogging.Globals.SENSOR_BUFFER_SIZE;

/**
 * Created by phil on 20-8-17.
 */

public class SensorReader implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor accSensor;
    private Sensor gyroSensor;
    private DataLogger dataLogger;
    private BlockingQueue<AccelMeasurement> accelMeasurements;
    private BlockingQueue<GyroMeasurement> gyroMeasurements;
    private long starttime_ms = 0;

    private boolean reading = false;
    public SensorReader(SensorManager sensorManager, DataLogger dataLogger){
        this.mSensorManager = sensorManager;

        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(gyroSensor==null)
            gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.dataLogger = dataLogger;

    }
    public void startReading(String label){
        this.starttime_ms = System.currentTimeMillis();
        accelMeasurements = new ArrayBlockingQueue<>(SENSOR_BUFFER_SIZE, true);
        gyroMeasurements = new ArrayBlockingQueue<>(SENSOR_BUFFER_SIZE, true);
        reading = true;
        new Thread(() -> {
            try {
                while (reading) {
                    Thread.sleep(1000);
                    List<DataEntry> entries = new ArrayList<>(SENSOR_BUFFER_SIZE);
                    while (!accelMeasurements.isEmpty() && !gyroMeasurements.isEmpty()){
                        entries.add(new DataEntry(System.currentTimeMillis(), accelMeasurements.remove(), gyroMeasurements.remove(), label));
                    }
                    dataLogger.writeAll(entries);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }
    public Tuple<AccelMeasurement,GyroMeasurement> getLastMeasurements() throws NoDataAvailableException {
        AccelMeasurement lastAccel = accelMeasurements.peek();
        GyroMeasurement lastGyro = gyroMeasurements.peek();
        if (lastAccel != null && lastGyro !=null ){
            return new Tuple<>(lastAccel,lastGyro);
        }else{
            throw new NoDataAvailableException();
        }
    }

    public long getRuntime(){
        return (System.currentTimeMillis() - this.starttime_ms)/1000;
    }
    public void pauseReading(){
        reading = false;
        mSensorManager.unregisterListener(this, accSensor);
        mSensorManager.unregisterListener(this, gyroSensor);
    }

    public boolean isActive(){
        return reading;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    switch (event.sensor.getType()) {
                        case Sensor.TYPE_ACCELEROMETER :
                            accelMeasurements.put(new AccelMeasurement(event.values.clone()));
                            break;
                        case Sensor.TYPE_MAGNETIC_FIELD :
                            gyroMeasurements.put(new GyroMeasurement(event.values.clone()));
                            break;
                        case Sensor.TYPE_GYROSCOPE :
                            gyroMeasurements.put(new GyroMeasurement(event.values.clone()));
                            break;
                        default:
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
