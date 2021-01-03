package it.dawidwojdyla.controller.oauth;

import java.util.Properties;

public interface OauthAuthorizing {
    void enableAction();
    void loginUsingOAuth(Properties tokens);
    void authorizationFailed();
}
