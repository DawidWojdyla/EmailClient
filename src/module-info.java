module JavaFxMailClient {

    requires javafx.controls;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.fxml;
    requires activation;
    requires java.mail;

    opens it.dawidwojdyla;
    opens it.dawidwojdyla.view;
    opens it.dawidwojdyla.controller;

}