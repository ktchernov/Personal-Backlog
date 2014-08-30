package nz.co.lazycoder.personalbacklog.model;

import java.util.List;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class ListItems {

    private final List<ListItem> wrappedList;

    public ListItems(List<ListItem> wrappedList) {
        this.wrappedList = wrappedList;
    }

    public ListItem getItem(int position) {
        return wrappedList.get(position);
    }

    public int getCount() {
        return wrappedList.size();
    }
}
