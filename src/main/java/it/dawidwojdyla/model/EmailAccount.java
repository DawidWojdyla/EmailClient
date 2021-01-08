package it.dawidwojdyla.model;

import javafx.concurrent.Service;

import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

/**
 * Created by Dawid on 2020-12-01.
 */
public class EmailAccount {

    private String address;
    private String password;
    private Properties properties;
    private Store store;
    private Session session;

    public Service<Void> getFetchFolderService() {
        return fetchFolderService;
    }

    public void setFetchFolderService(Service<Void> fetchFolderService) {
        this.fetchFolderService = fetchFolderService;
    }

    private Service<Void> fetchFolderService;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public EmailAccount(String address, String password, Properties properties) {
        this.address = address;
        this.password = password;
        this.properties = properties;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getProperties() {
        return properties;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }


    @Override
    public String toString() {
        return address;
    }
}
