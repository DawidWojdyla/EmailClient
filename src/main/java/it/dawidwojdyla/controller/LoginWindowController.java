package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.oauth.Oauth;
import it.dawidwojdyla.controller.services.LoginService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2020-11-26.
 */
public class LoginWindowController extends AbstractController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField emailAddressField;

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

    @FXML
    private CheckBox manualHostCheckBox;

    boolean isLoginActionBlocked = false;


    public LoginWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    public void enableLoginAction() {
        isLoginActionBlocked = false;
        errorLabel.setText("");
    }

    public String getEmailAddress() {
        return emailAddressField.getText();
    }

    public EmailManager getEmailManager() {
        return emailManager;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    @FXML
    void loginButtonActon() {
        if (!isLoginActionBlocked) {
            errorLabel.setText("");

            if (fieldsAreValid()) {
                if (oauthCheckBox.isSelected()) {
                    isLoginActionBlocked = true;
                    Oauth oauth = new Oauth(this);
                    oauth.obtainAuthorizationCode();
                } else {
                    Properties properties = new Properties();
                    properties.putAll(emailManager.getDefaultMailProperties());
                    logInToNewAccount(properties, false);
                }
            }
        } else {
            errorLabel.setText("OAuth2 authorization flow in progress...");
        }
    }

    public void logUsingOAuth(Properties oauthTokens) {
        if(oauthTokens.getProperty("access_token").equals("")) {
            errorLabel.setText("Couldn't get accessToken");
        } else {
            Properties properties = new Properties();
            properties.putAll(emailManager.getOauthDefaultMailProperties());
            properties.putAll(oauthTokens);
            logInToNewAccount(properties, true);
        }
        isLoginActionBlocked = false;
    }

    private void logInToNewAccount(Properties properties, boolean isOauth) {
        properties.put("incomingHost", incomingHostField.getText());
        properties.put("outgoingHost", outgoingHostField.getText());
        EmailAccount emailAccount = new EmailAccount(emailAddressField.getText(), passwordField.getText(), properties);
        LoginService loginService = new LoginService(emailAccount, emailManager, isOauth);
        loginService.start();
        loginService.setOnSucceeded(event -> {
            EmailLoginResult emailLoginResult = loginService.getValue();
            switch(emailLoginResult) {
                case SUCCESS:
                    if(!viewFactory.isMainViewInitialized()) {
                        viewFactory.showMainWindow();
                    }
                    Stage stage = (Stage) errorLabel.getScene().getWindow();
                    viewFactory.closeStage(stage);
                    return;
                case FAILED_BY_CREDENTIALS:
                    errorLabel.setText("Invalid credencials!");
                    return;
                case FAILED_BY_UNEXPECTED_ERROR:
                    errorLabel.setText("Unexpected error!");
                    return;
                default:
            }
        });
    }

    private boolean fieldsAreValid() {

        if(emailAddressField.getText().isEmpty() || !emailAddressField.getText().contains("@")) {
            errorLabel.setText("Please fill correct email");
            return false;
        }

        if(passwordField.getText().isEmpty()) {
            errorLabel.setText("Please fill password");
            return false;
        }

        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //just for now (3lines)
        emailAddressField.setText("dawidmailtest@gmail.com");
        passwordField.setText("Dawidmailtest123haslo");
        setStandardHosts(emailAddressField.getText());

        disableHostFields(true);
        manualHostCheckBox.setOnAction(e -> {
            boolean disableHostFieldCondition = !manualHostCheckBox.isSelected();
            disableHostFields(disableHostFieldCondition);
            if (disableHostFieldCondition) {
                setStandardHosts(emailAddressField.getText());
            }
        });

        emailAddressField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!manualHostCheckBox.isSelected()) {
                setStandardHosts(newValue);
            }
        });
    }

    private void setStandardHosts(String newValue) {
        String domain = newValue.substring(newValue.indexOf("@") + 1);
        incomingHostField.setText("imap." + domain);
        outgoingHostField.setText("smtp." + domain);
    }

    private void disableHostFields(boolean disableCondition) {
        incomingHostField.setDisable(disableCondition);
        outgoingHostField.setDisable(disableCondition);
    }
}