package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2021-01-02.
 */
public class AccountSettingsWindowController extends AbstractController implements Initializable {

    @FXML
    private ChoiceBox<EmailAccount> emailAccountChoiceBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField incomingHostField;

    @FXML
    private TextField outgoingHostField;

    @FXML
    private CheckBox oauthCheckBox;


    public AccountSettingsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void applyButtonAction() {
        EmailAccount emailAccount = emailAccountChoiceBox.getValue();
        if (oauthCheckBox.isSelected()) {
            //OAUTH

            if (emailAccount.getProperties().containsValue("XOAUTH2")) {
                //OAUTH -> OAUTH
                if(!emailAccount.getPassword().equals(passwordField.getText())) {
                    //new authorization needed then relog

                } else if (!incomingHostField.getText().equals(emailAccount.getProperties().getProperty("incomingHost")) ||
                        !outgoingHostField.getText().equals(emailAccount.getProperties().getProperty("outgoingHost"))) {
                    //relog
                }
            } else {
                //NoOauth -> oauth

                //remove oauthMailProperties and add noOauthDefaultProperties

                if (!emailAccount.getProperties().containsKey("refresh_token") || !emailAccount.getPassword().equals(passwordField.getText())) {
                    //new authorization needed then relog
                } else {
                    //refresh access_token if needed then relog
                }
            }
        } else {
            //NoOauth

            if (emailAccount.getProperties().containsValue("XOAUTH2")) {
                //Oauth -> NoOauth

                //remove oauthMailProperties and add noOauthDefaultProperties and relog
                //NoOauth -> NoOauth
            } else if (!passwordField.getText().equals(emailAccount.getPassword()) ||
                    !incomingHostField.getText().equals(emailAccount.getProperties().getProperty("incomingHost")) ||
                    !outgoingHostField.getText().equals(emailAccount.getProperties().getProperty("outgoingHost"))){
                //relog
            }
        }
    }

    @FXML
    void cancelButtonAction() {
        viewFactory.closeStage((Stage) oauthCheckBox.getScene().getWindow());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAccountChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldChoice, newChoice) -> {
            passwordField.setText(newChoice.getPassword());
            incomingHostField.setText(newChoice.getProperties().getProperty("incomingHost"));
            outgoingHostField.setText(newChoice.getProperties().getProperty("outgoingHost"));
            oauthCheckBox.setSelected(newChoice.getProperties().containsValue("XOAUTH2"));
        });
        emailAccountChoiceBox.setItems(emailManager.getEmailAccounts());
        emailAccountChoiceBox.setValue(emailManager.getEmailAccounts().get(0));
    }
}
