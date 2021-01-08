package it.dawidwojdyla.view;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.Main;
import it.dawidwojdyla.controller.*;
import it.dawidwojdyla.controller.services.MessageRendererService;
import javafx.application.Platform;
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

    private final EmailManager emailManager;
    private final ArrayList<Stage> activeStages;
    private boolean mainViewInitialized = false;

    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
        this.activeStages = new ArrayList<>();
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
        initializeStage(controller,false);
    }

    public void showMainWindow() {
        if(!mainViewInitialized) {
            mainViewInitialized = true;
            AbstractController controller = new MainWindowController(emailManager, this, "MainWindow.fxml");
            initializeStage(controller, true);
            activeStages.get(activeStages.size() - 1).setOnCloseRequest(e -> Platform.exit());
        }

    }

    public void showOptionsWindow() {

        AbstractController controller = new OptionsWindowController(emailManager, this, "OptionsWindow.fxml");
        initializeStage(controller, false);
    }

    public void showAboutWindow() {
        AbstractController controller = new AboutWindowController(emailManager, this, "AboutWindow.fxml");
        initializeStage(controller, false);
    }

    public void showComposeMessageWindow(ComposeMessageType messageType) {
        AbstractController controller = new ComposeMessageWindowController(emailManager, this, "ComposeMessageWindow.fxml", messageType);
        initializeStage(controller,true);
    }

    public void showAccountSettingsWindow() {
        AbstractController controller = new AccountSettingsWindowController(emailManager, this, "AccountSettingsWindow.fxml");
        initializeStage(controller,false);
    }

    public void showDeleteAccountWindow() {
        AbstractController controller = new DeleteAccountWindowController(emailManager, this, "DeleteAccountWindow.fxml");
        initializeStage(controller,false);
    }


    public void showEmailDetailsWindow(MessageRendererService messageRendererService) {
        AbstractController controller = new EmailDetailsWindowController(emailManager, this, "EmailDetailsWindow.fxml", messageRendererService);
        initializeStage(controller,true);
    }

    public void showObtainAuthorizationCodeWindow (AbstractController controller) {
        initializeStage(controller, false);
    }

    private void initializeStage(AbstractController controller, boolean resizable) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/" + controller.getFxmlName()));
        fxmlLoader.setController(controller);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        updateStyle(scene);
        Stage stage = new Stage();
        stage.setScene(scene);
        if(!resizable) {
            stage.setResizable(false);
        }
        stage.show();
        activeStages.add(stage);
    }

    public void closeStage(Stage stageToClose) {
        stageToClose.close();
        activeStages.remove(stageToClose);
    }

    public void updateAllStyles() {
        for (Stage stage: activeStages) {
            Scene scene = stage.getScene();
            updateStyle(scene);
        }
    }

    private void updateStyle(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Main.class.getClassLoader().getResource("css/default.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getClassLoader().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
        scene.getStylesheets().add(Main.class.getClassLoader().getResource(FontSize.getCssPath(fontSize)).toExternalForm());
    }
}
