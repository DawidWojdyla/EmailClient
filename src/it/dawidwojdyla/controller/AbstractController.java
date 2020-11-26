package it.dawidwojdyla.controller;

import it.dawidwojdyla.EmailManager;
import it.dawidwojdyla.view.ViewFactory;

/**
 * Created by Dawid on 2020-11-26.
 */
public abstract class AbstractController {

    private EmailManager emailManager;
    protected ViewFactory viewFactory;
    private String fxmlName;

    public AbstractController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        this.emailManager = emailManager;
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    public String getFxmlName() {
        return fxmlName;
    }
}
