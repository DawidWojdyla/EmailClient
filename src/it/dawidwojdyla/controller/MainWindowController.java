package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.services.MessageRendererService;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.model.EmailTreeItem;
import it.dawidwojdyla.model.SizeInteger;
import it.dawidwojdyla.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    private MenuItem markUnreadMenuItem = new MenuItem("mark as unread");
    private MenuItem deleteMessageMenuItem  = new MenuItem("delete message");
    private MenuItem showMessageDetailsMenuItem = new MenuItem("view details");

    @FXML
    private TreeView<String> emailsTreeView;

    @FXML
    private TableView<EmailMessage> emailsTableView;

    @FXML
    private TableColumn<EmailMessage, String> senderColumn;

    @FXML
    private TableColumn<EmailMessage, String> subjectColumn;

    @FXML
    private TableColumn<EmailMessage, String> recipientColumn;

    @FXML
    private TableColumn<EmailMessage, SizeInteger> sizeColumn;

    @FXML
    private TableColumn<EmailMessage, Date> dateColumn;

    @FXML
    private WebView emailWebView;

    private MessageRendererService messageRendererService;

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

    @FXML
    void composeMessageAction() {
        viewFactory.showComposeMessageWindow();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpEmailsTreeView();
        setUpEmailsTableView();
        setUpFolderSelection();
        setUpBoldRows();
        setUpMessageRendererService();
        setUpMessageSelection();
        setUpContextMenus();
    }

    private void setUpContextMenus() {
        markUnreadMenuItem.setOnAction(event -> {
            emailManager.setUnread();
            emailsTableView.refresh();
        });
        deleteMessageMenuItem.setOnAction(event -> {
            emailManager.deleteSelectedMessage();
            emailWebView.getEngine().loadContent("");
        });
        showMessageDetailsMenuItem.setOnAction(actionEvent -> viewFactory.showEmailDetailsWindow());
    }

    private void setUpMessageSelection() {
        emailsTableView.setOnMouseClicked(event -> {
            EmailMessage emailMessage = emailsTableView.getSelectionModel().getSelectedItem();
            if (emailMessage != null) {
                emailManager.setSelectedMessage(emailMessage);
                if(!emailMessage.isRead()) {
                    emailManager.setRead();
                }
                emailsTableView.refresh();
                messageRendererService.setEmailMessage(emailMessage);
                messageRendererService.restart();

            }
        });
    }

    private void setUpMessageRendererService() {
        messageRendererService = new MessageRendererService(emailWebView.getEngine());
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
                emailManager.setSelectedFolder(item);
                emailsTableView.setItems(item.getEmailMessages());
            }
        });
    }

    private void setUpEmailsTableView() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("subject"));
        recipientColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("recipient"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, SizeInteger>("size"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, Date>("date"));

        emailsTableView.setContextMenu(new ContextMenu(markUnreadMenuItem, deleteMessageMenuItem, showMessageDetailsMenuItem));
    }

    private void setUpEmailsTreeView() {
        emailsTreeView.setRoot(emailManager.getFoldersRoot());
        emailsTreeView.setShowRoot(false);
    }
}
