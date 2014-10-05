package nz.co.lazycoder.personalbacklog.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ktchernov on 5/10/2014.
 */
public class MutableListItems implements ListItems, ListItemsEditor {
    private final List<ListItem> wrappedList;

    private int selectedItemIndex = -1;

    public MutableListItems() {
        this.wrappedList = new LinkedList<ListItem>();
    }

    @Override
    public ListItem getItem(int position) {
        return wrappedList.get(position);
    }

    @Override
    public int size() {
        return wrappedList.size();
    }

    @Override
    public ListItem remove(int position) {
        return wrappedList.remove(position);
    }

    @Override
    public ListItem removeSelected() {
        return wrappedList.remove(selectedItemIndex);
    }

    @Override
    public void move(int from, int to) {
        wrappedList.add(to, wrappedList.remove(from));
    }

    @Override
    public void add(ListItem item) {
        add(0, item);
    }

    @Override
    public void add(int position, ListItem item) {
        wrappedList.add(position, item);
    }

    @Override
    public void select(int position) {
        this.selectedItemIndex = position;
    }

    @Override
    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof MutableListItems)) {
            return false;
        }
        MutableListItems otherMutableListItems = (MutableListItems)other;

        return otherMutableListItems.selectedItemIndex == this.selectedItemIndex &&
               otherMutableListItems.wrappedList.equals(this.wrappedList);
    }


    static class MutableListItemsJsonSerializer implements JsonSerializer<MutableListItems>, JsonDeserializer<MutableListItems> {
        private static Gson gson = new Gson();

        @Override
        public JsonElement serialize(MutableListItems src, Type typeOfSrc, JsonSerializationContext context) {
            return gson.toJsonTree(src.wrappedList);
        }

        @Override
        public MutableListItems deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MutableListItems mutableListItems = new MutableListItems();
            ListItem[] listItems = gson.fromJson(json, ListItem[].class);
            mutableListItems.wrappedList.addAll(Arrays.asList(listItems));
            return mutableListItems;
        }
    }
}
