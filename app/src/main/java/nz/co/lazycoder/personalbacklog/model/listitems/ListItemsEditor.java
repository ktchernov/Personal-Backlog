package nz.co.lazycoder.personalbacklog.model.listitems;

import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;

/**
 * Created by ktchernov on 5/10/2014.
 */
public interface ListItemsEditor {
    public ListItem remove(int position);

    public void move(int from, int to);
    public void add(ListItem item);
    public void add(int position, ListItem item);

    public EditableListItem edit(int position);
    public void acceptEdit();
    public void rejectEdit();
}

