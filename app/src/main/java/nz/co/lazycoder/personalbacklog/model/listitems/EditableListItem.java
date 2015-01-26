package nz.co.lazycoder.personalbacklog.model.listitems;

public class EditableListItem extends ListItem {

    public EditableListItem(ListItem listItem) {
        super(listItem == null ? "" : listItem.getTitle());
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
