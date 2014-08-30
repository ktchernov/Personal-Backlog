package nz.co.lazycoder.personalbacklog.model;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ktchernov on 30/08/2014.
 */
class DataModel {

    public final static String VERSION = "0.1";

    private final String version = VERSION;

    private final List<ListItem> inProgressItemList;
    private final List<ListItem> backlogItemList;

    private transient ListItems readonlyInProgressItemList;
    private transient ListItems readonlyBacklogItemList;


    public DataModel() {
        inProgressItemList = new LinkedList<ListItem>();
        backlogItemList = new LinkedList<ListItem>();

        initReadOnlyListItems();
    }

    public static DataModel deserialize(String fromString) {
        final Gson gson = new Gson();
        DataModel model = gson.fromJson(fromString, DataModel.class);
        if (model == null) {
            model = new DataModel();
        }
        else {
            model.initReadOnlyListItems();
        }
        return model;
    }

    private void initReadOnlyListItems() {
        readonlyInProgressItemList = new ListItems(inProgressItemList);
        readonlyBacklogItemList = new ListItems(backlogItemList);
    }

    public String serialize() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }




    public ListItems getInProgressItemList() {
        return readonlyInProgressItemList;
    }

    public void addToInProgress(ListItem item) {
        inProgressItemList.add(0, item);
    }

    public ListItem removeFromInProgress(int position) {
        return inProgressItemList.remove(position);
    }

    public ListItems getBacklogItemList() {
        return readonlyBacklogItemList;
    }

    public void addToBacklog(ListItem item) {
        backlogItemList.add(0, item);
    }

    public ListItem removeFromBacklog(int position) {
        return backlogItemList.remove(position);
    }

    @Override
    public boolean equals(Object other) {
        DataModel otherModel = (DataModel)other;

        if (!backlogItemList.equals(otherModel.backlogItemList)) {
            return false;
        }
        return inProgressItemList.equals(otherModel.backlogItemList);
    }

}
