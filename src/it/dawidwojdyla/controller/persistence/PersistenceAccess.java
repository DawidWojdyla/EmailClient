package it.dawidwojdyla.controller.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 2020-12-10.
 */
public class PersistenceAccess {

    private String VALID_ACCOUNTS_LOCATION = System.getProperty("user.home") + File.separator + "validAccounts.ser";
    private Encoder encoder = new Encoder();

    public List<ValidAccount> loadFromPersistence() {
        List<ValidAccount> accountList = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_ACCOUNTS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<ValidAccount> persistedList = (List<ValidAccount>) objectInputStream.readObject();
            decodePasswords(persistedList);
            accountList.addAll(persistedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    private void decodePasswords(List<ValidAccount> persistedList) {
        for (ValidAccount account: persistedList) {
            account.setPassword(encoder.decode(account.getPassword()));
        }
    }

    private void encodePasswords(List<ValidAccount> persistedList) {
        for (ValidAccount account: persistedList) {
            account.setPassword(encoder.encode(account.getPassword()));
        }
    }

    public void saveToPersistence(List<ValidAccount> accounts) {
        try {
            File file = new File(VALID_ACCOUNTS_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            encodePasswords(accounts);
            objectOutputStream.writeObject(accounts);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
