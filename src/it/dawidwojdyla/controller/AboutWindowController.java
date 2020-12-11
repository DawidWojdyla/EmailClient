package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by Dawid on 2020-12-11.
 */
public class AboutWindowController extends AbstractController {

    @FXML
    private Button okButton;

    public AboutWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void okButtonAction() {
        viewFactory.closeStage((Stage)okButton.getScene().getWindow());
    }
}
