package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.servives.LoginService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Dawid on 2020-11-26.
 */
public class LoginWindowController extends AbstractController {

    @FXML
    private TextField emailAddressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public LoginWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void loginButtonActon() {
        System.out.println("LoginButtonAction!");

        if(fieldsAreValid()) {
            EmailAccount emailAccount = new EmailAccount(emailAddressField.getText(), passwordField.getText());
            LoginService loginService = new LoginService(emailAccount, emailManager);
            EmailLoginResult emailLoginResult = loginService.login();

            switch(emailLoginResult) {
                case SUCCESS:
                    System.out.println("Login succesfull! " + emailAccount);
                    viewFactory.showMainWindow();
                    Stage stage = (Stage) errorLabel.getScene().getWindow();
                    viewFactory.closeStage(stage);
                    return;
                // rest of cases
            }

        }

    }

    private boolean fieldsAreValid() {

        if(emailAddressField.getText().isEmpty()) {
            errorLabel.setText("Please fill email");
            return false;
        }

        if(passwordField.getText().isEmpty()) {
            errorLabel.setText("Please fill password");
            return false;
        }

        return true;

    }
}
