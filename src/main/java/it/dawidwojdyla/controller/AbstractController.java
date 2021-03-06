package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;

/**
 * Created by Dawid on 2020-11-26.
 */
public abstract class AbstractController {

    protected EmailManager emailManager;
    protected ViewFactory viewFactory;
    private final String fxmlName;

    public AbstractController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        this.emailManager = emailManager;
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    public String getFxmlName() {
        return fxmlName;
    }
}
