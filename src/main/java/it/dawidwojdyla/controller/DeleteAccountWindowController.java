package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2021-01-08.
 */
public class DeleteAccountWindowController extends AbstractController implements Initializable {
    @FXML
    private ChoiceBox<EmailAccount> emailAccountChoiceBox;

    public DeleteAccountWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void cancelButtonAction() {
        viewFactory.closeStage((Stage) emailAccountChoiceBox.getScene().getWindow());
    }

    @FXML
    void deleteButtonAction() {
        if (emailAccountChoiceBox.getSelectionModel().getSelectedItem() != null) {
            EmailAccount emailAccount = emailAccountChoiceBox.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure to delete " + emailAccount.getAddress() + " ?", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Deleting account");
            alert.setHeaderText(null);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                deleteAccount(emailAccountChoiceBox.getSelectionModel().getSelectedItem());
            }
        }
    }

    private void deleteAccount(EmailAccount emailAccount) {
        emailManager.removeEmailAccount(emailAccount);
        viewFactory.closeStage((Stage) emailAccountChoiceBox.getScene().getWindow());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAccountChoiceBox.setItems(emailManager.getAllEmailAccounts());
        emailAccountChoiceBox.setValue(emailManager.getEmailAccounts().get(0));
    }
}
