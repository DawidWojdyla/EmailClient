package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        System.out.println("click");

    }
}