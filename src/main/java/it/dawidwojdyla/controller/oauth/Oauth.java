package it.dawidwojdyla.controller.oauth;
import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.ObtainAuthorizationCodeWindowController;
import it.dawidwojdyla.view.ViewFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Created by Dawid on 2020-12-27.
 */
public class Oauth {

    private String oauthClientId;
    private String oauthClientSecret;

    private String refreshToken;
    private String accessToken;
    private String authorizationCode;
    private long tokenExpires;
    private String tokenUrl;


    private String authorizationCodeUrl;
    private String scope;
    private String securityToken;
    private String redirectUri;

    private ViewFactory viewFactory;
    private EmailManager emailManager;

    public Oauth(EmailManager emailManager, ViewFactory viewFactory) {
        this.emailManager = emailManager;
        this.viewFactory = viewFactory;
        loadTokens();
        loadCredentials();
    }

    public String getAccessToken() {
        if (accessToken == null || refreshToken == null) {
            obtainAuthorizationCode();
        } else if (System.currentTimeMillis() > tokenExpires) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void loadCredentials() {
        //just for now (have to be in a file)
        oauthClientId = "1097648184338-d4h0ojjclgf4ng6ap7vgc4bbu2d3sfu1.apps.googleusercontent.com";
        oauthClientSecret = "AmB8QDH9NilclJNiwR_OJriI";
        authorizationCodeUrl = "https://accounts.google.com/o/oauth2/v2/auth";
        scope = "https://mail.google.com/";
        tokenUrl = "https://oauth2.googleapis.com/token";
        redirectUri = "http://localhost/authorization-code/callback";

        //should be generated :
        securityToken = "security_token%3DALAMAKOTAKOTau3e1%26url%3Dhttps%3A%2F%2Foauth2.example.com%2Ftoken";
    }

    private void loadTokens() {
        //will be in a file
        refreshToken = null;
        accessToken = null;
        tokenExpires = 0;
    }

    private void obtainAuthorizationCode() {
        ObtainAuthorizationCodeWindowController authorizationCodeWindowController =
                new ObtainAuthorizationCodeWindowController(emailManager, viewFactory, "ObtainAuthorizationCodeWindow.fxml");
        viewFactory.showObtainAuthorizationCodeWindow(authorizationCodeWindowController);
        listenForAuthorizationCode(authorizationCodeWindowController.getWebView());
    }

    public void listenForAuthorizationCode(WebView webView) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.setOnCloseRequest(e -> manageWithAuthorizationCode());

        webView.getEngine().locationProperty().addListener((observableValue, oldAddress, newAddress) -> {
            if(newAddress.startsWith(redirectUri)) {
                if (!newAddress.contains("error")) {
                    authorizationCode = extractAuthorizationCodeFromURL(newAddress);
                }
                manageWithAuthorizationCode();
                viewFactory.closeStage(stage);
            }
        });
        webView.getEngine().load(buildAuthorizationURL());
    }

    private String buildAuthorizationURL() {
        return authorizationCodeUrl +
                "?scope=" + scope +
                "&response_type=code" +
                "&state=" + securityToken +
                "&redirect_uri=" + redirectUri +
                "&client_id=" + oauthClientId;
    }

    private String extractAuthorizationCodeFromURL(String url) {
        //return authorization code method
        return "";
    }

    private void manageWithAuthorizationCode() {
        //exchange authorization code for access and refresh tokens
    }

    private void refreshAccessToken()  {
        //refresh token mothod

    }


}
