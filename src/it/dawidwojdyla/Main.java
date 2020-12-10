package it.dawidwojdyla;

import it.dawidwojdyla.controller.persistence.PersistenceAccess;
import it.dawidwojdyla.controller.persistence.ValidAccount;
import it.dawidwojdyla.controller.services.LoginService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 2020-11-25.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private PersistenceAccess persistenceAccess = new PersistenceAccess();
    private EmailManager emailManager = new EmailManager();

    @Override
    public void start(Stage stage) throws Exception {

        ViewFactory viewFactory = new ViewFactory(emailManager);
        List<ValidAccount> validAccounts = persistenceAccess.loadFromPersistence();
        if (validAccounts.isEmpty()) {
            viewFactory.showLoginWindow();
        } else {
            for (ValidAccount account : validAccounts) {
                EmailAccount emailAccount = new EmailAccount(account.getAddress(), account.getPassword());
                LoginService loginService = new LoginService(emailAccount, emailManager);
                loginService.start();
            }
            viewFactory.showMainWindow();
        }
    }

    @Override
    public void stop() throws Exception {
        List<ValidAccount> validAccounts = new ArrayList<>();
        for (EmailAccount emailAccount: emailManager.getEmailAccounts()) {
            validAccounts.add(new ValidAccount(emailAccount.getAddress(), emailAccount.getPassword()));
        }
        persistenceAccess.saveToPersistence(validAccounts);
    }
}
