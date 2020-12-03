package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.model.EmailTreeItem;
import it.dawidwojdyla.view.ViewFactory;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Dawid on 2020-11-26.
 */
public class MainWindowController extends AbstractController implements Initializable {

    @FXML
    private TreeView<String> emailsTreeView;

    @FXML
    private TableView<EmailMessage> emailsTableView;

    @FXML
    private WebView emailWebView;

    @FXML
    private TableColumn<EmailMessage, String> senderColumn;

    @FXML
    private TableColumn<EmailMessage, String> subjectColumn;

    @FXML
    private TableColumn<EmailMessage, String> recipientColumn;

    @FXML
    private TableColumn<EmailMessage, Integer> sizeColumn;

    @FXML
    private TableColumn<EmailMessage, Date> dateColumn;


    public MainWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
        viewFactory.showOptionsWindow();
    }

    @FXML
    void addAccountAction() {
        viewFactory.showLoginWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpEmailsTreeView();
        setUpEmailsTableView();
        setUpFolderSelection();
        setUpBoldRows();
    }

    private void setUpBoldRows() {
        emailsTableView.setRowFactory(new Callback<TableView<EmailMessage>, TableRow<EmailMessage>>() {
            @Override
            public TableRow<EmailMessage> call(TableView<EmailMessage> param) {
                return new TableRow<EmailMessage>() {
                    @Override
                    protected void updateItem(EmailMessage item, boolean empty) {
                         super.updateItem(item, empty);
                         if(item != null) {
                             if(item.isRead()) {
                                 setStyle("");
                             } else {
                                 setStyle("-fx-font-weight: bold");
                             }
                         }
                    }
                };
            }
        });
    }

    private void setUpFolderSelection() {
        emailsTreeView.setOnMouseClicked(event -> {
            EmailTreeItem<String> item = (EmailTreeItem<String>) emailsTreeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                emailsTableView.setItems(item.getEmailMessages());
            }
        });
    }

    private void setUpEmailsTableView() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("subject"));
        recipientColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("recipient"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, Integer>("size"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, Date>("date"));
    }

    private void setUpEmailsTreeView() {
        emailsTreeView.setRoot(emailManager.getFoldersRoot());
        emailsTreeView.setShowRoot(false);
    }
}
