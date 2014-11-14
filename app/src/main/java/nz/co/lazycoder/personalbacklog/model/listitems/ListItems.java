package nz.co.lazycoder.personalbacklog.model.listitems;

/**
 * Created by ktchernov on 24/08/2014.
 */
public interface ListItems {

    public ListItem getItem(int position);

    /**
     * @return -1 if no item is being edited
     */
    public int getEditItemPosition();

    /**
     * Get the item that is in progress of being edited with the in-progress state.
     *
     * @return null if no item is being edited
     */
    public EditableListItem getEditItem();

    public int size();

}
