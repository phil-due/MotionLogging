package phild.motionlogging.DataLogger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

import phild.motionlogging.MainActivity;

/**
 * Created by phil on 12-8-17.
 */

public class DataLoggerImpl implements DataLogger {

    private File file;

    public DataLoggerImpl(String fileName,String appDir){
        this.file = new File(appDir +"/"+ fileName + ".csv");
    }

    @Override
    public void write(DataEntry dataEntry) {

        ArrayList<DataEntry> entries = new ArrayList<>(1);
        entries.add(dataEntry);
        this.writeEntries(entries);
    }

    @Override
    public void writeAll(Collection<DataEntry> dataEntries) {

        try {
            synchronized (this) {
                if (!file.exists()){
                    if(file.createNewFile()){
                        writeEntries(dataEntries);
                    }else {
                        android.util.Log.d("Can't create file", file.getAbsolutePath().toString());
                    }
                }else{
                        writeEntries(dataEntries);
                }
            }
        }catch (Exception e) {
            android.util.Log.d("failed to save file", e.toString());
        }
    }

    private void writeEntries(Collection<DataEntry> dataEntries){
        try {
                FileWriter filewriter = new FileWriter(file, true);
                BufferedWriter out = new BufferedWriter(filewriter);
                for (DataEntry e : dataEntries){
                    out.write(e.toCsv());
                    //android.util.Log.d("written",e.toCsv());
                }
                out.close();
                filewriter.close();

        } catch (Exception e) {
            android.util.Log.d("failed to save file", e.toString());
        }
    }

}
