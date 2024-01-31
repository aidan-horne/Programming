import com.samsung.knoxwsm.util.KnoxTokenUtility;
import okhttp3.*;

import java.io.*;

public class KDPUploadDevice {

    public static final String CLIENT_ID = "eyJhbGciOiJIUzUxMiJ9.eyJjbGllbnRJZGVudGlmaWVyIjoiMTMxYjMzMTAtNDM3NC00OTM5LThjOTMtM2JhOGYyNmY3YjNjOTZmNzRhOWItMTg3OC00MzY5LThlMjEtY2FiODkyZmQ2N2IwIiwiYXR0cjEiOiIxIn0.nnD7G0FdRqayBY_ePKkfoTds74zl_F-xopjnyiFo8Dsb0FRL1AtLjz1z_LdSxvlvdhNaf3FMjg76F6qqVW5-hg";
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjk6qGYP34AljwVObnHzvKmSPjAEWzFpe/zpjTi7pymeGdxYq7XgHZyTFjiuwfVOGUSY27arf6LSkT4lRJBd1BKwrbb4vR5KX+X5LdRJWh058TqqEqDuNQ+gFdUu279NPrhglCTmepOSyq2E5UtEitzefFeofPTW0PkwWxWplgi2V7xxr7EmT/80F+oQsl25j8IkFQUpdFQIk6r7myxgoOTueOqWW3vMaveccuS6ATywt1hYLKjjlIPZS2ZjyrPrDc0sHOUhrHzYNgd7c8/4LmEFBlnJDt0RRBu2ODS3sy5LVTWXl/YiCLZb6Sm9Hzs97dIXrVnP+NUcRJBZNPmT95wIDAQAB";
    public static final String CERT = "src/main/resources/kdp-keys.json";

    public static String getAccessToken(String cert, String cliendId, String publicKey) throws IOException {
        //publicKey = Base64.getEncoder().encodeToString(publicKey.getBytes());
        String signedClientId = KnoxTokenUtility.generateSignedClientIdentifierJWT(new FileInputStream(cert), cliendId);
        String curlCommand = String.format(
                "curl -X POST https://eu-kcs-api.samsungknox.com/ams/v1/users/accesstoken " +
                        "-H \"Content-Type: application/json\" " +
                        "-d \"{\\\"clientIdentifierJwt\\\":\\\"%s\\\",\\\"base64EncodedStringPublicKey\\\":\\\"%s\\\",\\\"validityForAccessTokenInMinutes\\\":\\\"30\\\"}\"",
                signedClientId, publicKey);
        System.out.println(curlCommand);
        Process process = Runtime.getRuntime().exec(curlCommand);
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String local_token = String.valueOf(reader.readLine());
        return local_token.substring(16, local_token.length() - 2);
    }

    public static void main(String[] args) throws IOException {

        //String signedClientId = getSignedClientID(CERT, CLIENT_ID, PUBLIC_KEY);
        /*String signedClientId = KnoxTokenUtility.generateSignedClientIdentifierJWT(new FileInputStream(CERT), CLIENT_ID);

        System.out.println("signedClientId : " + signedClientId);*/

        String accessToken = getAccessToken(CERT, CLIENT_ID, PUBLIC_KEY);

        String signedAccessToken = KnoxTokenUtility.generateSignedAccessTokenJWT(new FileInputStream(CERT), accessToken);

        System.out.println("signedAccessToken : " + signedAccessToken);

        String customerId = "8142128188";
        String resellerId = "1979621309";
        String transactionId = "1234";
//        String imei = "351921580053066";
        String imei = "351801420130888";

        String curlCommand = String.format(
                "curl -X PUT https://eu-kcs-api.samsungknox.com/kcs/v1/rp/devices/upload " +
                        "-H \"cache-control: no-cache\" " +
                        "-H \"content-type: application/json\" " +
                        "-H \"x-knox-apitoken: %s\" " +
                        "-d \"{ customerId: %s, resellerId: %s, transactionId: %s, devices:[%s], type: 'IMEI'}\"",
                signedAccessToken, customerId, resellerId, transactionId, imei/*, orderNo, vendorId*/);

        System.out.println(curlCommand);

        Process process = Runtime.getRuntime().exec(curlCommand);
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String local_token = String.valueOf(reader.readLine());
        System.out.println(local_token.substring(16, local_token.length() - 2));

    }
}
