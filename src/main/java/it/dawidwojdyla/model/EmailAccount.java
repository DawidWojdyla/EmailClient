package it.dawidwojdyla.model;

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
