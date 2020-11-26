package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ColorTheme;
import it.dawidwojdyla.view.FontSize;
import it.dawidwojdyla.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2020-11-26.
 */
public class OptionsWindowController extends AbstractController implements Initializable {

    @FXML
    private Slider fontSizePicker;

    @FXML
    private ChoiceBox<ColorTheme> themePicker;

    public OptionsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void applyButtonAction() {

        viewFactory.setColorTheme(themePicker.getValue());
        viewFactory.setFontSize(FontSize.values()[(int)(fontSizePicker.getValue())]);
        viewFactory.closeStage((Stage) themePicker.getScene().getWindow());
    }

    @FXML
    void cancelButtonAction() {
        viewFactory.closeStage((Stage) themePicker.getScene().getWindow());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpThemePicker();
        setUpFontSizePicker();

    }

    private void setUpFontSizePicker() {
        fontSizePicker.setMin(0);
        fontSizePicker.setMax(FontSize.values().length-1);
        fontSizePicker.setValue(viewFactory.getFontSize().ordinal());
        fontSizePicker.setMinorTickCount(0);
        fontSizePicker.setMajorTickUnit(1);
        fontSizePicker.setBlockIncrement(1);
        fontSizePicker.setSnapToTicks(true);
        fontSizePicker.setShowTickMarks(true);
        fontSizePicker.setShowTickLabels(true);
        fontSizePicker.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double aDouble) {
                int i = aDouble.intValue();
                return FontSize.values()[i].toString();
            }

            @Override
            public Double fromString(String s) {
                return null;
            }
        });
        fontSizePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            fontSizePicker.setValue(newVal.intValue());
        });

    }

    private void setUpThemePicker() {
        themePicker.setItems(FXCollections.observableArrayList(ColorTheme.values()));
        themePicker.setValue(viewFactory.getColorTheme());
    }
}
