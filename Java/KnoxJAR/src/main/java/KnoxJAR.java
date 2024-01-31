import com.samsung.knoxwsm.util.*;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KnoxJAR {

    /*
        -------------------------------- JAR VERSION ------------------------------------------------------
    */

    public static final ArrayList<Integer> totals = new ArrayList<>();
    public static final List<List<String>> infile = new ArrayList<>();
    public static List<String> LOG_CONTENTS = new ArrayList<>();
    public static List<Level> LOG_LEVEL = new ArrayList<>();
    public static int tempcounter = 0;
    /**
     * getInfo();
     * <p>
     *     This method is used to get the information needed to establish a connection to the Samsung Knox API.
     *     Information includes:
     *         - config.properties.txt
     *             - Getting the path for the "inputfile.txt" which has the filename of the public / private key json, Knox ID,
     *               Signed client identifier and the public key.
     *             - Also gets the path for the keys directory which houses all the key json files for the customers.
     * </p>
     */
    public static void getInfo() throws IOException {

        Properties properties = new Properties();
        InputStream inputStream = KnoxJAR.class.getResourceAsStream("config.properties.txt");
        assert inputStream != null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            properties.load(reader);
            String filePath = properties.getProperty("infile");

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\s*\\|\\s*"); // Split by the delimiter "|"
                    List<String> valueList = new ArrayList<>();
                    for (String value : values) {
                        valueList.add(value.trim());
                    }
                    infile.add(valueList);
                }
            } catch (IOException e) {
                LOG_LEVEL.add(Level.SEVERE);
                LOG_CONTENTS.add("Cannot find config.properties. Please ensure it is at the root.");
            }
        }
    }

    /**
     *
     * generateLog()
     *
     * <p>
     *     This method creates a LogUtil instance which is another java class packaged in the JAR file
     *     to record the actions and details of the KnoxJAR file.
     * </p>
     *
     */
    public static void generateLog() throws IOException {

        // Generate the technical log for troubleshooting.
//        String time = LocalTime.now().toString().substring(0, 8).replace(":", "_");
        LogUtil genLog = new LogUtil();
        genLog.logger.info("Starting process");

        for (int i = 0; i < LOG_CONTENTS.size(); i++) {
            genLog.logger.log(LOG_LEVEL.get(i), LOG_CONTENTS.get(i));
        }
    }

    /**
     * getSignedClientID()
     *
     * <p>
     *     This method is used to generate the x-knox-api token and is used to authenticate to then
     *     generate requests for each customer.
     * </p>
     *
     * @param cert
     * This variable has the filepath for the private key directory and is used to sign the client ID.
     *
     * @param client_id
     * This variable has the client identifier for the customer in it and gets signed by the private key in the cert.
     *
     * @param pub_key
     * This is passed in the cURL command to get the access token with the signed client identifier.
     *
     * @return
     * This method returns the token, but the token is returned in json format, so I've used substring to remove the extra characters.
     *
     */
    public static @NotNull String getSignedClientID(String cert, String client_id, String pub_key) throws IOException {
        String signedClientId = KnoxTokenUtility.generateSignedClientIdentifierJWT(Files.newInputStream(Paths.get(cert)), client_id);
        String curlCommand = String.format(
                "curl -X POST https://eu-kcs-api.samsungknox.com/ams/v1/users/accesstoken " +
                        "-H \"Content-Type: application/json\" " +
                        "-d \"{\\\"clientIdentifierJwt\\\":\\\"%s\\\",\\\"base64EncodedStringPublicKey\\\":\\\"%s\\\",\\\"validityForAccessTokenInMinutes\\\":\\\"30\\\"}\"",
                signedClientId, pub_key);
        Process process = Runtime.getRuntime().exec(curlCommand);
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String local_token = String.valueOf(reader.readLine());
        try {
            local_token = local_token.substring(16, local_token.length() - 2);
        } catch (Exception e) {
            LOG_LEVEL.add(Level.SEVERE);
            LOG_CONTENTS.add("Token returned is NULL. This could be because the cURL command can't talk outwards. Check Internet / Firewall rules");
            generateLog();
            System.exit(1);
        }
        return local_token;
    }

    /**
     *
     * getJsonArray()
     *
     * <p>
     *     This method is used to get the raw data from the established Http connection.
     *     The method currently gets deviceList which is all devices for a certain customer but it can be adapted
     *     by changing the "deviceList" to whatever the JSON returns.
     * </p>
     *
     * @param connection
     * This variable is the HTTP connection established in the connect() method.
     *
     * @return
     * This method returns an object list because we need the total number of devices which is at level 0 and
     * then we need the data of each device that is at level 1.
     */
    private static Object[] getJsonArray(HttpURLConnection connection) {
        LOG_LEVEL.add(Level.INFO);
        LOG_CONTENTS.add("Getting device list with the open connection");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray deviceList = jsonObject.getJSONArray("deviceList");
            return new Object[]{jsonObject, deviceList};
        } catch (Exception e) {
            LOG_LEVEL.add(Level.SEVERE);
            LOG_CONTENTS.add("Problem while getting information from the open connection");
        }
        return null;
    }

    /**
     *
     * connect()
     * <p>
     *     This method will take in the credentials generated from
     * </p>
     *
     * @param token
     * This variable houses the x-knox-access token that we generated in the getSignedClientID() method.
     *
     * @param id
     * This variable has the customers Knox ID e.g., 8142128188.
     *
     * @param apiUrl
     * This variable has the api url we are going to use. Currently, the code only gets the device list, so it uses
     * <a href="https://eu-kcs-api.samsungknox.com/ams/v1/users/accesstoken">...</a>. You can replace this URL with another one,
     * but it may cause complications from the printInfo() class.
     *
     * @param params
     * This variable has the parameters that are passed in with the api url. Currently, the param I'm using is page number.
     * This is because when we query the site, if the customer has over 100 devices, they are housed on a different page so we have
     * to loop through all pages.
     *
     * @return
     * This method returns the opened connection to then be passed into getJsonArray() to get the raw data.
     *
     */
    private static HttpURLConnection connect(String token, String id, String apiUrl, String params) throws IOException {
        URL url = new URL(apiUrl + "?" + params);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("x-knox-apitoken", token);
        connection.setRequestProperty("cache-control", "no-cache");
        if (id != null) {
            connection.setRequestProperty("x-wsm-managed-tenantid", id);
        }
        return connection;
    }

    /**
     *
     * printInfo()
     *
     * <p>
     *     This method will sift through the raw data and format it into the schema below.
     *
     *     CustID | TenanatName | IMEI | SerialNumber | uploadTime | orderID | model | userName | tags | createdTime
     *     | profile | resellerID | resellerName | lastUpdatedTime | IMEI_14
     *
     *     It will then write it to a text file that will be uploaded to the database.
     * </p>
     *
     * @param token
     * This variable houses the x-knox-access token that we generated in the getSignedClientID() method.
     *
     * @param apiUrl
     * This variable has the api url we are going to use. Currently, the code only gets the device list, so it uses
     * <a href="https://eu-kcs-api.samsungknox.com/ams/v1/users/accesstoken">...</a>. You can replace this URL with another one,
     * but it may cause complications from the printInfo() class.
     *
     * @param id
     * This variable has the customers Knox ID e.g., 8142128188.
     *
     */
    private static void printInfo(String token, String apiUrl, String id) throws IOException, JSONException {
        int j = 0;
        int count = 0;
        int devicesRemoved = 0;
        boolean lastPage = false;
        String tenantName = "";
        FileWriter writer = new FileWriter("output/output.txt", true);
        while (!lastPage) {
            HttpURLConnection connection = connect(token, id, apiUrl, String.format("pageNum=%s", j));
            int responseCode = connection.getResponseCode();

            if (!(responseCode == HttpURLConnection.HTTP_OK)) {
                connection = connect(token, null, apiUrl, String.format("pageNum=%s", j));
                responseCode = connection.getResponseCode();
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Object[] results = getJsonArray(connection);
                JSONArray deviceList = (JSONArray) results[1];
                JSONObject totalDevices = (JSONObject) results[0];


                // Assuming there's only one element in the deviceList array
                for (int i = 0; i < deviceList.length(); i++) {
                    JSONObject device = deviceList.getJSONObject(i);

                    long lastModTime = Long.parseLong(device.getString("updateTime"));
                    long createTime = Long.parseLong(device.getString("createTime"));
                    long resellerUploadTime = Long.parseLong(device.getString("resellerUploadTime"));

                    String IMEI = device.getString("mei");
                    String profileAssigned = device.getString("profileName");
                    tenantName = device.getString("tenantName");
                    String serialNumber = device.getString("serialNumber");
                    String orderId = device.getString("orderId");
                    String model = device.getString("model");
                    String userName = device.getString("userName");
                    String tags = device.getString("tags");
                    String resellerId = device.getString("uploadId");
                    String resellerName = device.getString("resellerName");

                    Instant uinst = Instant.ofEpochMilli(resellerUploadTime);
                    Instant cinst = Instant.ofEpochMilli(createTime);
                    Instant linst = Instant.ofEpochMilli(lastModTime);

                    LocalDateTime uploadDateTime = uinst.atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime createDateTime = cinst.atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime lastDateTime = linst.atZone(ZoneId.systemDefault()).toLocalDateTime();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    String uploadformattedDateTime = uploadDateTime.format(formatter);
                    String createformattedDateTime = createDateTime.format(formatter);
                    String lastformattedDateTime = lastDateTime.format(formatter);

                    id = (!Objects.equals(id, "null")) ? id : "".trim();
                    tenantName = (!Objects.equals(tenantName, "null")) ? tenantName : "".trim();
                    serialNumber = (!Objects.equals(serialNumber, "null")) ? serialNumber : "".trim();
                    uploadformattedDateTime = (!uploadformattedDateTime.equals("null")) ? uploadformattedDateTime : "".trim();
                    orderId = (!Objects.equals(orderId, "null")) ? orderId : "".trim();
                    model = (!Objects.equals(model, "null")) ? model : "".trim();
                    userName = (!Objects.equals(userName, "null")) ? userName : "".trim();
                    tags = (!Objects.equals(tags, "null")) ? tags : "".trim();
                    createformattedDateTime = (!createformattedDateTime.equals("null")) ? createformattedDateTime : "".trim();
                    profileAssigned = (!Objects.equals(profileAssigned, "null")) ? profileAssigned : "".trim();
                    resellerId = (!Objects.equals(resellerId, "null")) ? resellerId : "".trim();
                    resellerName = (!Objects.equals(resellerName, "null")) ? resellerName : "".trim();
                    lastformattedDateTime = (!lastformattedDateTime.equals("null")) ? lastformattedDateTime : "".trim();
                    IMEI = (!Objects.equals(IMEI, "null")) ? IMEI: "".trim();
                    try {
                        writer.write(id + ";" + tenantName + ";" + IMEI + ";" + serialNumber + ";" + uploadformattedDateTime + ";" + orderId + ";" + model
                                + ";" + userName + ";" + tags + ";" + createformattedDateTime + ";" + profileAssigned + ";" + resellerId + ";" + resellerName + ";" + lastformattedDateTime
                                + ";" + IMEI.substring(0, 14) + "\n");
                    } catch (Exception e) {

                        // If the device doesn't have an IMEI (maybe engineering model) then we skip it because it violates primary key integrity.
                        devicesRemoved += 1;
                        continue;
                    }
                    count++;
                }
                if (count == Integer.parseInt(totalDevices.getString("totalCount")) - devicesRemoved) {
                    LOG_LEVEL.add(Level.INFO);
                    LOG_CONTENTS.add(String.format("Successfully got all devices for %s", tenantName));
                    lastPage = true;
                    totals.add(count);
                    count = 0;
                    devicesRemoved = 0;
                }
                j++;
            } else {
                System.out.println(connection.getResponseMessage());
                System.out.println(connection.getResponseCode());
                LOG_LEVEL.add(Level.SEVERE);
                LOG_CONTENTS.add(String.format("Error getting information. Error Code: %s, Error Message: %s", connection.getResponseCode(), connection.getResponseMessage()));
            }
        }
        writer.close();
    }

    /**
     *
     * main()
     * <p>
     *     The main method is the structure and process. If you follow this from top to bottom you can see the steps the program takes/
     *     Steps include:
     *          - Gather the info needed to run the program from getInfo()
     *          - Generate the output file and delete the old one if found.
     *          - Get the directory for the private keys and all the variables needed to establish a connection.
     *          - Sign the client identifier and get the signed access token.
     *          - run printInfo to write to output.txt the results of all the customers.
     *          - generate the log.
     *          - done.
     * </p>
     *
     * @param args
     * args is just a placeholder variable idk good practice i guess.
     *
     */
    public static void main(String[] args) throws IOException {

        getInfo();
        File outputFile = new File("output/output.txt");
        outputFile.delete();

        for (List<String> list : infile) {
            Properties prop = new Properties();
            InputStream inputStream = KnoxJAR.class.getResourceAsStream("config.properties.txt");
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                prop.load(reader);
                String filePath = prop.getProperty("private.keys.directory");

                String cert = filePath + list.get(0) + ".json";
                String knoxID = list.get(1);
                String client_id = list.get(2);
                String public_key = list.get(3);

                String signedClientID = getSignedClientID(cert, client_id, public_key);
                String signedAccessToken = KnoxTokenUtility.generateSignedAccessTokenJWT(Files.newInputStream(Paths.get(cert)), signedClientID);
                String apiUrl = "https://eu-kcs-api.samsungknox.com/kcs/v1/kme/devices/list";

                printInfo(signedAccessToken, apiUrl, knoxID);
            } catch (Exception e) {
                LOG_LEVEL.add(Level.SEVERE);
                LOG_CONTENTS.add("Error opening config.properties. Please check it is at the root directory");
            }
        }
        generateLog();
    }
}
