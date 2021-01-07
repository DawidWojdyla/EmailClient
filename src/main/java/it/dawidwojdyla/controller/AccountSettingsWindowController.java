package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.oauth.Oauth;
import it.dawidwojdyla.controller.oauth.OauthAuthorizingController;
import it.dawidwojdyla.controller.services.LoginService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private boolean loginFailedFlag = false;
    private boolean authorizationFailedFlag = false;

    private boolean switchingToOauthFlag = false;
    private boolean switchingToDefaultFlag = false;
    private boolean alertCancelFlag = false;

    private EmailAccount emailAccount;
    String lastPassword;
    String lastOutgoingHost;
    String lastIncomingHost;
    boolean wasOauth;

    private Oauth oauth;
    private Stage stage;

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
        if (stage == null) {
            stage = (Stage) passwordField.getScene().getWindow();
            stage.setOnCloseRequest(e -> {
                if (oauth != null) {
                    oauth.closeAuthorizationStage();
                }
                rollBackAccountSettings();
            });
        }
        if (isActionEnabled) {
            isActionEnabled = false;
            errorLabel.setText("");
            System.out.println("AccountSettingsWindowController: isActionEnabled==true");
            if(loginFailedFlag) {
                loginFailedApplyButtonAction();
            } else {
                defaultApplyButtonAction();
            }
        } else {
            errorLabel.setText("Wait for end of current action...");
        }
    }

    private void loginFailedApplyButtonAction() {
        loginFailedFlag = false;
        Alert alert = new Alert(Alert.AlertType.WARNING, "Save the settings anyway?", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Login failed!");
        alert.setHeaderText(null);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            closeStage();
        } else {
            alertCancelFlag = true;
            isActionEnabled = true;
        }
    }

    private void defaultApplyButtonAction() {
        if (oauthCheckBox.isSelected()) {
            manageOauth();
        } else if (wasOauth) {
            if (!switchingToDefaultFlag) {
                switchToDefaultMailProperties();
                switchingToDefaultFlag = true;
                switchingToOauthFlag = false;
            }
            relogin(false);
        } else if (!passwordField.getText().equals(lastPassword) ||
                !incomingHostField.getText().equals(lastIncomingHost) ||
                !outgoingHostField.getText().equals(lastOutgoingHost)) {
            relogin(false);
        } else {
            closeStage();
        }
    }

    @FXML
    void cancelButtonAction() {
        System.out.println("AccountSettingsWindowController: cancelButtonAction()");
        if (isActionEnabled) {
            if (loginFailedFlag || authorizationFailedFlag || alertCancelFlag) {
                rollBackAccountSettings();
            }
            closeStage();
        } else if(oauth != null) {
            oauth.closeAuthorizationStage();
            authorizationFailedAction("Authorization has been canceled");
        }
    }

    @Override
    public void authorizationFailedAction(String errorMessage) {
        System.out.println("AccountSettingsWindowController: authorizationFailed()");
        errorLabel.setText(errorMessage);
        oauth = null;
        authorizationFailedFlag = true;
        isActionEnabled = true;
    }

    private void rollBackAccountSettings() {
        emailAccount.setPassword(lastPassword);
        emailAccount.getProperties().setProperty("incomingHost", lastIncomingHost);
        emailAccount.getProperties().setProperty("outgoingHost", lastOutgoingHost);

        if (switchingToOauthFlag) {
            switchingToOauthFlag = false;
            switchToDefaultMailProperties();
        }
        if (switchingToDefaultFlag) {
            switchingToDefaultFlag = false;
            switchToOauthMailProperties();
        }
    }

    private void manageOauth() {
        System.out.println("AccountSettingsWindowController: manageOauth()");
        if (wasOauth) {
            continueWithOauth();
        } else {
            switchToOauth();
        }
    }

    private void continueWithOauth() {
        System.out.println("AccountSettingsWindowController: continueWithOauth()");
        if (!lastPassword.equals(passwordField.getText())) {
            startOauthAuthorization();
        } else if (!incomingHostField.getText().equals(lastIncomingHost) ||
                !outgoingHostField.getText().equals(lastOutgoingHost)) {
            relogin(true);
        } else {
            closeStage();
        }
    }

    private void switchToOauth() {
        System.out.println("AccountSettingsWindowController: switchToOauth()");
        if (!switchingToOauthFlag) {
            switchingToOauthFlag = true;
            switchingToDefaultFlag = false;
            switchToOauthMailProperties();
        }
        if (!emailAccount.getProperties().containsKey("access_token") ||
                !emailAccount.getProperties().containsKey("refresh_token") ||
                !emailAccount.getProperties().containsKey("token_expires") ||
                !lastPassword.equals(passwordField.getText())) {
            startOauthAuthorization();
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

    private void updateEmailAccountFromFields() {
        System.out.println("AccountSettingsWindowController: updateFields()");
        emailAccount.setPassword(passwordField.getText());
        emailAccount.getProperties().setProperty("incomingHost", incomingHostField.getText());
        emailAccount.getProperties().setProperty("outgoingHost", outgoingHostField.getText());
    }

    private void startOauthAuthorization() {
        System.out.println("AccountSettingsWindowController: startOauthAuthorization()");
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
        updateEmailAccountFromFields();
        System.out.println("AccountSettingsWindowController: relogin(isOauth: " + isOauth + ")");
        LoginService loginService = new LoginService(emailAccount, emailManager, isOauth);
        System.out.println("AccountSettingsWindowController: newLoginService()");
        loginService.setOnSucceeded(event -> {
            oauth = null;
            EmailLoginResult emailLoginResult = loginService.getValue();
            if (emailLoginResult == EmailLoginResult.SUCCESS) {
                System.out.println("AccountSettingsWindowController: Login -> success");
                emailManager.removeInvalidEmailAccount(emailAccount);
                closeStage();
            } else {
                loginFailedFlag = true;
                isActionEnabled = true;
                System.out.println("AccountSettingsWindowController: Login -> error");
                errorLabel.setText("Login Failed!");
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
                addListener((observableValue, oldChoice, newChoice) -> setCurrentAccount(newChoice));

        passwordField.textProperty().addListener(e -> loginFailedFlag = false);
        incomingHostField.textProperty().addListener(e -> loginFailedFlag = false);
        outgoingHostField.textProperty().addListener(e -> loginFailedFlag = false);
        oauthCheckBox.selectedProperty().addListener(e -> loginFailedFlag = false);

        ObservableList<EmailAccount> accounts = FXCollections.observableArrayList();
        accounts.addAll(emailManager.getEmailAccounts());
        accounts.addAll(emailManager.getInvalidEmailAccounts());
        emailAccountChoiceBox.setItems(accounts);
        emailAccountChoiceBox.setValue(emailManager.getEmailAccounts().get(0));
    }

    private void setCurrentAccount(EmailAccount newChoice) {
        if (isActionEnabled) {
            if(authorizationFailedFlag || loginFailedFlag || alertCancelFlag) {
                loginFailedFlag = false;
                alertCancelFlag = false;
                authorizationFailedFlag = false;
                rollBackAccountSettings();
            }
            errorLabel.setText("");
            emailAccount = newChoice;
            lastPassword = newChoice.getPassword();
            lastIncomingHost = newChoice.getProperties().getProperty("incomingHost");
            lastOutgoingHost = newChoice.getProperties().getProperty("outgoingHost");
            wasOauth = newChoice.getProperties().containsValue("XOAUTH2");

            passwordField.setText(lastPassword);
            incomingHostField.setText(lastIncomingHost);
            outgoingHostField.setText(lastOutgoingHost);
            oauthCheckBox.setSelected(wasOauth);
        } else {
            emailAccountChoiceBox.setValue(emailAccount);
        }
    }
}
