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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by Dawid on 2020-11-26.
 */
public class EmailManager {

    private EmailMessage selectedMessage;
    private EmailTreeItem<String> selectedFolder;
    private final ObservableList<EmailAccount> emailAccounts = FXCollections.observableArrayList();
    private final IconResolver iconResolver = new IconResolver();
    private HashMap<String, Properties> mailProperties = new HashMap<>();

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

    private final EmailTreeItem<String> foldersRoot = new EmailTreeItem<>("");

    public EmailTreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    private final List<Folder> folderList = new ArrayList<>();

    public EmailManager() {
        setMailProperties();
        FolderUpdaterService folderUpdaterService = new FolderUpdaterService(folderList);
        folderUpdaterService.start();
    }

    private void setMailProperties() {
        setDefaultMailProperties();
        setOauthDefaultMailProperties();
        setOauthProperties();
    }

    public void setDefaultMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.host", "smtp.gmail.com");
        properties.put("mail.smtps.auth", "true");
        mailProperties.put("defaultMailProperties", properties);
    }

    private void setOauthDefaultMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.auth.mechanisms", "XOAUTH2");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        mailProperties.put("oauthDefaultMailProperties", properties);
    }

    private void setOauthProperties() {
        Properties properties = new Properties();
        properties.put("client_id", "1097648184338-d4h0ojjclgf4ng6ap7vgc4bbu2d3sfu1.apps.googleusercontent.com");
        properties.put("client_secret", "AmB8QDH9NilclJNiwR_OJriI");
        properties.put("authorization_server", "https://accounts.google.com/o/oauth2/v2/auth");
        properties.put("scope", "https://mail.google.com/");
        properties.put("token_server", "https://oauth2.googleapis.com/token");
        properties.put("redirect_uri", "http://localhost/authorization-code/callback");

        mailProperties.put("oauthProperties", properties);
    }

    public Properties getOauthDefaultMailProperties() {
        return mailProperties.get("oauthDefaultMailProperties");
    }

    public Properties getDefaultMailProperties() {
        return mailProperties.get("defaultMailProperties");
    }

    public Properties getOauthProperties() {
        return mailProperties.get("oauthProperties");
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