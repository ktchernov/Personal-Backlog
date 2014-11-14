package nz.co.lazycoder.personalbacklog.model.listitems;

/**
 * Created by ktchernov on 28/10/2014.
 */
public class EditableListItem extends ListItem {

    public EditableListItem(ListItem listItem) {
        super(listItem.getTitle());
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
