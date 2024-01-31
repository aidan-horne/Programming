import com.samsung.knoxwsm.util.KnoxTokenUtility;
import okhttp3.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class AddIMEIToKnox extends KnoxAPI{

    public static String apiUrl = "https://kcs-openapi.samsungknox.com/kcs/v1/rp/devices/upload";

    private static HttpURLConnection connect(String token, String id, String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("content-type", "application/java");
        connection.setRequestProperty("x-knox-apitoken", token);
        connection.setRequestProperty("cache-control", "no-cache");

//        connection.setDoOutput(true);
//
//        // Create a JSON string with your data
//        String jsonInput = "{"
//                + "\"customerID\": 8142128188, "
//                + "\"resellerID\": 8142128188, "
//                + "\"transactionID\": 8142128188, "
//                + "\"devices\": \"351801420130888\", "
//                + "\"type\": \"IMEI\""
//                + "}";
//        // Write the JSON data to the request's output stream
//        try (OutputStream os = connection.getOutputStream()) {
//            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
//            os.write(input, 0, input.length);
//        }
//
//        // Get the response from the API
//        int responseCode = connection.getResponseCode();
//        System.out.println(responseCode);
//        if (id != null) {
//            connection.setRequestProperty("x-wsm-managed-tenantid", id);
//        }

        return connection;
    }

    public static String getToken(List<String> list) throws FileNotFoundException {
        Properties prop = new Properties();
        try (FileReader fileReader = new FileReader("config.properties.txt")) {
            prop.load(fileReader);
            String filePath = prop.getProperty("private.keys.directory");

            String cert = filePath + list.get(0) + ".json";
            String client_id = list.get(2);
            String public_key = list.get(3);

            String signedClientID = getSignedClientID(cert, client_id, public_key);
            System.out.println(signedClientID);
            return KnoxTokenUtility.generateSignedAccessTokenJWT(Files.newInputStream(Paths.get(cert)), signedClientID);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fetchData(String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/java");
        RequestBody body = RequestBody.create(mediaType, "customerId=8142128188 & resellerId=8142128188 & transactionId=1 & type=IMEI & orderNo=1 & " +
                "orderTime=1542405655 & vendorId=8142128188 & devices=[351801420130888,capella]");
        Request request = new Request.Builder()
                .url("https://eu-kcs-api.samsungknox.com/kcs/v1/rp/devices/upload")
                .put(body)
                .addHeader("cache-control", "no-cache")
                .addHeader("Content-Type", "application/java")
                .addHeader("x-knox-apitoken", token)
                .build();

        System.out.println(client.newCall(request).execute().code());
        System.out.println(Objects.requireNonNull(client.newCall(request).execute().body()).string());

    }


    public static void main(String[] args) throws IOException {

        List<List<String>> infile = KnoxAPI.getInfo();
        for (List<String> list : infile) {
            String results = getToken(list);
            System.out.println(results);
            fetchData(results);
//            HttpURLConnection connection = connect(results[0], results[0], apiUrl);
//            if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
//                connection = connect(results[0], null, apiUrl);
//            }
//            System.out.println(connection.getResponseCode());
        }
    }
}
