package it.dawidwojdyla;

import it.dawidwojdyla.controller.services.FetchFoldersService;
import it.dawidwojdyla.controller.services.FolderUpdaterService;
import it.dawidwojdyla.model.EmailAccount;
import it.dawidwojdyla.model.EmailTreeItem;

import javax.mail.Folder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dawid on 2020-11-26.
 */
public class EmailManager {

    private FolderUpdaterService folderUpdaterService;
    //Folder handling:
    private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");

    public EmailTreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    private List<Folder> folderList = new ArrayList<Folder>();
    public List<Folder> getFolderList() {
        return folderList;
    }

    public EmailManager() {
        folderUpdaterService = new FolderUpdaterService(folderList);
        folderUpdaterService.start();
    }

    public void addEmailAccount(EmailAccount emailAccount) {
        EmailTreeItem<String> treeItem = new EmailTreeItem<String>(emailAccount.getAddress());
        FetchFoldersService fetchFoldersService = new FetchFoldersService(emailAccount.getStore(), treeItem, folderList);
        fetchFoldersService.start();
        foldersRoot.getChildren().add(treeItem);

    }
}
