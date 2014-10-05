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

                dataModel = new DataModelSerializer().deserialize(jsonString);
            }

        } catch(FileNotFoundException fileNotFoundException) {
            // swallow - leave model as empty
        } catch (IOException ex) {
            fileInputStream.close();
            throw ex;
        }
    }

    public ListItemsEditor getBacklogEditor() {
        return new ControllerListEditor(dataModel.backlogItemList, backlogListener);
    }

    public ListItemsEditor getInProgressEditor() {
        return new ControllerListEditor(dataModel.inProgressItemList, inProgressListener);
    }

    public void setInProgressListener(ListListener listener) {
        inProgressListener = listener;
    }

    public void setBacklogListener(ListListener listener) {
        backlogListener = listener;
    }

    public ListItems getInProgressItemList() {
        return dataModel.inProgressItemList;
    }

    public ListItems getBacklogItemList() {
        return dataModel.backlogItemList;
    }

    public void moveItemFromBacklogToInProgress(int backlogPosition) {
        dataModel.inProgressItemList.add(dataModel.backlogItemList.remove(backlogPosition));

        notifyInProgressChanged();
        notifyBacklogChanged();
    }

    public void moveItemFromInProgressToBacklog(int inProgressPosition) {
        dataModel.backlogItemList.add(dataModel.inProgressItemList.remove(inProgressPosition));

        notifyInProgressChanged();
        notifyBacklogChanged();
    }

    private void notifyInProgressChanged() {
        if (inProgressListener != null)
            inProgressListener.onListChanged();

        queueSave();
    }

    private void notifyBacklogChanged() {
        if (backlogListener != null)
            backlogListener.onListChanged();

        queueSave();
    }

    private void queueSave() {
        String jsonifiedString = new DataModelSerializer().serialize(dataModel);
        saveQueuer.queueSave(jsonifiedString, null);
    }

    private class ControllerListEditor implements  ListItemsEditor {
        private MutableListItems list;
        private ListListener listListener;

        ControllerListEditor(MutableListItems list, ListListener listListener) {
            this.list = list;
            this.listListener = listListener;
        }

        @Override
        public ListItem remove(int position) {
            ListItem listItem = list.remove(position);
            notifyListenerAndQueueSave(true);
            return listItem;
        }

        @Override
        public ListItem removeSelected() {
            ListItem listItem = list.removeSelected();
            notifyListenerAndQueueSave(true);
            return listItem;
        }

        @Override
        public void move(int from, int to) {
            list.add(to, list.remove(from));
            notifyListenerAndQueueSave(true);
        }

        @Override
        public void add(ListItem item) {
            list.add(item);
            notifyListenerAndQueueSave(true);
        }

        @Override
        public void add(int position, ListItem item) {
            list.add(position, item);
            notifyListenerAndQueueSave(true);
        }

        @Override
        public void select(int position) {
            list.select(position);
            notifyListenerAndQueueSave(false);
        }

        private void notifyListenerAndQueueSave(boolean queueSave) {
            if (listListener != null) {
                listListener.onListChanged();
            }
            if (queueSave) {
                queueSave();
            }
        }
    }
}
