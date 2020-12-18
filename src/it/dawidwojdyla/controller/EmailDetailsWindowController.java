package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.services.MessageRendererService;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.view.ViewFactory;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2020-12-09.
 */
public class EmailDetailsWindowController extends AbstractController implements Initializable {

    private String LOCATION_OF_DOWNLOADS = System.getProperty("user.home") + "/Downloads/";

    @FXML
    private WebView webView;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label senderLabel;

    @FXML
    private Label attachmentLabel;

    @FXML
    private HBox hBoxDownloads;

    @FXML
    private ScrollPane attachScrollPane;

    public EmailDetailsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        EmailMessage emailMessage = emailManager.getSelectedMessage();
        subjectLabel.setText(emailMessage.getSubject());
        senderLabel.setText(emailMessage.getSender());

        if(emailMessage.hasAttachment()) {
            loadAttachments(emailMessage);
        } else {
            attachmentLabel.setVisible(false);
        }

        if (emailMessage.getMessageContent() == null) {
            MessageRendererService messageRendererService = new MessageRendererService(webView.getEngine());
            messageRendererService.setEmailMessage(emailMessage);
            messageRendererService.restart();
        } else {
            webView.getEngine().loadContent(emailMessage.getMessageContent());
        }
    }

    private void loadAttachments(EmailMessage emailMessage) {
        for (MimeBodyPart mimeBodyPart: emailMessage.getAttachmentList()) {
            try {
                Button button = new AttachmentButton(mimeBodyPart);
                button.setMinWidth(120);
                hBoxDownloads.getChildren().add(button);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }
    }

    private class AttachmentButton extends Button {

        private MimeBodyPart mimeBodyPart;
        private String downloadedFilePath;

        public AttachmentButton(MimeBodyPart mimeBodyPart) throws MessagingException {
            this.mimeBodyPart = mimeBodyPart;
            this.setText(mimeBodyPart.getFileName());
            this.downloadedFilePath = LOCATION_OF_DOWNLOADS + this.getText();

            this.setOnAction(e -> downloadAttachment());
        }

        private void downloadAttachment() {
            colorBlue();
            Service downloadingService = new Service() {
                @Override
                protected Task createTask() {
                    return new Task() {
                        @Override
                        protected Void call() throws Exception {
                            mimeBodyPart.saveFile(downloadedFilePath);
                            return null;
                        }
                    };
                }
            };
            downloadingService.restart();
            downloadingService.setOnSucceeded(e -> {
                colorGreen();
                this.setOnAction(e2 -> {
                    File file = new File(downloadedFilePath);
                    Desktop desktop = Desktop.getDesktop();
                    if (file.exists()) {
                        try {
                            desktop.open(file);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });
            });
        }

        private void colorBlue() {
            this.setStyle("-fx-background-color: blue;");
        }

        private void colorGreen() {
            this.setStyle("-fx-background-color: green;");
        }
    }
}
