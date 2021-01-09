package it.dawidwojdyla.controller.services;

import it.dawidwojdyla.model.EmailTreeItem;
import it.dawidwojdyla.view.IconResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.List;

/**
 * Created by Dawid on 2020-12-02.
 */
public class FetchFoldersService extends Service<Void> {

    private Store store;
    private EmailTreeItem<String> foldersRoot;
    private List<Folder> folderList;
    private IconResolver iconResolver = new IconResolver();

    public FetchFoldersService(Store store, EmailTreeItem<String> foldersRoot, List<Folder> folderList) {
        this.store = store;
        this.foldersRoot = foldersRoot;
        this.folderList = folderList;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                fetchFolders();
                return null;
            }
        };
    }

    private void fetchFolders() throws MessagingException {
        Folder[] folders = store.getDefaultFolder().list();
        handleFolders(folders, foldersRoot);
    }

    private void handleFolders(Folder[] folders, EmailTreeItem<String> foldersRoot) throws MessagingException {
        for(Folder folder: folders) {
            folderList.add(folder);
            EmailTreeItem<String> emailTreeItem = new EmailTreeItem<>(folder.getName());
            emailTreeItem.setGraphic(iconResolver.getIconForFolder(folder.getName()));
            foldersRoot.getChildren().add(emailTreeItem);
            foldersRoot.setExpanded(true);
            addMessageListenerToFolder(folder, emailTreeItem);
            fetchMessagesOnFolder(folder, emailTreeItem);
            if(folder.getType() == Folder.HOLDS_FOLDERS) {
                Folder[] subfolders = folder.list();
                handleFolders(subfolders, emailTreeItem);
            }
        }
    }

    private void addMessageListenerToFolder(Folder folder, EmailTreeItem<String> emailTreeItem) {
        folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent event) {
                for (int i = 0; i < event.getMessages().length; i++) {
                    try {
                        Message message = folder.getMessage(folder.getMessageCount() - i);
                        emailTreeItem.addEmailToTop(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent event) {
                System.out.println("Message removed event " + event);

                for (Message message : event.getMessages()) {
                    emailTreeItem.getEmailMessages().removeIf(m -> m.getMessage().equals(message));
                }
            }
        });
    }

    private void fetchMessagesOnFolder(Folder folder, EmailTreeItem<String> emailTreeItem) {
        Service<Void> fetchMesegesSerrvice = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        if(folder.getType() != Folder.HOLDS_FOLDERS) {
                            folder.open(Folder.READ_WRITE);
                            int folderSize = folder.getMessageCount();
                            for(int i = folderSize; i > 0; i--) {
                                emailTreeItem.addEmail(folder.getMessage(i));
                            }
                        }
                        return null;
                    }
                };
            }
        };
        fetchMesegesSerrvice.start();
    }
}
