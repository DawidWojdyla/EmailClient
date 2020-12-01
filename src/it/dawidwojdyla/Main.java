package it.dawidwojdyla;

import it.dawidwojdyla.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Dawid on 2020-11-25.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        ViewFactory viewFactory = new ViewFactory(new EmailManager());
        viewFactory.showLoginWindow();
        //viewFactory.showOptionsWindow();
        viewFactory.updateStyles();

    }
}
