package ch.axa.ita.personenverwaltung.utility;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static ch.axa.ita.personenverwaltung.utility.Password.password;

public class Mail {
    private static final String USER = "raphael.werner.suter@gmail.com";
    private static final String HOST_PROPERTY = "mail.smtp.host";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT_PROPERTY = "mail.smtp.port";
    private static final String PORT = "465";
    private static final String SSL_PROPERTY = "mail.smtp.ssl.enable";
    private static final String SSL = "true";
    private static final String AUTH_PROPERTY = "mail.smtp.auth";
    private static final String AUTH = "true";

    public static void send(String email, String subject, String text) {
        try {
            MimeMessage message = new MimeMessage(getSession());

            message.setFrom(new InternetAddress(USER));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Session getSession() {
        Properties properties = System.getProperties();

        properties.put(HOST_PROPERTY, HOST);
        properties.put(PORT_PROPERTY, PORT);
        properties.put(SSL_PROPERTY, SSL);
        properties.put(AUTH_PROPERTY, AUTH);

        return Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, password());
            }
        });
    }
}
