package it.dawidwojdyla.controller.services;

import it.dawidwojdyla.controller.EmailSendingResult;
import it.dawidwojdyla.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Dawid on 2020-12-05.
 */
public class EmailSenderService extends Service<EmailSendingResult> {

    private EmailAccount emailAccount;
    private String subject;
    private String recipient;
    private String messageContent;


    public EmailSenderService(EmailAccount emailAccount, String subject, String recipient, String messageContent) {
        this.emailAccount = emailAccount;
        this.subject = subject;
        this.recipient = recipient;
        this.messageContent = messageContent;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected EmailSendingResult call() {
                try {
                    //Create the message:
                    MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
                    mimeMessage.setFrom(emailAccount.getAddress());
                    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                    mimeMessage.setSubject(subject);
                    //Set content:
                    Multipart multipart = new MimeMultipart();
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(messageContent, "text/html");
                    multipart.addBodyPart(messageBodyPart);
                    mimeMessage.setContent(multipart);
                    //sending the message:
                    Transport transport = emailAccount.getSession().getTransport();
                    transport.connect(
                            emailAccount.getProperties().getProperty("outgoingHost"),
                            emailAccount.getAddress(),
                            emailAccount.getPassword());
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    transport.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return EmailSendingResult.FAILED_BY_UNEXPECTED_ERROR;
                }
                return EmailSendingResult.SUCCESS;
            }
        };
    }
}
