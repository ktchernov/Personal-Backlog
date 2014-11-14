package nz.co.lazycoder.personalbacklog.model.listitems;

import junit.framework.TestCase;

import nz.co.lazycoder.personalbacklog.model.listitems.EditableListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;

/**
 * Created by ktchernov on 28/10/2014.
 */
public class EditableListItemTest extends TestCase {

    private EditableListItem editableListItem;
    private ListItem immutableListItem;

    private static final String TEST_TEXT = "Test 1";

    @Override
    public void setUp() {
        immutableListItem = new ListItem(TEST_TEXT);
        editableListItem = new EditableListItem(immutableListItem);
    }

    public void testSetTextShouldChangeEditableTextButNotWrappedItemText() {
        final String editedText = "Edited text";
        editableListItem.setTitle(editedText);

        assertEquals(editedText, editableListItem.getTitle());
        assertEquals(TEST_TEXT, immutableListItem.getTitle());
    }
}
