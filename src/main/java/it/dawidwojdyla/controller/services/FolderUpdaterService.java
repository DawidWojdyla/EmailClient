package it.dawidwojdyla.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.mail.Folder;
import java.util.List;

/**
 * Created by Dawid on 2020-12-04.
 */
public class FolderUpdaterService extends Service<Void> {

    private final List<Folder> folderList;

    public FolderUpdaterService(List<Folder> folderList) {
        this.folderList = folderList;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                Folder folder;
                for(;;) {
                    try {
                        Thread.sleep(5000);
                        for(int i = 0; i < folderList.size(); i++)  {
                            folder = folderList.get(i);
                            if(folder.getType() != Folder.HOLDS_FOLDERS && folder.isOpen()) {
                                folder.getMessageCount();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        };
    }
}
