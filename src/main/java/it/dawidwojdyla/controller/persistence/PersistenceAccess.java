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

    private String VERIFIED_ACCOUNTS_LOCATION = System.getProperty("user.home") + File.separator + "emailAccounts.ser";
    private Encoder encoder = new Encoder();

    public List<VerifiedAccount> loadFromPersistence() {
        List<VerifiedAccount> accountList = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(VERIFIED_ACCOUNTS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<VerifiedAccount> persistedList = (List<VerifiedAccount>) objectInputStream.readObject();
            decodePasswords(persistedList);
            accountList.addAll(persistedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    private void decodePasswords(List<VerifiedAccount> persistedList) {
        for (VerifiedAccount account: persistedList) {
            account.setPassword(encoder.decode(account.getPassword()));
        }
    }

    private void encodePasswords(List<VerifiedAccount> persistedList) {
        for (VerifiedAccount account: persistedList) {
            account.setPassword(encoder.encode(account.getPassword()));
        }
    }

    public void saveToPersistence(List<VerifiedAccount> accounts) {
        try {
            File file = new File(VERIFIED_ACCOUNTS_LOCATION);
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
