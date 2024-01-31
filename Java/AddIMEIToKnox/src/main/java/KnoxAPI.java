import com.samsung.knoxwsm.util.*;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KnoxAPI {

    /*
        -------------------------------- NON JAR VERSION ------------------------------------------------------
    */

    public static final ArrayList<Integer> totals = new ArrayList<>();

    /**
     * getInfo();
     * <p>
     * This method is used to get the information needed to establish a connection to the Samsung Knox API.
     * Information includes:
     *      - config.properties.txt
     *          - Getting the path for the "inputfile.txt" which has the filename of the public / private key json, Knox ID,
     *            Signed client identifier and the public key.
     *          - Also gets the path for the keys directory which houses all the key json files for the customers.
     * </p>
     */
    public static List<List<String>> getInfo() throws IOException {

        // ----------------------------------- Getting the Knox ID and the customer name -------------------------------
        List<List<String>> infile = new ArrayList<>();
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("config.properties.txt")) {
            properties.load(fileReader);
            String filePath = properties.getProperty("infile");

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\s*\\|\\s*"); // Split by the delimiter ";"
                    List<String> valueList = new ArrayList<>();
                    for (String value : values) {
                        valueList.add(value.trim());
                    }
                    infile.add(valueList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return infile;
    }

    /**
     * getSignedClientID()
     *
     * <p>
     * This method is used to generate the x-knox-api token and is used to authenticate to then
     * generate requests for each customer.
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
     * This method returns the token but the token is returned in json format so I've substringed off the extra characters.
     *
     */
    public static @NotNull String getSignedClientID(String cert, String client_id, String pub_key) throws IOException {
        String signedClientId = KnoxTokenUtility.generateSignedClientIdentifierJWT(new FileInputStream(cert), client_id);
        String curlCommand = String.format(
                "curl -X POST https://eu-kcs-api.samsungknox.com/ams/v1/users/accesstoken " +
                        "-H \"Content-Type: application/json\" " +
                        "-d \"{\\\"clientIdentifierJwt\\\":\\\"%s\\\",\\\"base64EncodedStringPublicKey\\\":\\\"%s\\\",\\\"validityForAccessTokenInMinutes\\\":\\\"30\\\"}\"",
                signedClientId, pub_key);
        Process process = Runtime.getRuntime().exec(curlCommand);
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String local_token = String.valueOf(reader.readLine());
        return local_token.substring(16, local_token.length() - 2);
    }

    /**
     *
     * getJsonArray()
     *
     * <p>
     * This method is used to get the raw data from the established Http connection.
     * The method currently gets deviceList which is all devices for a certain customer but it can be adapted
     * by changing the "deviceList" to whatever the JSON returns.
     * </p>
     *
     * @param connection
     * This variable is the HTTP connection established in the connect() method.
     *
     * @return
     * This method returns an object list because we need the total number of devices which is at level 0 and
     * then we need the data of each device which is at level 1.
     */
    private static Object[] getJsonArray(HttpURLConnection connection) throws IOException, JSONException {
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
    }


    static HttpURLConnection connect(String token, String id, String apiUrl, String params) throws IOException {
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

    private static void printInfo(String token, String apiUrl, String id) throws IOException, JSONException {
        int j = 0;
        int count = 0;
        int devicesRemoved = 0;
        boolean lastPage = false;
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
                    String tenantName = device.getString("tenantName");
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

                    id = (id != "null") ? id : "".trim();
                    tenantName = (tenantName != "null") ? tenantName : "".trim();
                    serialNumber = (serialNumber != "null") ? serialNumber : "".trim();
                    uploadformattedDateTime = (uploadformattedDateTime != "null") ? uploadformattedDateTime : "".trim();
                    orderId = (orderId != "null") ? orderId : "".trim();
                    model = (model != "null") ? model : "".trim();
                    userName = (userName != "null") ? userName : "".trim();
                    tags = (tags != "null") ? tags : "".trim();
                    createformattedDateTime = (createformattedDateTime != "null") ? createformattedDateTime : "".trim();
                    profileAssigned = (profileAssigned != "null") ? profileAssigned : "".trim();
                    resellerId = (resellerId != "null") ? resellerId : "".trim();
                    resellerName = (resellerName != "null") ? resellerName : "".trim();
                    lastformattedDateTime = (lastformattedDateTime != "null") ? lastformattedDateTime : "".trim();
                    IMEI = (IMEI != "null") ? IMEI: "".trim();

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
                    lastPage = true;
                    totals.add(count);
                    count = 0;
                    devicesRemoved = 0;
                }
                j++;
            } else {
                System.out.println(connection.getResponseMessage());
                System.out.println(connection.getResponseCode());
            }
        }
        writer.close();
    }

    public static String[] getProperties(List<String> list) throws FileNotFoundException {
        String[] result = new String[0];
        Properties prop = new Properties();
        try (FileReader fileReader = new FileReader("config.properties.txt")) {
            prop.load(fileReader);
            String filePath = prop.getProperty("private.keys.directory");

            String cert = filePath + list.get(0) + ".json";
            String knoxID = list.get(1);
            String client_id = list.get(2);
            String public_key = list.get(3);

            String signedClientID = getSignedClientID(cert, client_id, public_key);
            String signedAccessToken = KnoxTokenUtility.generateSignedAccessTokenJWT(Files.newInputStream(Paths.get(cert)), signedClientID);

            result = new String[]{signedAccessToken, knoxID};

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) throws IOException, JSONException {

        List<List<String>> infile = getInfo();
        File outputFile = new File("output/output.txt");
        outputFile.delete();
        for (List<String> list : infile) {
            String[] results = getProperties(list);
            printInfo(results[0], "https://eu-kcs-api.samsungknox.com/kcs/v1/kme/devices/list", results[1]);
        }
    }
}
