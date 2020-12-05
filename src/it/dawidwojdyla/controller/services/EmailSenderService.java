package it.dawidwojdyla.controller.services;

import it.dawidwojdyla.controller.EmailSendingResult;
import it.dawidwojdyla.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

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
            protected Object call() throws Exception {
                return null;
            }
        };
    }
}
