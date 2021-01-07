package it.dawidwojdyla;

import it.dawidwojdyla.controller.EmailLoginResult;
import it.dawidwojdyla.controller.oauth.Oauth;
import it.dawidwojdyla.controller.persistence.PersistenceAccess;
import it.dawidwojdyla.controller.persistence.VerifiedAccount;
import it.dawidwojdyla.controller.services.LoginService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 2020-11-25.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private final PersistenceAccess persistenceAccess = new PersistenceAccess();
    private final EmailManager emailManager = new EmailManager();

    @Override
    public void start(Stage stage) {

        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        ViewFactory viewFactory = new ViewFactory(emailManager);

        List<VerifiedAccount> verifiedAccounts = persistenceAccess.loadFromPersistence();
        if (verifiedAccounts.isEmpty()) {
            System.out.println("Main: validAccounts.isEmpty()==true");
            viewFactory.showLoginWindow();
        } else {
            System.out.println("Main: validAccounts.isEmpty()==false");
            for (VerifiedAccount account : verifiedAccounts) {
                EmailAccount emailAccount = new EmailAccount(account.getAddress(), account.getPassword(), account.getProperties());

                if (emailAccount.getProperties().containsValue("XOAUTH2") && emailAccount.getProperties().containsKey("token_expires")) {
                    System.out.println("Main: emailAccount with oauth");
                    long tokenExpires = Long.parseLong(emailAccount.getProperties().getProperty("token_expires"));
                    if (System.currentTimeMillis() > tokenExpires) {
                        System.out.println("Main: token expires");
                        Oauth oauth = new Oauth(emailManager.getOauthProperties(), emailAccount.getProperties());
                        Service<Void> service = new Service<>() {
                            @Override
                            protected Task<Void> createTask() {
                                return new Task<>() {
                                    @Override
                                    protected Void call() throws IOException {
                                        oauth.refreshAccessToken();
                                        return null;
                                    }
                                };
                            }
                        };
                        service.setOnSucceeded(e -> logIntoAccount(emailAccount, true));
                        service.start();
                    } else {
                        logIntoAccount(emailAccount, true);
                    }
                } else {
                    logIntoAccount(emailAccount, false);
                }
                viewFactory.showMainWindow();
            }
        }
    }

        @Override
        public void stop () {
            List<VerifiedAccount> verifiedAccounts = new ArrayList<>();
            for (EmailAccount emailAccount : emailManager.getEmailAccounts()) {
                verifiedAccounts.add(new VerifiedAccount(emailAccount.getAddress(), emailAccount.getPassword(), emailAccount.getProperties()));
            }
            for (EmailAccount emailAccount : emailManager.getInvalidEmailAccounts()) {
                verifiedAccounts.add(new VerifiedAccount(emailAccount.getAddress(), emailAccount.getPassword(), emailAccount.getProperties()));
            }
            persistenceAccess.saveToPersistence(verifiedAccounts);
        }

    private void logIntoAccount(EmailAccount emailAccount, boolean isOauth) {
        LoginService loginService = new LoginService(emailAccount, emailManager, isOauth);
        loginService.start();
        loginService.setOnSucceeded(e -> {
            if (loginService.getValue() != EmailLoginResult.SUCCESS) {
                emailManager.addInvalidEmailAccount(emailAccount);
            }
            });
    }
}
