package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.services.EmailSenderService;
import it.dawidwojdyla.controller.services.MessageRendererService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2020-12-05.
 */
public class ComposeMessageWindowController extends AbstractController implements Initializable {

    private List<File> attachments = new ArrayList<>();

    private List<MimeBodyPart> attachmentsForForwarding = new ArrayList<>();

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private Label errorLabel;

    @FXML
    private HBox attachHBox;

    @FXML
    private ChoiceBox<EmailAccount> emailAccountChoiceBox;

    private ComposeMessageType messageType;

    public ComposeMessageWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName, ComposeMessageType messageType) {
        super(emailManager, viewFactory, fxmlName);
        this.messageType = messageType;
    }

    @FXML
    void sendButtonAction() {
        EmailSenderService emailSenderService = new EmailSenderService(
                emailAccountChoiceBox.getValue(),
                subjectTextField.getText(),
                recipientTextField.getText(),
                htmlEditor.getHtmlText(),
                attachments,
                attachmentsForForwarding);
        emailSenderService.start();
        emailSenderService.setOnSucceeded(e -> {
            EmailSendingResult emailSendingResult = emailSenderService.getValue();
            switch(emailSendingResult) {
                case SUCCESS:
                    Stage stage = (Stage) errorLabel.getScene().getWindow();
                    viewFactory.closeStage(stage);
                    break;
                case FAILED_BY_PROVIDER:
                    errorLabel.setText("Provider error!");
                    break;
                case FAILED_BY_UNEXPECTED_ERROR:
                    errorLabel.setText("Unexpected error!");
                    break;
            }
        });
    }

    @FXML
    void attachButtonAction() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null && !attachments.contains(selectedFile)) {
            attachments.add(selectedFile);
            addAttachmentToAttachBox(selectedFile, selectedFile.getName(), attachments);
        }
    }

    private void addAttachmentToAttachBox(Object attachment, String fileName, List attachmentList) {
        TextFlow attachmentTextFlow = new TextFlow();

        Button closingButton = new Button("x");
        closingButton.getStyleClass().add("attachRemoveButton");
        //closingButton.setStyle("-fx-base: #f3622d; -fx-margin:1px; -fx-padding: -1px 4px 1px 4px; -fx-font-weight: bold; -fx-cursor: hand;");

        closingButton.setOnAction(e -> {
            attachmentList.remove(attachment);
            attachHBox.getChildren().remove(attachmentTextFlow);
        });

        attachmentTextFlow.getChildren().addAll(new Text(fileName), closingButton);
        attachmentTextFlow.setMinWidth(TextFlow.USE_PREF_SIZE);

        attachHBox.getChildren().add(attachmentTextFlow);
    }

    private void addAttachmentsForForwardingToAttachBox() {
        for (MimeBodyPart attachmentBodyPart : attachmentsForForwarding) {
            try {
                addAttachmentToAttachBox(attachmentBodyPart, attachmentBodyPart.getFileName(), attachmentsForForwarding);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAccountChoiceBox.setItems(emailManager.getEmailAccounts());
        emailAccountChoiceBox.setValue(emailManager.getEmailAccounts().get(0));
        if (messageType != ComposeMessageType.DEFAULT) {
            loadMessageData();
        }
    }

    private void loadMessageData() {
        EmailMessage emailMessage = emailManager.getSelectedMessage();
        if (emailMessage.getMessageContent() == null) {
            MessageRendererService messageRendererService = new MessageRendererService(emailMessage);
            messageRendererService.restart();
            messageRendererService.setOnSucceeded(e -> setMessageData(emailMessage));
        } else {
            setMessageData(emailMessage);
        }
    }

    private void setMessageData(EmailMessage emailMessage) {
        if (messageType == ComposeMessageType.REPLY) {
            subjectTextField.setText("Re: " + emailMessage.getSubject());
            recipientTextField.setText(emailMessage.getSender());
            htmlEditor.setHtmlText(prepareReplayHtmlText(emailMessage));
        } else {
            subjectTextField.setText("Fwd: " + emailMessage.getSubject());
            htmlEditor.setHtmlText(prepareForwardHtmlText(emailMessage));
            if (emailMessage.hasAttachment()) {
                attachmentsForForwarding.addAll(emailMessage.getAttachmentList());
                addAttachmentsForForwardingToAttachBox();
            }
        }
    }

    private String prepareReplayHtmlText(EmailMessage message) {
        String htmlText = "<br><br>On " + message.getDate() + " - " + message.getRecipient() + " wrote:";
        htmlText += prepareMessageContent(message);
        return htmlText;
    }


    private String prepareForwardHtmlText(EmailMessage message) {
        String htmlText = "<br><br>" +
                "<table><tr><td colspan='2' style='text-align: center'>----- Forwarded message -----</td></tr>" +
                "<tr><td>From:</td><td>" + message.getSender() + "</td></tr>" +
                "<td>Date:</td><td>" + message.getDate() + "</td></tr>" +
                "<td>Subject:</td><td>" + message.getSubject() + "</td></tr>" +
                "<td>To:</td><td>" + message.getRecipient() + "</td></tr></table>";
        htmlText += prepareMessageContent(message);
        return htmlText;
    }

    private String prepareMessageContent(EmailMessage message) {
        String messageContent = "<blockquote style='border-left: 0.5px solid gray; padding: 5px; margin-left: 10px;'>";
        messageContent += message.getMessageContent();
        messageContent += "</blockquote>";
        return messageContent;
    }
}
