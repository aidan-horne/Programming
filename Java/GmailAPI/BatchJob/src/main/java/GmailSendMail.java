import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.*;
import com.opencsv.CSVWriter;


/**
 * GmailSendMail
 * <p>
 * This class is the main class that utilises GmailSendMainUtil and LogUtil.
 * In this class, the code will look through the src/resources/keys folder and
 * src/resources/tokens folder to gather all information needed to locate / generate
 * a token for each inbox and then create a template email that will be sent to each inbox
 * to (in theory) keep the inboxes alive and not be deleted by Google's 2-year rule.
 * <p/>
 */
public class GmailSendMail extends GmailSendMailUtil {

    static List<String> CREDENTIAL_LIST = new ArrayList<>();
    static List<String> EMAIL_ADDRESSES = new ArrayList<>();
    static List<String> TOKENS = new ArrayList<>();
    static List<String> LOG_CONTENTS = new ArrayList<>();
    static List<Level> LOG_LEVEL = new ArrayList<>();
    static List<String> ACCOUNT_NAME = new ArrayList<>();
    static List<String> RESULT = new ArrayList<>();
    static String USER_ID = "me";
    static int count = 0;
    static int emailSent = 0;
    static boolean willCreateEmail = true;
    static boolean willCreateLists = true;
    static boolean somethingWentWrong = false;
    static List<File> files = new ArrayList<>();
    static File[] key_contents = null;
    static File key_directory = null;

    public static void main(String[] args) throws Exception {

        String time = LocalTime.now().toString().substring(0, 8).replace(":", "_");

        // Getting the filepath of credential and tokens files and adding the email address to a list.
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("config.properties.txt")) {
            properties.load(inputStream);
        } catch (IOException e) {
            LOG_LEVEL.add(Level.SEVERE);
            LOG_CONTENTS.add("Error: Cannot find config.properties");
        }

        // Getting the filepath of the credentials directory from the config.properties file
        String keys_credentialsDirectory = properties.getProperty("credentials.directory");
        try {
            key_directory = new File(keys_credentialsDirectory);
            key_contents = key_directory.listFiles();
        } catch (Exception e) {
            LOG_LEVEL.add(Level.SEVERE);
            LOG_CONTENTS.add("Cannot find file directory");
            somethingWentWrong = true;
        }

        // Checking that we found the correct file paths before proceeding to ensure we correctly log what went wrong.
        if (!somethingWentWrong) {
            assert key_contents != null;
            try {
                if (key_contents.length == 0) {
                    LOG_LEVEL.add(Level.SEVERE);
                    LOG_CONTENTS.add(String.format("No keys in directory %s", key_directory));
                }
            } catch (Exception e) {
                LOG_LEVEL.add(Level.SEVERE);
                LOG_CONTENTS.add(String.format("Incorrect file directory. current path is %s", key_directory));
            }

            // Checking all file names follows the correct format as in the guide.
            for (File file : key_contents) {
                if (!file.toString().contains("_credentials") && !somethingWentWrong) {
                    try {
                        LOG_LEVEL.add(Level.SEVERE);
                        LOG_CONTENTS.add(String.format("File name for %s keys are in the wrong format. Format should be <customeremail>_credentials.json", file.toString().substring(file.toString().lastIndexOf("\\") + 1).toUpperCase()));
                        ACCOUNT_NAME.add(file.toString().substring(file.toString().lastIndexOf("\\") + 1, file.toString().lastIndexOf(".")));
                        RESULT.add("FAILED");
                    } catch (Exception e) {
                        // Tested with a folder in there, and it throws an exception.
                        LOG_LEVEL.add(Level.SEVERE);
                        LOG_CONTENTS.add("Make sure there are only json files in the resources folder");
                        somethingWentWrong = true;
                    }
                } else {
                    files.add(file);
                }
            }
        }

        if (willCreateLists && !somethingWentWrong) {
            // Loop through all the files and format the data appropriately.
            for (File file : files) {
                try {
                    // Format = RenaissanceGroup.techsafe_credentials.json
                    CREDENTIAL_LIST.add(file.toString().substring(file.toString().lastIndexOf("/") + 1));

                    // Format = tokens\RenaissanceGroup.techsafe
                    TOKENS.add(file.toString().substring(file.toString().lastIndexOf("/") + 1, file.toString().lastIndexOf("_")).replace("keys", "tokens"));

                    // Format = RenaissanceGroup.techsafe@gmail.com
                    EMAIL_ADDRESSES.add(file.toString().substring(file.toString().lastIndexOf("\\") + 1, file.toString().indexOf("_")) + "@gmail.com");
                    count++;

                } catch (Exception e) {
                    LOG_LEVEL.add(Level.SEVERE);
                    LOG_CONTENTS.add("Error looping through keys files to create credential list, token list and email address list");
                    ACCOUNT_NAME.add(TOKENS.get(count).substring(TOKENS.get(count).indexOf("/") + 6));
                    RESULT.add("FAILED");
                    willCreateEmail = false;
                }
            }
        }

        // Again making sure that we log the correct errors by having these booleans.
        if (willCreateEmail && !somethingWentWrong) {
            // Loop through all the emails with the credentials and email itself.
            for (int i = 0; i < CREDENTIAL_LIST.size(); i++) {
                try {
                    LOG_LEVEL.add(Level.INFO);
                    LOG_CONTENTS.add(String.format("Getting authentication token for %s", TOKENS.get(i).substring(28)));

                    // Build a new authorized Gmail API client
                    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

                    // Creating credential object to parse into email.
                    System.out.printf("Getting (or checking) token for %s%n", TOKENS.get(i).substring(28).toUpperCase());
                    Credential credential = GmailSendMailUtil.getCredentials(HTTP_TRANSPORT, CREDENTIAL_LIST.get(i), TOKENS.get(i));

                    // Building Gmail object.
                    Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GmailSendMailUtil.JSON_FACTORY, credential)
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                    LOG_LEVEL.add(Level.INFO);
                    LOG_CONTENTS.add(String.format("Building email for %s with their access token", TOKENS.get(i).substring(28)));

                    // Create a message
                    MimeMessage email = createEmail(EMAIL_ADDRESSES.get(i), EMAIL_ADDRESSES.get(i), "Subject", "This is an email sent to the inbox to keep it alive");

                    // Send the email
//                    sendMessage(service, USER_ID, email);
                    emailSent++;
                    ACCOUNT_NAME.add(TOKENS.get(i).substring(TOKENS.get(i).indexOf("\\") + 6));
                    RESULT.add("SUCCESS");
                } catch (Exception e) {
                    LOG_LEVEL.add(Level.SEVERE);
                    LOG_CONTENTS.add(String.format("Error getting %s access token", TOKENS.get(emailSent).substring(TOKENS.get(emailSent).lastIndexOf("/") + 1).toUpperCase()));
                    LOG_LEVEL.add(Level.SEVERE);
                    LOG_CONTENTS.add(e.toString());
                    ACCOUNT_NAME.add(TOKENS.get(emailSent).substring(TOKENS.get(emailSent).indexOf("\\") + 6));
                    RESULT.add("FAILED");
                }
            }
        }

        // Generate csv log to be sucked up and used for reporting.
        File f = new File(String.format("logs/%s/results_", time) + time + ".csv");
        f.getParentFile().mkdirs();
        FileWriter outputFile = new FileWriter(f);
        CSVWriter writer = new CSVWriter(outputFile);
        for (int i = 0; i < ACCOUNT_NAME.size(); i++) {
            Date date = new Date(System.currentTimeMillis());
            writer.writeNext(new String[]{ACCOUNT_NAME.get(i).substring(21), RESULT.get(i)});
        }
        writer.close();

        // Generate the technical log for troubleshooting.
        LogUtil genLog = new LogUtil(String.format("%s/log", time) + ".txt");
        genLog.logger.info("Starting process");

        for (int i = 0; i < LOG_CONTENTS.size(); i++) {
            genLog.logger.log(LOG_LEVEL.get(i), LOG_CONTENTS.get(i));
        }
        genLog.logger.log(Level.INFO, String.format("Sent email to %s inboxes out of %s inboxes", emailSent, key_contents.length));
    }
}
