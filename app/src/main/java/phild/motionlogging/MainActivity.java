package phild.motionlogging;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import phild.motionlogging.DataLogger.AccelMeasurement;
import phild.motionlogging.DataLogger.DataLoggerImpl;
import phild.motionlogging.DataLogger.GyroMeasurement;
import phild.motionlogging.SensorReader.NoDataAvailableException;
import phild.motionlogging.SensorReader.SensorReader;

import static phild.motionlogging.Globals.APP_DIR;
import static phild.motionlogging.Globals.LOG_TAG;
import static phild.motionlogging.Globals.T_GUI_REFRESH_MS;


public class MainActivity extends Activity  {

    private SensorReader sensorReader;
    private String activityLabel;
    private int countdown_s;
    private int duration_s;
    private ToggleButton toggleButton;
    private TextView textAccelData;
    private TextView textGyroData;
    private TextView textStatus;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpGui();

        textStatus.post(() ->textStatus.setText("App initialized."));
    }

    protected void stopLogging(){
        sensorReader.pauseReading();
        android.util.Log.d(LOG_TAG,"measurement stopped");
    }

    protected void startLogging(){

        //TODO request memory
        if (sensorReader == null)
            sensorReader = new SensorReader((SensorManager) getSystemService(SENSOR_SERVICE),new DataLoggerImpl(Globals.FILE_PATH,Globals.APP_DIR));

        startCountdown(countdown_s);

        if (duration_s > 0){
            stopCountdown(this.duration_s);
        }

        android.util.Log.d(LOG_TAG,"measurement started");

    }

    private void startCountdown(int countdown_s){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        sensorReader.startReading(activityLabel);
                        new Thread(() -> showStatus()).start();
                    }
                },
                countdown_s *1000
        );
    }

    private void stopCountdown(int duration_s){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        stopLogging();
                    }
                },
                duration_s *1000
        );
    }

    protected void checkFolder() {
        File f = new File(APP_DIR);

        if (!f.exists() ) {
            if(!f.mkdirs()){
                System.err.print("Could not create Folder structure");
            }
        }
    }

    protected void showStatus(){
        textStatus.post(() ->textStatus.setText("Started"));
        while(sensorReader.isActive()){
            try {
                Tuple<AccelMeasurement,GyroMeasurement> measurements = sensorReader.getLastMeasurements();
                Message msg = mHandler.obtainMessage();
                msg.obj = measurements;
                msg.sendToTarget();

            } catch (NoDataAvailableException e) {
                wait(T_GUI_REFRESH_MS);
            }
            wait(T_GUI_REFRESH_MS);
            textStatus.post(() ->textStatus.setText(Long.toString(sensorReader.getRuntime())+"s"));


        }
        toggleButton.post(()->toggleButton.setChecked(sensorReader.isActive()));
        textAccelData.post(() -> textAccelData.setText("--"));
        textGyroData.post(() -> textGyroData.setText("--"));
        textStatus.post(() ->textStatus.setText("Stopped"));

    }
    private static void wait(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    protected void buttonListener(boolean isStart){
        if (hasWritePermission()) {

            checkFolder();

            if (isStart){
                startLogging();
            }else{
                stopLogging();
            }
        }else{
            toggleButton.setChecked(false);
            requestWritePermission(MainActivity.this);
        }
    }
    private boolean hasWritePermission(){
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setMessage("This app needs permission to write the log file to the sd card")
                    .setPositiveButton("Allow", (dialog, which) -> ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1)).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUpGui(){

        textAccelData = findViewById(R.id.textAccelData);
        textGyroData = findViewById(R.id.textGyroData);
        textStatus = findViewById(R.id.textStatus);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener((v,isChecked) -> buttonListener(isChecked));

        final EditText activityLabelField = findViewById(R.id.activityLabel);
        activityLabelField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activityLabel = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final EditText countdownField = findViewById(R.id.countdown);
        countdownField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    countdown_s = Integer.parseInt(charSequence.toString());
                }catch (NumberFormatException e){
                    countdown_s = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final EditText durationField = findViewById(R.id.duration);
        durationField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    duration_s = Integer.parseInt(charSequence.toString());
                }catch (NumberFormatException e){
                    duration_s = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage){
                Tuple<AccelMeasurement,GyroMeasurement> measurements = (Tuple<AccelMeasurement,GyroMeasurement>) inputMessage.obj;
                textAccelData.setText(measurements.x.toString());
                textGyroData.setText(measurements.y.toString());
            }
        };
    }


}