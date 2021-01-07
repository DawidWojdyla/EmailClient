package it.dawidwojdyla.controller.services;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.EmailLoginResult;
import it.dawidwojdyla.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.*;

/**
 * Created by Dawid on 2020-12-01.
 */
public class LoginService extends Service<EmailLoginResult> {

    EmailAccount emailAccount;
    EmailManager emailManager;
    boolean isOauth;

    public LoginService(EmailAccount emailAccount, EmailManager emailManager, boolean isOauth) {
        this.emailAccount = emailAccount;
        this.emailManager = emailManager;
        this.isOauth = isOauth;
    }

    private EmailLoginResult login() {
        Session session;
        Store store;
        try {
            if (isOauth) {
                session = Session.getInstance(emailAccount.getProperties());
                store = session.getStore("imap");
                store.connect(emailAccount.getProperties().getProperty("incomingHost"),
                        emailAccount.getAddress(),
                        emailAccount.getProperties().getProperty("access_token"));
            } else {
                Authenticator authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailAccount.getAddress(), emailAccount.getPassword());
                    }
                };
                session = Session.getInstance(emailAccount.getProperties(), authenticator);
                store = session.getStore("imaps");
                store.connect(emailAccount.getProperties().getProperty("incomingHost"),
                        emailAccount.getAddress(),
                        emailAccount.getPassword());
            }
            emailAccount.setStore(store);
            emailAccount.setSession(session);
            if (!emailManager.getEmailAccounts().contains(emailAccount)) {
                emailManager.addEmailAccount(emailAccount);
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_NETWORK;
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_CREDENTIALS;
        } catch (MessagingException e) {
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_UNEXPECTED_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return EmailLoginResult.FAILED_BY_UNEXPECTED_ERROR;
        }
        return EmailLoginResult.SUCCESS;
    }

    @Override
    protected Task<EmailLoginResult> createTask() {
        return new Task<>() {
            @Override
            protected EmailLoginResult call() {
                return login();
            }
        };
    }
}
