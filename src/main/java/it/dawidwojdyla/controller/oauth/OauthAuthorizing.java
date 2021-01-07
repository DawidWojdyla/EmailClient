package it.dawidwojdyla.controller.oauth;

import java.util.Properties;

public interface OauthAuthorizing {
    void loginUsingOAuth(Properties tokens);
    void authorizationFailedAction(String errorMessage);
}
