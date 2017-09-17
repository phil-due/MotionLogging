package phild.motionlogging.DataLogger;

import java.util.Collection;

/**
 * Created by phil on 12-8-17.
 */

public interface DataLogger {

    void write(DataEntry dataEntry);

    void writeAll(Collection<DataEntry> dataEntries);
}
