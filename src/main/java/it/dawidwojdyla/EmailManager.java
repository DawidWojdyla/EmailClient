package it.dawidwojdyla;

import com.sun.mail.imap.IMAPFolder;
import it.dawidwojdyla.controller.services.FetchFoldersService;
import it.dawidwojdyla.controller.services.FolderUpdaterService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.model.EmailMessage;
import it.dawidwojdyla.model.EmailTreeItem;
import it.dawidwojdyla.view.IconResolver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by Dawid on 2020-11-26.
 */
public class EmailManager {

    private EmailMessage selectedMessage;
    private EmailTreeItem<String> selectedFolder;
    private final ObservableList<EmailAccount> emailAccounts = FXCollections.observableArrayList();
    private final ObservableList<EmailAccount> invalidEmailAccounts = FXCollections.observableArrayList();
    private final IconResolver iconResolver = new IconResolver();
    private final HashMap<String, Properties> mailProperties = new HashMap<>();
    private final EmailTreeItem<String> foldersRoot = new EmailTreeItem<>("");
    private HashMap<String, List<Folder>> folderLists = new HashMap<>();

    public EmailManager() {
        setProperties();
        FolderUpdaterService folderUpdaterService = new FolderUpdaterService(folderLists);
        folderUpdaterService.start();
    }

    public EmailTreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    public ObservableList<EmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    public ObservableList<EmailAccount> getInvalidEmailAccounts() {
        return invalidEmailAccounts;
    }

    public ObservableList<EmailAccount> getAllEmailAccounts() {

        ObservableList<EmailAccount> accounts = FXCollections.observableArrayList();
        accounts.addAll(emailAccounts);
        accounts.addAll(invalidEmailAccounts);

        return accounts;
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

    private void setProperties() {
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
        FetchFoldersService fetchFoldersService = new FetchFoldersService(emailAccount.getStore(), treeItem, folderLists);
        fetchFoldersService.start();
        emailAccount.setFetchFolderService(fetchFoldersService);
        foldersRoot.getChildren().add(treeItem);
    }

    public void addInvalidEmailAccount(EmailAccount emailAccount) {
        invalidEmailAccounts.add(emailAccount);
        EmailTreeItem<String> treeItem = new EmailTreeItem<>(emailAccount.getAddress() + " !");
        treeItem.setGraphic(iconResolver.getIconForFolder("invalidAccount"));
        foldersRoot.getChildren().add(treeItem);
    }

    public void removeInvalidEmailAccount(EmailAccount emailAccount) {
        invalidEmailAccounts.remove(emailAccount);
        foldersRoot.getChildren().removeIf(item -> item.getValue().equals(emailAccount.getAddress() + " !"));
    }

    public void removeEmailAccount(EmailAccount emailAccount) {
        if (emailAccounts.contains(emailAccount)) {
            emailAccount.getFetchFolderService().cancel();
            foldersRoot.getChildren().removeIf(item -> item.getValue().equals(emailAccount.getAddress()));
            emailAccounts.remove(emailAccount);
        } else {
            removeInvalidEmailAccount(emailAccount);
        }
    }

    public void setMessageReadState(boolean isRead) {
        try {
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, isRead);
            MimeMessage mimeMessage = (MimeMessage) selectedMessage.getMessage();
            manageMessageReadStateInEachFolder(isRead, mimeMessage.getMessageID(), getRoot(selectedFolder));
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private void manageMessageReadStateInEachFolder(boolean isRead, String messageId, EmailTreeItem<String> emailTreeItem) throws MessagingException {
        for (TreeItem<String> child: emailTreeItem.getChildren()) {
            if (child.isExpanded()) {
                manageMessageReadStateInEachFolder(isRead, messageId, (EmailTreeItem<String>) child);
            } else {
                EmailTreeItem<String> item = (EmailTreeItem<String>) child;
                for (EmailMessage emailMessage: item.getEmailMessages()) {
                    if (emailMessage.getMimeMessage().getMessageID().equals(messageId)) {
                        emailMessage.setRead(isRead);
                        if (isRead) {
                            item.decrementUnreadMessagesCount();
                        } else {
                            item.incrementUnreadMessagesCount();
                        }
                    }
                }
            }
        }
    }

    public void deleteSelectedMessage() {
        try {

            Message message = selectedMessage.getMessage();
            selectedFolder.getEmailMessages().remove(selectedMessage);
            Folder folderFrom = message.getFolder();
            IMAPFolder imapFolder = (IMAPFolder) folderFrom;

            if (folderFrom.getName().toLowerCase().equals("trash") || folderFrom.getName().toLowerCase().equals("kosz")) {

                message.setFlag(Flags.Flag.DELETED, true);
                imapFolder.expunge();

            } else {

                List<Folder> folderList = folderLists.get(getRoot(selectedFolder).getValue());

                for (Folder folder : folderList) {
                    if (folder.getName().toLowerCase().equals("trash") || folder.getName().toLowerCase().equals("kosz")) {
                        imapFolder.moveMessages(new Message[]{message}, folder);
                        break;
                    }
                }
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private EmailTreeItem<String> getRoot(EmailTreeItem<String> emailTreeItem) {
        while (!emailTreeItem.getValue().contains("@")) {
            emailTreeItem = (EmailTreeItem<String>) emailTreeItem.getParent();
        }
        return emailTreeItem;
    }
}