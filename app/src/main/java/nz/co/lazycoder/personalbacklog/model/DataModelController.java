package nz.co.lazycoder.personalbacklog.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nz.co.lazycoder.personalbacklog.io.SaveQueuer;
import nz.co.lazycoder.personalbacklog.model.listitems.EditableListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItems;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItemsEditor;
import nz.co.lazycoder.personalbacklog.model.listitems.MutableListItems;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class DataModelController {

    private DataModel dataModel;

    private SaveQueuer saveQueuer;

    private ControllerListEditor backlogEditor;
    private ControllerListEditor inProgressEditor;

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
                inProgressEditor = null;
                backlogEditor = null;
            }

        } catch(FileNotFoundException fileNotFoundException) {
            // swallow - leave model as empty
        } catch (IOException ex) {
            fileInputStream.close();
            throw ex;
        }
    }

    public ListItemsEditor getBacklogEditor() {
        if (backlogEditor == null) {
            backlogEditor = new ControllerListEditor(dataModel.backlogItemList);
        }
        return backlogEditor;
    }

    public ListItemsEditor getInProgressEditor() {
        if (inProgressEditor == null) {
            inProgressEditor = new ControllerListEditor(dataModel.inProgressItemList);
        }
        return inProgressEditor;
    }

    public void setInProgressListener(ListListener listener) {
        // TODO: eew cast
        ((ControllerListEditor) getInProgressEditor()).listListener = listener;
    }

    public void setBacklogListener(ListListener listener) {
        // TODO: eew cast
        ((ControllerListEditor) getBacklogEditor()).listListener = listener;
    }

    public ListItems getInProgressItemList() {
        return dataModel.inProgressItemList;
    }

    public ListItems getBacklogItemList() {
        return dataModel.backlogItemList;
    }

    public void moveItemFromBacklogToInProgress(int backlogPosition) {
        dataModel.inProgressItemList.add(dataModel.backlogItemList.remove(backlogPosition));

        notifyBothListenersAndQueueSave();
    }

    private void notifyBothListenersAndQueueSave() {
        if (inProgressEditor.listListener != null)
            inProgressEditor.listListener .onListChanged();
        if (backlogEditor.listListener != null)
            backlogEditor.listListener.onListChanged();


        queueSave();
    }

    public void moveItemFromInProgressToBacklog(int inProgressPosition) {
        dataModel.backlogItemList.add(dataModel.inProgressItemList.remove(inProgressPosition));

        notifyBothListenersAndQueueSave();
    }

    private void queueSave() {
        String jsonifiedString = new DataModelSerializer().serialize(dataModel);
        saveQueuer.queueSave(jsonifiedString, null);
    }

    private class ControllerListEditor implements  ListItemsEditor {
        private MutableListItems list;
        private ListListener listListener;

        ControllerListEditor(MutableListItems list) {
            this.list = list;
        }

        @Override
        public ListItem remove(int position) {
            ListItem listItem = list.remove(position);
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
        public EditableListItem edit(int position) {
            EditableListItem editableListItem = list.edit(position);
            notifyListenerAndQueueSave(false);
            return editableListItem;
        }

        @Override
        public void acceptEdit() {
            list.acceptEdit();
            notifyListenerAndQueueSave(true);
        }

        @Override
        public void rejectEdit() {
            list.rejectEdit();
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
