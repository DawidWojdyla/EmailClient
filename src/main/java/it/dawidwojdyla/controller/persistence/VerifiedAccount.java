package it.dawidwojdyla.controller.persistence;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by Dawid on 2020-12-10.
 */
public class VerifiedAccount implements Serializable {

    private String address;
    private String password;
    private Properties properties;

    public VerifiedAccount(String address, String password, Properties properties) {
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

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getAccessToken() {
        return properties.getProperty("access_token");
    }

    public void setAccessToken(String accessToken) {
        properties.setProperty("access_token", accessToken);
    }

    public String getRefreshToken() {
        return properties.getProperty("refresh_token");
    }

    public void setRefreshToken(String refreshToken) {
        properties.setProperty("refresh_token", refreshToken);
    }
}
