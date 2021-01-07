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
import java.util.Properties;

/**
 * Created by Dawid on 2020-12-27.
 */
public class Oauth {

    private final Properties oauthProperties;
    private Properties accountProperties;
    private String authorizationCode;
    private EmailManager emailManager;
    private ViewFactory viewFactory;
    private OauthAuthorizingController controller;
    private Stage authorizationWindowStage;

    public Oauth(OauthAuthorizingController controller) {
        this.controller = controller;
        emailManager = controller.getEmailManager();
        viewFactory = controller.getViewFactory();
        oauthProperties = emailManager.getOauthProperties();
    }

    public Oauth(Properties oauthProperties, Properties accountProperties) {
       this.accountProperties = accountProperties;
       this.oauthProperties = oauthProperties;
    }

    public void closeAuthorizationStage() {
        if (authorizationWindowStage != null) {
            viewFactory.closeStage(authorizationWindowStage);
            authorizationWindowStage = null;
        }
    }

    public void startNewAutorization(String emailAddress) {
        System.out.println("OAUTH: startNewAutorization()");
            ObtainAuthorizationCodeWindowController authorizationCodeWindowController =
                    new ObtainAuthorizationCodeWindowController(emailManager, viewFactory, "ObtainAuthorizationCodeWindow.fxml");
            viewFactory.showObtainAuthorizationCodeWindow(authorizationCodeWindowController);
            listenForAuthorizationCode(authorizationCodeWindowController.getWebView(), emailAddress);
    }

    private void listenForAuthorizationCode(WebView webView, String emailAddress) {
        System.out.println("OAUTH: listenForAuthorizationCode()");
        authorizationWindowStage = (Stage) webView.getScene().getWindow();
        authorizationWindowStage.setOnCloseRequest(e -> {
            System.out.println("OAUTH: listenForAuthorizationCode() -> setOnCloseRequest()");
            manageWithAuthorizationCode();
        });

        webView.getEngine().locationProperty().addListener((observableValue, oldAddress, newAddress) -> {
            if(newAddress.startsWith(oauthProperties.getProperty("redirect_uri"))) {
                if (!newAddress.contains("error")) {
                    authorizationCode = extractAuthorizationCodeFromURL(newAddress);
                }
                manageWithAuthorizationCode();
                closeAuthorizationStage();
            }
        });
        webView.getEngine().load(buildAuthorizationURL(emailAddress));
    }

    private String buildAuthorizationURL(String emailAddress) {
        System.out.println("OAUTH: buildAuthorizationURL()");
        return oauthProperties.getProperty("authorization_server") +
                "?scope=" + oauthProperties.getProperty("scope") +
                "&response_type=code" +
                //"&state=" + securityToken +
                "&login_hint=" + emailAddress +
                "&redirect_uri=" + oauthProperties.getProperty("redirect_uri") +
                "&client_id=" + oauthProperties.getProperty("client_id");
    }

    private String extractAuthorizationCodeFromURL(String url) {
        System.out.println("OAUTH: extractAuthorizationCodeFromURL()");
        String authorizationCode;
        authorizationCode = url.substring(url.indexOf("code=") + 5);
        if (authorizationCode.contains("&")) {
            authorizationCode = authorizationCode.substring(0, authorizationCode.indexOf("&"));
        }
        return authorizationCode;
    }

    private void manageWithAuthorizationCode() {
        System.out.println("OAUTH: manageWithAuthorizationCode()");
        if(authorizationCode != null && !authorizationCode.equals("")) {
            try {
                exchangeAuthorizationCodeForTokens();
            } catch (IOException e) {
                e.printStackTrace();
                controller.authorizationFailedAction("OAuth2 Authorization failed");
            }
        } else {
            System.out.println("OAUTH: controller.authorizationFailed()");
            controller.authorizationFailedAction("Authorization window has been closed");
        }
    }

    private void exchangeAuthorizationCodeForTokens() throws IOException {
        System.out.println("OAUTH: exchangeAuthorizationCodeForTokens()");
        String request = buildExchangeRequest();
        HttpURLConnection connection = buildConnection(request);

        HashMap<String, Object> result;
        result = new ObjectMapper().readValue(connection.getInputStream(), new TypeReference<>() {});

        Properties oauthTokens = new Properties();
        oauthTokens.put("access_token", result.get("access_token"));
        oauthTokens.put("refresh_token", result.get("refresh_token"));
        long tokenExpires = (System.currentTimeMillis() + ((Number)result.get("expires_in")).intValue() * 1000 - 5000);
        oauthTokens.put("token_expires", tokenExpires + "");
        System.out.println("OAUTH: controller.loginUsingOAuth(oauthTokens)");
        controller.loginUsingOAuth(oauthTokens);
    }

    private String buildExchangeRequest() {
        System.out.println("OAUTH: buildExchangeRequest");
        return "code=" + authorizationCode +
                "&client_id=" + oauthProperties.getProperty("client_id") +
                "&client_secret=" + oauthProperties.getProperty("client_secret") +
                "&redirect_uri=" + oauthProperties.getProperty("redirect_uri") +
                "&grant_type=authorization_code";
    }

    private HttpURLConnection buildConnection(String request) throws IOException {
        System.out.println("OAUTH: buildConnection");
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
        System.out.println("OAUTH: refreshAccessToken");
        String request = buildRefreshTokenRequest();
        HttpURLConnection connection = buildConnection(request);

        HashMap<String, Object> result;
        result = new ObjectMapper().readValue(connection.getInputStream(), new TypeReference<>() {
        });
        accountProperties.put("access_token", result.get("access_token"));
        long tokenExpires = (System.currentTimeMillis() + ((Number) result.get("expires_in")).intValue() * 1000 - 5000);
        accountProperties.put("token_expires", tokenExpires + "");
        System.out.println("OAUTH: set reshreshed token to account properties");
    }

    private String buildRefreshTokenRequest() {
        System.out.println("OAUTH: buildRefreshTokenRequest");
        return "client_id=" + oauthProperties.getProperty("client_id") +
                "&client_secret=" + oauthProperties.getProperty("client_secret") +
                "&refresh_token="+ accountProperties.getProperty("refresh_token")+
                "&grant_type=refresh_token";
    }
}
