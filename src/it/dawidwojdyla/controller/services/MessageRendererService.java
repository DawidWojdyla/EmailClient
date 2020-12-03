package it.dawidwojdyla.controller.services;


import it.dawidwojdyla.model.EmailMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.BodyTerm;
import java.io.IOException;

/**
 * Created by Dawid on 2020-12-03.
 */
public class MessageRendererService extends Service {

    private EmailMessage emailMessage;
    private WebEngine webEngine;
    private StringBuffer stringBuffer;

    public MessageRendererService(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.stringBuffer = new StringBuffer();
        this.setOnSucceeded(event -> displayMessage());
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    loadMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private void displayMessage() {
        webEngine.loadContent(stringBuffer.toString());
    }

    private void loadMessage() throws MessagingException, IOException {
        stringBuffer.setLength(0); //clears the stringBuffer
        Message message = emailMessage.getMessage();
        String contentType = message.getContentType();
        if (isSimpleType(contentType)) {
            stringBuffer.append(message.getContent().toString());
        } else if (isMultipartType(contentType)) {
            Multipart multipart = (Multipart) message.getContent();
            for(int i = multipart.getCount()-1; i >= 0; i--) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String bodyPartContentType = bodyPart.getContentType();
                if(isSimpleType(bodyPartContentType)) {
                    stringBuffer.append(bodyPart.getContent().toString());
                }
            }
        }
    }

    private boolean isSimpleType(String contentType) {
        if(contentType.contains("TEXT/HTML") ||
        contentType.contains("mixed") ||
        contentType.contains("text")) {
            return true;
        }
        return false;
    }

    private boolean isMultipartType(String contentType) {
        if(contentType.contains("multipart")) {
            return true;
        }
        return false;
    }


}
