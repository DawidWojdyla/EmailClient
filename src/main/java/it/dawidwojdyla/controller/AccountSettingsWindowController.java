package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.oauth.Oauth;
import it.dawidwojdyla.controller.oauth.OauthAuthorizingController;
import it.dawidwojdyla.controller.services.LoginService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2021-01-02.
 */
public class AccountSettingsWindowController extends OauthAuthorizingController implements Initializable {

    @FXML
    private ChoiceBox<EmailAccount> emailAccountChoiceBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField incomingHostField;

    @FXML
    private TextField outgoingHostField;

    @FXML
    private CheckBox oauthCheckBox;

    private boolean isActionEnabled = true;

    private EmailAccount emailAccount;

    private boolean switchingToOauthRollBackFlag = false;

    private Oauth oauth;

    public AccountSettingsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @Override
    public void loginUsingOAuth(Properties tokens) {
        emailAccount.getProperties().putAll(tokens);
        relogin(true);
    }

    @FXML
    void applyButtonAction() {
        System.out.println("AccountSettingsWindowController: applyButtonAction()");
        if (isActionEnabled) {
            errorLabel.setText("");
            System.out.println("AccountSettingsWindowController: isActionEnabled==true");
            emailAccount = emailAccountChoiceBox.getValue();
            if (oauthCheckBox.isSelected()) {
                manageOauth();
            } else if (emailAccount.getProperties().containsValue("XOAUTH2")) {
                switchToDefaultMailProperties();
                relogin(false);
            } else if (!passwordField.getText().equals(emailAccount.getPassword()) ||
                    !incomingHostField.getText().equals(emailAccount.getProperties().getProperty("incomingHost")) ||
                    !outgoingHostField.getText().equals(emailAccount.getProperties().getProperty("outgoingHost"))) {
                relogin(false);
            } else {
                closeStage();
            }
        } else {
            errorLabel.setText("OAuth2 authorization flow in progress...");
        }
    }

    @FXML
    void cancelButtonAction() {
        System.out.println("AccountSettingsWindowController: cancelButtonAction()");
        if (isActionEnabled) {
            closeStage();
        } else if(oauth != null) {
            oauth.closeAuthorizationStage();
            authorizationFailed("Authorization has been canceled");
            oauth = null;
        }
    }

    private void manageOauth() {
        System.out.println("AccountSettingsWindowController: manageOauth()");
        if (emailAccount.getProperties().containsValue("XOAUTH2")) {
            continueWithOauth();
        } else {
            switchToOauth();
        }
    }

    private void continueWithOauth() {
        System.out.println("AccountSettingsWindowController: continueWithOauth()");
        if (!emailAccount.getPassword().equals(passwordField.getText())) {
            startOauthAuthorization();
        } else if (!incomingHostField.getText().equals(emailAccount.getProperties().getProperty("incomingHost")) ||
                !outgoingHostField.getText().equals(emailAccount.getProperties().getProperty("outgoingHost"))) {
            relogin(true);
        } else {
            closeStage();
        }
    }

    private void switchToOauth() {
        System.out.println("AccountSettingsWindowController: switchToOauth()");
        switchToOauthMailProperties();
        if (!emailAccount.getProperties().containsKey("access_token") ||
                !emailAccount.getProperties().containsKey("refresh_token") ||
                !emailAccount.getProperties().containsKey("token_expires") ||
                !emailAccount.getPassword().equals(passwordField.getText())) {
            startOauthAuthorization();
            switchingToOauthRollBackFlag = true;
        } else {
            long tokenExpires = Long.parseLong(emailAccount.getProperties().getProperty("token_expires"));
            if (System.currentTimeMillis() > tokenExpires) {
                System.out.println("AccountSettingsWindowController: token expires...");
                Oauth oauth = new Oauth(emailManager.getOauthProperties(), emailAccount.getProperties());
                Service<Void> service = new Service<>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<>() {
                            @Override
                            protected Void call() throws IOException {
                                System.out.println("AccountSettingsWindowController: call -> oauth.refreshAccessToken()");
                                oauth.refreshAccessToken();
                                return null;
                            }
                        };
                    }
                };
                service.setOnSucceeded(e -> relogin(true));
                service.start();
            } else {
                relogin(true);
            }
        }
    }

    @Override
    public void authorizationFailed(String errorMessage) {
        System.out.println("AccountSettingsWindowController: authorizationFailed()");
        errorLabel.setText(errorMessage);
        isActionEnabled = true;
        if (switchingToOauthRollBackFlag) {
            switchToDefaultMailProperties();
            switchingToOauthRollBackFlag = false;
        }
    }

    private void switchToOauthMailProperties() {
        System.out.println("AccountSettingsWindowController: switchToOauthMailProperties()");
        removeDefaultMailProperties();
        addOauthMailProperties();
    }

    private void switchToDefaultMailProperties() {
        System.out.println("AccountSettingsWindowController: switchToDefaultMailProperties()");
        removeOauthMailProperties();
        addDefaultMailProperties();
    }

    private void updateFields() {
        System.out.println("AccountSettingsWindowController: updateFields()");
        emailAccount.setPassword(passwordField.getText());
        emailAccount.getProperties().setProperty("incomingHost", incomingHostField.getText());
        emailAccount.getProperties().setProperty("outgoingHost", outgoingHostField.getText());
    }

    private void startOauthAuthorization() {
        System.out.println("AccountSettingsWindowController: startOauthAuthorization()");
        isActionEnabled = false;
        Oauth oauth = new Oauth(this);
        oauth.startNewAutorization(emailAccount.getAddress());
        this.oauth = oauth;
    }

    private void addDefaultMailProperties() {
        System.out.println("AccountSettingsWindowController: addDefaultMailProperties()");
        emailAccount.getProperties().putAll(emailManager.getDefaultMailProperties());
    }

    private void addOauthMailProperties() {
        System.out.println("AccountSettingsWindowController: addOauthMailProperties()");
        emailAccount.getProperties().putAll(emailManager.getOauthDefaultMailProperties());
    }

    private void removeDefaultMailProperties() {
        System.out.println("AccountSettingsWindowController: removeDefaultMailProperties()");
        Properties defaultMailProperties = emailManager.getDefaultMailProperties();
        for(String key: defaultMailProperties.stringPropertyNames()) {
            emailAccount.getProperties().remove(key);
        }
    }

    private void removeOauthMailProperties() {
        System.out.println("AccountSettingsWindowController: removeOauthMailProperties()");
        Properties oauthMailProperties = emailManager.getOauthDefaultMailProperties();
        for(String key: oauthMailProperties.stringPropertyNames()) {
            emailAccount.getProperties().remove(key);
        }
    }

    private void relogin(boolean isOauth) {
        //Maybe there should be some tempFields for rolling back when emailLoginResult not SUCCESS
        updateFields();
        System.out.println("AccountSettingsWindowController: relogin(isOauth: " + isOauth + ")");
        LoginService loginService = new LoginService(emailAccount, emailManager, isOauth);
        System.out.println("AccountSettingsWindowController: newLoginService()");
        loginService.setOnSucceeded(event -> {
            EmailLoginResult emailLoginResult = loginService.getValue();
            switch(emailLoginResult) {
                case SUCCESS:
                    System.out.println("AccountSettingsWindowController: Login -> success");
                    closeStage();
                    return;
                case FAILED_BY_CREDENTIALS:
                    System.out.println("AccountSettingsWindowController: Login -> invalid credential");
                    errorLabel.setText("Invalid credencials!");
                    return;
                case FAILED_BY_UNEXPECTED_ERROR:
                    System.out.println("AccountSettingsWindowController: Login -> unexpected error");
                    errorLabel.setText("Unexpected error!");
                    return;
                default:
            }
        });
        loginService.start();
    }

    private void closeStage() {
        System.out.println("AccountSettingsWindowController: closeStage()");
        viewFactory.closeStage((Stage) oauthCheckBox.getScene().getWindow());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAccountChoiceBox.getSelectionModel().selectedItemProperty().
                addListener((observableValue, oldChoice, newChoice) -> setTextFields(newChoice));
        emailAccountChoiceBox.setItems(emailManager.getEmailAccounts());
        emailAccountChoiceBox.setValue(emailManager.getEmailAccounts().get(0));
    }

    private void setTextFields(EmailAccount newChoice) {
        passwordField.setText(newChoice.getPassword());
        incomingHostField.setText(newChoice.getProperties().getProperty("incomingHost"));
        outgoingHostField.setText(newChoice.getProperties().getProperty("outgoingHost"));
        oauthCheckBox.setSelected(newChoice.getProperties().containsValue("XOAUTH2"));
    }
}
