package it.dawidwojdyla.view;

import it.dawidwojdyla.Main;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Dawid on 2020-12-06.
 */
public class IconResolver {

    public Node getIconForFolder(String folderName) {
        String lowerCaseFolderName = folderName.toLowerCase();
        ImageView imageView;

        try {
            if (lowerCaseFolderName.contains("@")) {
                imageView = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("icons/email.png")));
            } else if (lowerCaseFolderName.contains("inbox") || lowerCaseFolderName.contains("przychodzące")) {
                imageView = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("icons/inbox.png")));
            } else if (lowerCaseFolderName.contains("sent") || lowerCaseFolderName.contains("wysłane")) {
                imageView = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("icons/sent2.png")));
            } else if (lowerCaseFolderName.contains("spam")) {
                imageView = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("icons/spam.png")));
            } else if (lowerCaseFolderName.contains("trash") || lowerCaseFolderName.contains("kosz")) {
            imageView = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("icons/bin.png")));
        }else {
                imageView = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("icons/folder.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        return imageView;
    }
}
