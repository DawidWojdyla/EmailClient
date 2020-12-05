package it.dawidwojdyla.view;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Dawid on 2020-11-26.
 */
public class ViewFactory {

    private EmailManager emailManager;
    private ArrayList<Stage> activeStages;
    private boolean mainViewInitialized = false;

    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
        this.activeStages = new ArrayList<Stage>();
    }

    public boolean isMainViewInitialized() {
        return mainViewInitialized;
    }

    //View options handling:
    private ColorTheme colorTheme = ColorTheme.DEFAULT;
    private FontSize fontSize = FontSize.MEDIUM;

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public void showLoginWindow() {

        AbstractController controller = new LoginWindowController(emailManager, this, "loginWindow.fxml");
        initializeStage(controller);
    }

    public void showMainWindow() {

        AbstractController controller = new MainWindowController(emailManager, this, "MainWindow.fxml");
        initializeStage(controller);
        mainViewInitialized = true;
    }

    public void showOptionsWindow() {

        AbstractController controller = new OptionsWindowController(emailManager, this, "OptionsWindow.fxml");
        initializeStage(controller);
    }

    public void showComposeMessageWindow() {
        AbstractController controller = new ComposeMessageWindowController(emailManager, this, "ComposeMessageWindow.fxml");
        initializeStage(controller);
    }

    private void initializeStage(AbstractController controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(controller.getFxmlName()));
        fxmlLoader.setController(controller);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        activeStages.add(stage);
    }

    public void closeStage(Stage stageToClose) {
        stageToClose.close();
        activeStages.remove(stageToClose);
    }

    public void updateStyles() {
        for (Stage stage: activeStages) {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
            scene.getStylesheets().add(getClass().getResource(FontSize.getCssPath(fontSize)).toExternalForm());

        }
    }
}
