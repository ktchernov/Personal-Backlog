package nz.co.lazycoder.personalbacklog.model;

/**
 * Created by ktchernov on 24/08/2014.
 */
public interface ListItems {

    public ListItem getItem(int position);

    public int size();

    public int getSelectedItemIndex();
}
