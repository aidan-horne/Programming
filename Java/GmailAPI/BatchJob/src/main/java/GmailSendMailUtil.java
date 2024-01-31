import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;

import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;

public class GmailSendMailUtil {
    public static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final String SCOPES = GMAIL_SEND;

    /**
     * getCredentials
     * <p>
     *     This method is used to generate the credential needed to send the email. It uses aspects
     *     from the credential json file to build a Credential object which is parsed with other variables
     *     when sending the email for authentication.
     * </p>
     *
     * @param HTTP_TRANSPORT is used to build the AuthorizationCodeFlow and is built in the main method with GoogleNetHttpTransport.newTrustedTransport();
     * @param credentials is from the JSON file and is a unique encoded string.
     * @param token is pre-generate from the workflow in the Sharepoint guide where you sign in to batchjobmaster and authorize the app. It's then a permanent token and read each time the code is ran.
     * @return a Credential object which is used to authenticate the email.
     * @throws IOException Is thrown if the input is bad.
     */
    static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String credentials, String token)
            throws IOException {

        // Load client secrets.
        File jsonFile = new File(credentials);
        InputStream in = new FileInputStream(jsonFile);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(SCOPES))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(token)))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * createEmail
     * <p>
     *     This method will take in the elements needed to create an email and create a MimeMessage which is later
     *     passed into the sendEmail call in the main method.
     * </p>
     *
     * @param to is who the email is going to, so in this case it's going to the same person it's from
     * @param from is who the email is from, and in this case it's going to the same person as it's to.
     * @param subject is just some default text and isn't important
     * @param bodyText same as a subject.
     * @return a MimeMessage object that is passed into the sendEmail call.
     * @throws MessagingException in case, the format of the returned object is invalid.
     */
    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * sendMesssage
     * <p>
     *     This method is used to send the email generated with the token and credentials we've read in and proceesed.
     * </p>
     *
     * @param service is the variable we encapsulate with the user, message and execute to send the email.
     * @param userId is what we use to distinguish who we are sending the email to for the Gmail API servers.
     * @param emailContent is the MimeMessage we generated in createEmail.
     * @throws MessagingException if the message is built incorrectly.
     * @throws IOException if the input is invalid.
     */
    public static void sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        service.users().messages().send(userId, message).execute();
    }
}
