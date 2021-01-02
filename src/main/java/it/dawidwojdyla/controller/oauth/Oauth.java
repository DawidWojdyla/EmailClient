package it.dawidwojdyla.controller.oauth;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.LoginWindowController;
import it.dawidwojdyla.controller.ObtainAuthorizationCodeWindowController;
import it.dawidwojdyla.view.ViewFactory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Dawid on 2020-12-27.
 */
public class Oauth {

    private Properties oauthProperties;
    private Properties accountProperties;
    private String authorizationCode;
    private EmailManager emailManager;
    private ViewFactory viewFactory;
    private LoginWindowController loginWindowController;

    public Oauth(LoginWindowController loginWindowController) {
        this.loginWindowController = loginWindowController;
        emailManager = loginWindowController.getEmailManager();
        viewFactory = loginWindowController.getViewFactory();
        oauthProperties = emailManager.getOauthProperties();
    }

    public Oauth(Properties oauthProperties, Properties accountProperties) {
       this.accountProperties = accountProperties;
       this.oauthProperties = oauthProperties;
    }

    public void obtainAuthorizationCode() {
            ObtainAuthorizationCodeWindowController authorizationCodeWindowController =
                    new ObtainAuthorizationCodeWindowController(emailManager, viewFactory, "ObtainAuthorizationCodeWindow.fxml");
            viewFactory.showObtainAuthorizationCodeWindow(authorizationCodeWindowController);
            listenForAuthorizationCode(authorizationCodeWindowController.getWebView());
    }

    private void listenForAuthorizationCode(WebView webView) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.setOnCloseRequest(e -> {
            manageWithAuthorizationCode();
            loginWindowController.enableLoginAction();
        });

        webView.getEngine().locationProperty().addListener((observableValue, oldAddress, newAddress) -> {
            if(newAddress.startsWith(oauthProperties.getProperty("redirect_uri"))) {
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
        return oauthProperties.getProperty("authorization_server") +
                "?scope=" + oauthProperties.getProperty("scope") +
                "&response_type=code" +
                //"&state=" + securityToken +
                "&login_hint=" + loginWindowController.getEmailAddress() +
                "&redirect_uri=" + oauthProperties.getProperty("redirect_uri") +
                "&client_id=" + oauthProperties.getProperty("client_id");
    }

    private String extractAuthorizationCodeFromURL(String url) {
        String authorizationCode;
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
        Properties oauthTokens = new Properties();
        oauthTokens.put("access_token", result.get("access_token"));
        oauthTokens.put("refresh_token", result.get("refresh_token"));
        long tokenExpires = (System.currentTimeMillis() + ((Number)result.get("expires_in")).intValue() * 1000 - 5000);
        oauthTokens.put("token_expires", tokenExpires + "");
        loginWindowController.logUsingOAuth(oauthTokens);
    }

    private String buildExchangeRequest() {
        return "code=" + authorizationCode +
                "&client_id=" + oauthProperties.getProperty("client_id") +
                "&client_secret=" + oauthProperties.getProperty("client_secret") +
                "&redirect_uri=" + oauthProperties.getProperty("redirect_uri") +
                "&grant_type=authorization_code";
    }

    private HttpURLConnection buildConnection(String request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(oauthProperties.getProperty("token_server")).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
        printWriter.print(request);
        printWriter.flush();
        printWriter.close();
        connection.connect();
        return connection;
    }

    public void refreshAccessToken() throws IOException {
        String request = buildRefreshTokenRequest();
        HttpURLConnection connection = buildConnection(request);

        HashMap<String, Object> result;
        result = new ObjectMapper().readValue(connection.getInputStream(), new TypeReference<>() {
        });
        accountProperties.put("access_token", result.get("access_token"));
        long tokenExpires = (System.currentTimeMillis() + ((Number) result.get("expires_in")).intValue() * 1000 - 5000);
        accountProperties.put("token_expires", tokenExpires + "");
    }

    private String buildRefreshTokenRequest() {
        return "client_id=" + oauthProperties.getProperty("client_id") +
                "&client_secret=" + oauthProperties.getProperty("client_secret") +
                "&refresh_token="+ accountProperties.getProperty("refresh_token")+
                "&grant_type=refresh_token";
    }
}
