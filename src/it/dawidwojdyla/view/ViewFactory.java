package it.dawidwojdyla.view;

import it.dawidwojdyla.EmailManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Created by Dawid on 2020-11-26.
 */
public class ViewFactory {

    private EmailManager emailManager;

    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public void showLoginWindow() {

        System.out.println("This method will show loginWindow");
    }
}
