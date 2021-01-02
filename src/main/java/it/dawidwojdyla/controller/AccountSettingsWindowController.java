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

    @FXML
    private CheckBox manualHostCheckBox;

    public AccountSettingsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void applyButtonAction() {

    }

    @FXML
    void cancelButtonAction() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
