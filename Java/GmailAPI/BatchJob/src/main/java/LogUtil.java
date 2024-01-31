import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtil {

    public Logger logger;
    FileHandler fh;

    /**
     * logUtil()
     * <p>
     *     This method is used to handle and build the log file and then write it out to the log directory.
     * </p>
     */
    public LogUtil(String file_name) throws SecurityException, IOException {

        String time = LocalTime.now().toString().substring(0, 8).replace(":", "_");
        File f = new File(String.format("logs/%s/", time));
        f.getParentFile().mkdirs();
        if (f.exists()) {
            f.createNewFile();
        }

        fh = new FileHandler("logs/" + file_name, true);
        logger = Logger.getLogger("test");
        logger.addHandler(fh);
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fh.setFormatter(simpleFormatter);
    }


}
