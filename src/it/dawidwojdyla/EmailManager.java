package it.dawidwojdyla;

import it.dawidwojdyla.model.EmailAccount;
import javafx.scene.control.TreeItem;

/**
 * Created by Dawid on 2020-11-26.
 */
public class EmailManager {

    //Folder handling:
    private TreeItem<String> foldersRoot = new TreeItem<String>("");

    public TreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    public void addEmailAccount(EmailAccount emailAccount) {
        TreeItem<String> treeItem = new TreeItem<String>(emailAccount.getAddress());
        treeItem.setExpanded(true);
        foldersRoot.getChildren().add(treeItem);

    }
}
