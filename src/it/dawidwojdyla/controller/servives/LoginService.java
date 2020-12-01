package it.dawidwojdyla.controller.servives;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.EmailLoginResult;
import it.dawidwojdyla.model.EmailAccount;

/**
 * Created by Dawid on 2020-12-01.
 */
public class LoginService {

    EmailAccount emailAccount;
    EmailManager emailManager;

    public LoginService(EmailAccount emailAccount, EmailManager emailManager) {
        this.emailAccount = emailAccount;
        this.emailManager = emailManager;
    }

    public EmailLoginResult login() {

    }
}
