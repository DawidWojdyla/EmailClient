package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.oauth.Oauth;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by Dawid on 2020-12-27.
 */
public class ObtainAuthorizationCodeWindowController extends AbstractController implements Initializable {

    @FXML
    private WebView webView;

    public ObtainAuthorizationCodeWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    public WebView getWebView() {
        return webView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //oauth.listenForAuthorizationCode(webView);
    }
}