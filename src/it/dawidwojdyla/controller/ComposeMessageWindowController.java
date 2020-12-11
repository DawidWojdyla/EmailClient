package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.services.EmailSenderService;
import it.dawidwojdyla.model.EmailAccount;
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
    private String fileName;

    public ComposeMessageWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }


    @FXML
    void sendButtonAction() {
        EmailSenderService emailSenderService = new EmailSenderService(
                emailAccountChoiceBox.getValue(),
                subjectTextField.getText(),
                recipientTextField.getText(),
                htmlEditor.getHtmlText(),
                attachments);
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
            addAttachment(selectedFile);
        }
    }

    private void addAttachment(File selectedFile) {
        attachments.add(selectedFile);

        TextFlow textFlow = new TextFlow();

        Text closingSign = new Text(" x ");
        closingSign.setStyle("-fx-font-weight: bold; -fx-cursor: hand");
        closingSign.setOnMouseClicked(e -> {
            attachments.remove(selectedFile);
            attachHBox.getChildren().remove(textFlow);
        });

        String fileName = selectedFile.getName();
        if(fileName.length() > 20) {
            fileName = fileName.substring(0, 17) + "...";
        }

        textFlow.getChildren().addAll(new Text(fileName), closingSign);
        attachHBox.getChildren().add(textFlow);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAccountChoiceBox.setItems(emailManager.getEmailAccounts());
        emailAccountChoiceBox.setValue(emailManager.getEmailAccounts().get(0));
    }
}
