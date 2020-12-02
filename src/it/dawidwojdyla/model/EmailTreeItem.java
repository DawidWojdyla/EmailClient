package it.dawidwojdyla.model;

import javafx.scene.control.TreeItem;

/**
 * Created by Dawid on 2020-12-02.
 */
public class EmailTreeItem<String> extends TreeItem<String> {

    private String name;

    public EmailTreeItem(String name) {
        super(name);
        this.name = name;
    }
}
