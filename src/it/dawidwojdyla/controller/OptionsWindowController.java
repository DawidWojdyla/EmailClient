package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

/**
 * Created by Dawid on 2020-11-26.
 */
public class OptionsWindowController extends AbstractController {

    @FXML
    private Slider fontSizePicker;

    @FXML
    private ChoiceBox<?> themePicker;

    public OptionsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void applyButtonAction() {

        //save and close window
        viewFactory.closeStage((Stage) themePicker.getScene().getWindow());
    }

    @FXML
    void cancelButtonAction() {
        viewFactory.closeStage((Stage) themePicker.getScene().getWindow());
    }



}
