package it.dawidwojdyla.controller.oauth;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.ObtainAuthorizationCodeWindowController;
import it.dawidwojdyla.view.ViewFactory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;



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

    private final ViewFactory viewFactory;
    private final EmailManager emailManager;

    public Oauth(EmailManager emailManager, ViewFactory viewFactory) {
        this.emailManager = emailManager;
        this.viewFactory = viewFactory;
        loadTokens();
        loadCredentials();
    }

    public String getAccessToken() throws IOException {
        if (accessToken == null || refreshToken == null) {
            obtainAuthorizationCode();
        } else if (System.currentTimeMillis() > tokenExpires) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void loadCredentials() {
        //just for now
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
        //have to be in a file
        refreshToken = "1//0c5kqYS9Ilc7qCgYIARAAGAwSNwF-L9IrQPfAxAvljVGXslH8VB3ebOcm6JWWCcQDX6r_GvaLrOkDgYYSMBcrBC8PdAZjOXfcLSQ";
        accessToken = "ya29.a0AfH6SMAL8nHWYgglIVoJTPXuHu1r54HympvojBscaUFU3PzPs7tyDU7Ax6gtRW0wMZdLwyF4Q0AsOzDiZflt7vESNEVphtzG78uEcGEmNmWqcUe3EUmYPlV7OVtHbmXJH36HoesVW2dS0aHgDxmIAN5kJv8s_9KFrcwT3LNxYrs";
        tokenExpires = 1609265566522L;
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
        String authorizationCode = "";
        authorizationCode = url.substring(url.indexOf("code=") + 5);
        if (authorizationCode.contains("&")) {
            authorizationCode = authorizationCode.substring(0, authorizationCode.indexOf("&"));
        }
        return authorizationCode;
    }

    private void manageWithAuthorizationCode() {
        if(authorizationCode != null && !authorizationCode.equals("")) {
            try {
                exchangeAuthorizationCodeForTokens();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exchangeAuthorizationCodeForTokens() throws IOException {
        String request = buildExchangeRequest();
        HttpURLConnection connection = buildConnection(request);

        HashMap<String, Object> result;
        result = new ObjectMapper().readValue(connection.getInputStream(), new TypeReference<>() {
        });
        accessToken = (String) result.get("access_token");
        refreshToken = (String) result.get("refresh_token");
        tokenExpires = System.currentTimeMillis() + ((Number)result.get("expires_in")).intValue() * 1000;
    }

    private String buildExchangeRequest() {
        return "code=" + authorizationCode +
                "&client_id=" + oauthClientId +
                "&client_secret=" + oauthClientSecret +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code";
    }

    private HttpURLConnection buildConnection(String request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(tokenUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
        printWriter.print(request);
        printWriter.flush();
        printWriter.close();
        connection.connect();
        return connection;
    }

    private void refreshAccessToken() throws IOException {
        String request = buildRefreshTokenRequest();
        HttpURLConnection connection = buildConnection(request);

        HashMap<String, Object> result;
        result = new ObjectMapper().readValue(connection.getInputStream(), new TypeReference<>(){});
        accessToken = (String) result.get("access_token");
        tokenExpires = System.currentTimeMillis() + ((Number)result.get("expires_in")).intValue() * 1000 - 5000;
    }

    private String buildRefreshTokenRequest() {
        return "client_id=" + oauthClientId +
                "&client_secret=" + oauthClientSecret +
                "&refresh_token="+ refreshToken +
                "&grant_type=refresh_token";
    }
}
