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

    private String VALID_ACCOUNTS_LOCATION = System.getenv("APPDATA") + "\\validAccounts.ser";

    public List<ValidAccount> loadFromPersistence() {
        List<ValidAccount> accountList = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(VALID_ACCOUNTS_LOCATION);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<ValidAccount> persistedList = (List<ValidAccount>) objectInputStream.readObject();
            accountList.addAll(persistedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public void saveToPersistence(List<ValidAccount> accounts) {
        try {
            File file = new File(VALID_ACCOUNTS_LOCATION);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(accounts);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
