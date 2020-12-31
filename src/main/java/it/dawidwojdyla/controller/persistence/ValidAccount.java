package it.dawidwojdyla.controller.persistence;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by Dawid on 2020-12-10.
 */
public class ValidAccount implements Serializable {

    private String address;
    private String password;
    private Properties properties;

    public ValidAccount(String address, String password, Properties properties) {
        this.address = address;
        this.password = password;
        this.properties = properties;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
