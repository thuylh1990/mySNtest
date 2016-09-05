/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author thuylh
 */
public class Email {

    private static void sendEmail(String attachFilename, List<String> failResult, long passed, long total) throws MessagingException, FileNotFoundException, IOException {

        Properties prop = new Properties();

        try {

            InputStream input = new FileInputStream("smtp-config.properties");

            prop.load(input);

            final String host = prop.getProperty("host") != null ? prop.getProperty("host") : "mail.cyberspace.vn";
            final int port = prop.getProperty("port") != null ? Integer.parseInt(prop.getProperty("port")) : 587;
            final String username = prop.getProperty("username") != null ? prop.getProperty("username") : "thuylh@cyberspace.vn";
            final String password = prop.getProperty("password") != null ? prop.getProperty("password") : "PassLaGi@@";
            final String recipients_to = prop.getProperty("recepients.TO") != null ? prop.getProperty("recepients.TO") : "thuylh@cyberspace.vn";
            final String recipients_cc = prop.getProperty("recepients.CC") != null ? prop.getProperty("recepients.CC") : "thuylh@cyberspace.vn";

            if (input != null) {

                input.close();

            }

            //Config mail server
            Properties props = System.getProperties();//new Properties();

            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.debug", "false");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(username, password);

                }

            });

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username)); // FROM address

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients_to)); // TO address

            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(recipients_cc)); // CC address

            message.setSentDate(new Date()); //Set sent Date

            message.setSubject("[SearchCC] Báo cáo kết quả test tập query"); // mail's SUBJECT

            //
            // This HTML mail have 2 parts, the BODY (with content) and the attached file
            //
            Multipart multipart = new MimeMultipart();

            //Create body part for the content
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            String content = createHTMLEmailContent(failResult, passed, total);

            messageBodyPart.setContent(content, "text/html; charset=UTF-8");

            multipart.addBodyPart(messageBodyPart);

            //Create body part for the attachment
            MimeBodyPart attachPart = new MimeBodyPart();

            DataSource source = new FileDataSource(attachFilename);

            attachPart.setDataHandler(new DataHandler(source));

            attachPart.setFileName(new File(attachFilename).getName());

            multipart.addBodyPart(attachPart);

            //set the multi-part as email's content
            message.setContent(multipart);

            //call send email method
            Transport.send(message);

            System.out.println("Email was sent successfully!!!");

        } catch (MessagingException e) {

            e.printStackTrace();

        }

    }

    private static String createHTMLEmailContent(List<String> failResult, long passed, long total) {

        String tbl_content = "<p><table style='width:100%'>"
                + "<tr><td><b>TT</b></td><td><b>Các query bị fail</b></td></tr>";

        int count = 1;

        if (!failResult.isEmpty()) {

            for (String fail : failResult) {

                tbl_content += "<tr><td>" + count + "</td>"
                        + "<td>" + fail + "</td></tr>";

                count++;

            }

        }

        tbl_content += "</table></p>";

        String content = "<html><body>"
                + "<p>Dear anh Hưng,</p>"
                + "<p>Em gửi kết quả test ngày hôm nay như sau:</p>"
                + "<p><h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + "Số testcase pass = " + passed + "/" + total + "</h3></p>"
                + tbl_content
                + "<p>Danh sách các query đầy đủ trong file đính kèm.</p>"
                + "<p>Trân trọng./.</p>"
                + "<p><i>This automatical email was sent from @thuylh.</i></p>"
                + "</body></html>";

        return content;

    }

}
