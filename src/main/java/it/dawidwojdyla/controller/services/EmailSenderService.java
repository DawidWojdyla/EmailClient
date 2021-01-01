package it.dawidwojdyla.controller.services;

import it.dawidwojdyla.controller.EmailSendingResult;
import it.dawidwojdyla.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;

/**
 * Created by Dawid on 2020-12-05.
 */
public class EmailSenderService extends Service<EmailSendingResult> {

    private EmailAccount emailAccount;
    private String subject;
    private String recipient;
    private String messageContent;
    private List<File> attachments;
    private List<MimeBodyPart> attachmentsForForwarding;


    public EmailSenderService(EmailAccount emailAccount, String subject, String recipient, String messageContent,
                              List<File> attachments, List<MimeBodyPart> attachmentsForForwarding) {
        this.emailAccount = emailAccount;
        this.subject = subject;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.attachments = attachments;
        this.attachmentsForForwarding = attachmentsForForwarding;
    }

    @Override
    protected Task<EmailSendingResult> createTask() {
        return new Task<>() {
            @Override
            protected EmailSendingResult call() {
                try {
                    MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
                    buildMessage(mimeMessage);
                    Transport transport = emailAccount.getSession().getTransport();
                    transport.connect(
                            emailAccount.getProperties().getProperty("outgoingHost"),
                            emailAccount.getAddress(),
                            getAuthenticator());
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    transport.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return EmailSendingResult.FAILED_BY_UNEXPECTED_ERROR;
                }
                return EmailSendingResult.SUCCESS;
            }

            private void buildMessage(MimeMessage mimeMessage) throws MessagingException {
                mimeMessage.setFrom(emailAccount.getAddress());
                mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                mimeMessage.setSubject(subject);

                Multipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(messageContent, "text/html; charset=UTF-8");
                multipart.addBodyPart(messageBodyPart);
                addAttachments(multipart);
                mimeMessage.setContent(multipart);
            }

            private void addAttachments(Multipart multipart) throws MessagingException {
                for (MimeBodyPart attachment : attachmentsForForwarding) {
                    multipart.addBodyPart(attachment);
                }
                for (File attachment : attachments) {
                    if (attachment.exists()) {
                        BodyPart attachmentBodyPart = new MimeBodyPart();
                        DataSource dataSource = new FileDataSource(attachment.getAbsolutePath());
                        attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
                        attachmentBodyPart.setFileName(attachment.getName());
                        multipart.addBodyPart(attachmentBodyPart);
                    }
                }
            }
        };
    }

    private String getAuthenticator() {
        if (emailAccount.getProperties().containsValue("XOAUTH2")) {
          return  emailAccount.getProperties().getProperty("access_token");
        } else {
            return emailAccount.getPassword();
        }
    }
}
