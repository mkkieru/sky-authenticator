package ke.co.skyworld.UTILS;

import java.util.Properties;

import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class SendMail {


    public static void sendMail(String userMail, String messageContnet) {
        // Recipient's email ID needs to be mentioned.
        String to = userMail.trim();

        // Sender's email ID needs to be mentioned
        String from = "mkieru55@gmail.com";

        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";
        String port = "587";

        // Get system properties
        Properties properties = new Properties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust", "*");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");


        // Get the default Session object.
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mkieru55@gmail.com", "markkierumark");
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("SKY AUTH OTP");

            // Now set the actual message
            message.setText(messageContnet);

            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


}
