import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public LogUtil() throws SecurityException, IOException {
        // Create a directory for logs using the current date and time
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        File logDir = new File("logs", timestamp);
        logDir.mkdirs();

        // Create the log file within the directory
        File logFile = new File(logDir, "log_file.txt");

        // Create the log file handler
        fh = new FileHandler(logFile.getPath(), true);

        // Configure the logger
        logger = Logger.getLogger("test");
        logger.addHandler(fh);
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fh.setFormatter(simpleFormatter);
    }

}
