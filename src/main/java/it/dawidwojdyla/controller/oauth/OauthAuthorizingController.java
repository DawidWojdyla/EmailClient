package it.dawidwojdyla.controller.oauth;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.AbstractController;
import it.dawidwojdyla.view.ViewFactory;

/**
 * Created by Dawid on 2021-01-02.
 */
public abstract class OauthAuthorizingController extends AbstractController implements OauthAuthorizing {

    public OauthAuthorizingController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    public EmailManager getEmailManager () {
        return emailManager;
    }

    public ViewFactory getViewFactory () {
        return viewFactory;
    }

}
