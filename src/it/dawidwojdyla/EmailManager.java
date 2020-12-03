package it.dawidwojdyla;

import it.dawidwojdyla.controller.services.FetchFoldersService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.model.EmailTreeItem;
import javafx.scene.control.TreeItem;


/**
 * Created by Dawid on 2020-11-26.
 */
public class EmailManager {

    //Folder handling:
    private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");

    public EmailTreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    public void addEmailAccount(EmailAccount emailAccount) {
        EmailTreeItem<String> treeItem = new EmailTreeItem<String>(emailAccount.getAddress());
        FetchFoldersService fetchFoldersService = new FetchFoldersService(emailAccount.getStore(), treeItem);
        fetchFoldersService.start();
        foldersRoot.getChildren().add(treeItem);

    }
}
