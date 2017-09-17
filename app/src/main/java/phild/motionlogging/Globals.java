package phild.motionlogging;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

/**
 * Created by phil on 12-8-17.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Globals {
    public static final int GYRO_DATA_SIZE = 9;
    public static final int ACCEL_DATA_SIZE = 3;
    public static final String APP_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/MotionLogging";
    public static final String FILE_PATH = "log";
    public static final String LOG_TAG = "MotionLogging";
    public static final int SENSOR_BUFFER_SIZE = 1000;
    public static final int T_GUI_REFRESH_MS = 500;
}
