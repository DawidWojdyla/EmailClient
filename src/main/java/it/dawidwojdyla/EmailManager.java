package it.dawidwojdyla;

import it.dawidwojdyla.controller.services.FetchFoldersService;
import it.dawidwojdyla.controller.services.FolderUpdaterService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.model.EmailTreeItem;
import it.dawidwojdyla.view.IconResolver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.mail.Flags;
import javax.mail.Folder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 2020-11-26.
 */
public class EmailManager {

    private EmailMessage selectedMessage;
    private EmailTreeItem<String> selectedFolder;
    private final ObservableList<EmailAccount> emailAccounts = FXCollections.observableArrayList();
    private final IconResolver iconResolver = new IconResolver();

    public ObservableList<EmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    public EmailMessage getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(EmailMessage selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public void setSelectedFolder(EmailTreeItem<String> selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    //Folder handling:
    private final EmailTreeItem<String> foldersRoot = new EmailTreeItem<>("");

    public EmailTreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    private final List<Folder> folderList = new ArrayList<>();

    public EmailManager() {
        FolderUpdaterService folderUpdaterService = new FolderUpdaterService(folderList);
        folderUpdaterService.start();
    }

    public void addEmailAccount(EmailAccount emailAccount) {
        emailAccounts.add(emailAccount);
        EmailTreeItem<String> treeItem = new EmailTreeItem<>(emailAccount.getAddress());
        treeItem.setGraphic(iconResolver.getIconForFolder(emailAccount.getAddress()));
        FetchFoldersService fetchFoldersService = new FetchFoldersService(emailAccount.getStore(), treeItem, folderList);
        fetchFoldersService.start();
        foldersRoot.getChildren().add(treeItem);

    }

    public void setRead() {
        try {
            selectedMessage.setRead(true);
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, true);
            selectedFolder.decrementUnreadMessagesCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUnread() {
        try {
            selectedMessage.setRead(false);
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, false);
            selectedFolder.incrementUnreadMessagesCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedMessage() {
        try {
            selectedMessage.getMessage().setFlag(Flags.Flag.DELETED, true);
            selectedFolder.getEmailMessages().remove(selectedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}