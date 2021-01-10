package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.controller.services.MessageRendererService;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.model.EmailTreeItem;
import it.dawidwojdyla.model.SizeInteger;
import it.dawidwojdyla.view.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
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
    private final MenuItem deleteMessageMenuItem  = new MenuItem("delete message");
    private final MenuItem showMessageDetailsMenuItem = new MenuItem("view details");
    private MenuItem showReplyMessageWindowMenuItem = new MenuItem("reply");
    private MenuItem showForwardMessageWindowMenuItem = new MenuItem("forward");

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
    void aboutAction() {
        viewFactory.showAboutWindow();
    }


    @FXML
    void accountSettingsAction() {
        viewFactory.showAccountSettingsWindow();
    }

    @FXML
    void addAccountAction() {
        viewFactory.showLoginWindow();
    }


    @FXML
    void deleteAccountAction() {
        viewFactory.showDeleteAccountWindow();
    }

    @FXML
    void composeMessageAction() {
        viewFactory.showComposeMessageWindow(ComposeMessageType.DEFAULT);
    }

    @FXML
    void closeAction() {
        Platform.exit();
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
            emailManager.setMessageReadState(false);
            emailsTableView.refresh();
        });

        deleteMessageMenuItem.setOnAction(event -> {
            emailManager.deleteSelectedMessage();
            emailWebView.getEngine().loadContent("");
        });

        showMessageDetailsMenuItem.setOnAction(actionEvent -> viewFactory.showEmailDetailsWindow(messageRendererService));

        showReplyMessageWindowMenuItem.setOnAction(event -> viewFactory.showComposeMessageWindow(ComposeMessageType.REPLY));

        showForwardMessageWindowMenuItem.setOnAction(event -> viewFactory.showComposeMessageWindow(ComposeMessageType.FORWARD));

    }

    private void setUpMessageSelection() {
        emailsTableView.setOnMouseClicked(event -> {
            EmailMessage emailMessage = emailsTableView.getSelectionModel().getSelectedItem();
            if (emailMessage != null) {
                emailManager.setSelectedMessage(emailMessage);

                if(!emailMessage.isRead()) {
                    emailManager.setMessageReadState(true);
                    emailsTableView.refresh();
                }

                if (emailMessage.getMessageContent() != null) {
                    emailWebView.getEngine().loadContent(emailMessage.getMessageContent());
                } else {
                    messageRendererService = new MessageRendererService(emailWebView.getEngine());
                    messageRendererService.setEmailMessage(emailMessage);
                    messageRendererService.restart();
                }
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        viewFactory.showEmailDetailsWindow(messageRendererService);
                    }
                }
            }
        });
    }

    private void setUpMessageRendererService() {
        messageRendererService = new MessageRendererService(emailWebView.getEngine());
    }

    private void setUpBoldRows() {
        emailsTableView.setRowFactory(new Callback<>() {
            @Override
            public TableRow<EmailMessage> call(TableView<EmailMessage> param) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(EmailMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            if (item.isRead()) {
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
                if (item.getValue().toLowerCase().contains("wysłane") || item.getValue().toLowerCase().contains("sent")) {
                    showReplyMessageWindowMenuItem.setVisible(false);
                } else {
                    showReplyMessageWindowMenuItem.setVisible(true);
                }
                if (event.getClickCount() > 1) {
                    emailsTableView.scrollTo(0);
                }
            }
        });
    }

    private void setUpEmailsTableView() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        recipientColumn.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        emailsTableView.setContextMenu(new ContextMenu(
                markUnreadMenuItem,
                deleteMessageMenuItem,
                showMessageDetailsMenuItem,
                showReplyMessageWindowMenuItem,
                showForwardMessageWindowMenuItem));
    }

    private void setUpEmailsTreeView() {
        emailsTreeView.setRoot(emailManager.getFoldersRoot());
        emailsTreeView.setShowRoot(false);
    }
}
