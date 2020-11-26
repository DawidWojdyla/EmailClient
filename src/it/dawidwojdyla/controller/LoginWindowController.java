package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;
import javafx.event.ActionEvent;
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
        System.out.println("loginButtonAction");

        //when login is successful (show main window and close login window)
        viewFactory.showMainWindow();

        Stage stage = (Stage) errorLabel.getScene().getWindow();
        viewFactory.closeStage(stage);


    }
}
