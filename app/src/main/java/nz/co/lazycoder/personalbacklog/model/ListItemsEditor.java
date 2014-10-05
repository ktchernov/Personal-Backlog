package nz.co.lazycoder.personalbacklog.model;

/**
 * Created by ktchernov on 5/10/2014.
 */
public interface ListItemsEditor {
    public ListItem remove(int position);
    public ListItem removeSelected();
    public void move(int from, int to);
    public void add(ListItem item);
    public void add(int position, ListItem item);
    public void select(int position);
}

