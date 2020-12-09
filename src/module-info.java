module JavaFxMailClient {

    requires javafx.controls;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.fxml;
    requires activation;
    requires java.mail;
    requires java.desktop;

    opens it.dawidwojdyla;
    opens it.dawidwojdyla.view;
    opens it.dawidwojdyla.controller;
    opens it.dawidwojdyla.model;

}