package nz.co.lazycoder.personalbacklog.model;

/**
 * Created by ktchernov on 16/08/2014.
 */
public class ListItem {
    private String title;

    public ListItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object other) {
        return this.title.equals( ((ListItem)other).title );
    }
}
