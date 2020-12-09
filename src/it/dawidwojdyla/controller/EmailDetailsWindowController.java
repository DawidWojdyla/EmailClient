package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2020-12-09.
 */
public class EmailDetailsWindowController extends AbstractController implements Initializable {

    @FXML
    private WebView webView;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label senderLabel;

    @FXML
    private Label attachmentLabel;

    @FXML
    private HBox hBoxDownloads;

    public EmailDetailsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
