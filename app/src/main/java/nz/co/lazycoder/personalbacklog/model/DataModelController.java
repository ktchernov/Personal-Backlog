package nz.co.lazycoder.personalbacklog.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nz.co.lazycoder.personalbacklog.io.SaveQueuer;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class DataModelController {

    private DataModel dataModel;

    private SaveQueuer saveQueuer;

    private ListListener inProgressListener;
    private ListListener backlogListener;

    public interface ListListener {
        void onListChanged();
    }

    public DataModelController(SaveQueuer saveQueuer) {
        dataModel = new DataModel();
        this.saveQueuer = saveQueuer;
    }

    public void fromDisk(File jsonFile) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);

            byte [] buffer = new byte[32 * 1024];
            int read = fileInputStream.read(buffer);
            if (read > 0) {
                byte [] destBuffer= new byte[read];
                System.arraycopy(buffer, 0, destBuffer, 0, read);
                String jsonString = new String(destBuffer, "UTF-8");

                dataModel = DataModel.deserialize(jsonString);
            }

        } catch(FileNotFoundException fileNotFoundException) {
            // swallow - leave model as empty
        } catch (IOException ex) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException closeException) { }
            }
            throw ex;
        }
    }

    public void setInProgressListener(ListListener listener) {
        inProgressListener = listener;
    }

    public void setBacklogListener(ListListener listener) {
        backlogListener = listener;
    }

    public ListItems getInProgressItemList() {
        return dataModel.getInProgressItemList();
    }

    public ListItems getBacklogItemList() {
        return dataModel.getBacklogItemList();
    }

    public void addItemToBacklog(ListItem newItem) {
        dataModel.addToBacklog(newItem);

        notifyBacklogChanged();
    }


    public void removeItemFromBacklog(int position) {
        dataModel.removeFromBacklog(position);

        notifyBacklogChanged();
    }

    public void removeItemFromInProgress(int position) {
        dataModel.removeFromInProgress(position);

        notifyInProgressChanged();
    }


    public void moveItemFromBacklogToInProgress(int backlogPosition) {
        dataModel.addToInProgress(dataModel.removeFromBacklog(backlogPosition));

        notifyInProgressChanged();
        notifyBacklogChanged();
    }

    public void moveItemFromInProgressToBacklog(int inProgressPosition) {
        dataModel.addToInProgress(dataModel.removeFromBacklog(inProgressPosition));

        notifyInProgressChanged();
        notifyBacklogChanged();
    }

    private void notifyInProgressChanged() {
        if (inProgressListener != null)
            inProgressListener.onListChanged();

        onDataChanged();
    }

    private void notifyBacklogChanged() {
        if (backlogListener != null)
            backlogListener.onListChanged();

        onDataChanged();
    }

    private void onDataChanged() {
        String jsonifiedString = dataModel.serialize();
        saveQueuer.queueSave(jsonifiedString, null);
    }
}
