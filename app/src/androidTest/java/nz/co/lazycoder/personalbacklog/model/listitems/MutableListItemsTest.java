package nz.co.lazycoder.personalbacklog.model.listitems;

import junit.framework.TestCase;

import java.util.Arrays;

import nz.co.lazycoder.personalbacklog.model.listitems.EditableListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.MutableListItems;

/**
 * Created by ktchernov on 28/10/2014.
 */
public class MutableListItemsTest extends TestCase {

    private MutableListItems mutableListItems;

    private static final ListItem[] TEST_ITEMS = {
            new ListItem("test1"),
            new ListItem("test2"),
            new ListItem("test3"),
            new ListItem("test4"),
    };

    @Override
    public void setUp() {
        mutableListItems = new MutableListItems(Arrays.asList(TEST_ITEMS));
    }

    public void testGetItemWithValidIndex() {
        for (int i = 0; i < TEST_ITEMS.length; i++) {
            assertSame(TEST_ITEMS[i], mutableListItems.getItem(i));
        }
    }

    public void testGetItemWithInvalidLargeIndexShouldThrowException() {
        assertInvalidIndexThrowsException(TEST_ITEMS.length);
    }

    public void testGetItemWithInvalidNegativeIndexShouldThrowException() {
        assertInvalidIndexThrowsException(-1);
    }

    private void assertInvalidIndexThrowsException(int invalidIndex) {
        boolean exceptionThrown = false;
        try {
            mutableListItems.getItem(invalidIndex);
        }
        catch (Exception ex) {
            exceptionThrown = true;
        }
        assertTrue("getItem(" + invalidIndex + ") should throw exception", exceptionThrown);
    }


    public void testSize() {
        assertEquals(TEST_ITEMS.length, mutableListItems.size());
    }

    public void testRemoveWithValidIndex() {
        for (int i = 0; i < TEST_ITEMS.length; i++) {
            ListItem removedItem = mutableListItems.remove(0);
            assertSame(TEST_ITEMS[i], removedItem);
            assertEquals(TEST_ITEMS.length - i - 1, mutableListItems.size());
        }
    }

    public void testRemoveWithInvalidLargeIndexShouldThrowException() {
        assertRemoveWithInvalidIndexShouldThrowException(TEST_ITEMS.length);
    }

    public void testRemoveWithInvalidNegativeIndexShouldThrowException() {
        assertRemoveWithInvalidIndexShouldThrowException(-1);
    }

    private void assertRemoveWithInvalidIndexShouldThrowException(int invalidIndex) {
        boolean exceptionThrown = false;
        try {
            mutableListItems.remove(invalidIndex);
        }
        catch (Exception ex) {
            exceptionThrown = true;
        }
        assertTrue("remove(" + invalidIndex + ") should throw exception", exceptionThrown);
    }

    public void testMove() {
    }

    public void testAddShouldAddToBeginning() {
        ListItem newTestItem = new ListItem("New test item");
        mutableListItems.add(newTestItem);

        assertEquals(TEST_ITEMS.length + 1, mutableListItems.size());
        assertSame(newTestItem, mutableListItems.getItem(0));
    }

    public void testAddWithValidPosition() {
        assertAddAtValidPositionSucceeds(TEST_ITEMS.length / 2);
    }

    public void testAddAtLastPosition() {
        assertAddAtValidPositionSucceeds(TEST_ITEMS.length);
    }

    public void testAddAtZeroPosition() {
        assertAddAtValidPositionSucceeds(0);
    }

    private void assertAddAtValidPositionSucceeds(int validPosition) {
        ListItem newTestItem = new ListItem("New test item");
        mutableListItems.add(validPosition, newTestItem);

        assertEquals(TEST_ITEMS.length + 1, mutableListItems.size());

        for (int i = 0; i < validPosition; i++) {
            assertSame(TEST_ITEMS[i], mutableListItems.getItem(i));
        }

        assertSame(newTestItem, mutableListItems.getItem(validPosition));

        for (int i = validPosition + 1; i < mutableListItems.size(); i++) {
            assertSame(TEST_ITEMS[i-1], mutableListItems.getItem(i));
        }
    }

    public void testAddWithInvalidNegativePositionShouldThrowException() {
        assertAddWithInvalidPositionShouldThrowException(-1);
    }


    public void testAddWithInvalidLargePositionShouldThrowException() {
        assertAddWithInvalidPositionShouldThrowException(TEST_ITEMS.length + 1);
    }

    private void assertAddWithInvalidPositionShouldThrowException(int invalidIndex) {
        ListItem newTestItem = new ListItem("New test item");
        boolean exceptionThrown = false;
        try {
            mutableListItems.add(invalidIndex, newTestItem);
        }
        catch (Exception ex) {
            exceptionThrown = true;
        }
        assertTrue("add at index" + invalidIndex + " should throw exception", exceptionThrown);
    }

    public void testEditWithValidIndexShouldSetItemAsEditable() {
        int position = TEST_ITEMS.length / 2;
        EditableListItem editableListItem = mutableListItems.edit(position);

        assertSame(TEST_ITEMS[position], mutableListItems.getItem(position));

        assertNotSame(TEST_ITEMS[position], editableListItem);
        assertEquals(TEST_ITEMS[position], editableListItem);
    }

    public void testEditWithNegativeIndexShouldThrowException() {
        assertEditWithInvalidPositionShouldThrowException(-1);
    }

    public void testEditWithLargeIndexShouldThrowException() {
        assertEditWithInvalidPositionShouldThrowException(TEST_ITEMS.length);
    }

    private void assertEditWithInvalidPositionShouldThrowException(int invalidPosition) {
        boolean exceptionThrown = false;
        try {
            mutableListItems.edit(invalidPosition);
        }
        catch (Exception ex) {
            exceptionThrown = true;
        }
        assertTrue("edit(" + invalidPosition + ") should throw exception", exceptionThrown);
    }

    public void testGetItemPositionShouldStartAsNegative() {
        assertTrue(mutableListItems.getEditItemPosition() < 0);
    }

    public void testGetItemPositionShouldBeReturned() {
        final int position = TEST_ITEMS.length / 2;
        mutableListItems.edit(position);
        assertEquals(position, mutableListItems.getEditItemPosition());
    }

    public void testGetEditItemShouldStartAsNull() {
        assertNull(mutableListItems.getEditItem());
    }

    public void testGetEditItemBeingEditedShouldBeReturned() {
        final int position = TEST_ITEMS.length / 2;
        EditableListItem editableListItem = mutableListItems.edit(position);
        assertSame(editableListItem, mutableListItems.getEditItem());
    }

    public void testAcceptEdit() {
        final String newTitle = "New title after edit";
        final int position = TEST_ITEMS.length / 2;

        EditableListItem editableListItem = mutableListItems.edit(position);
        editableListItem.setTitle(newTitle);
        mutableListItems.acceptEdit();

        assertNotSame(TEST_ITEMS[position], mutableListItems.getItem(position));
        assertNotSame(editableListItem,  mutableListItems.getItem(position));
        assertEquals(new ListItem(newTitle),  mutableListItems.getItem(position));

        assertNotEditing();
    }

    public void testRejectEdit() {
        final String newTitle = "New title after edit";
        final int position = TEST_ITEMS.length / 2;

        EditableListItem editableListItem = mutableListItems.edit(position);
        editableListItem.setTitle(newTitle);
        mutableListItems.rejectEdit();

        assertEquals(TEST_ITEMS[position], mutableListItems.getItem(position));
        assertFalse("Title must not change", newTitle.equals(mutableListItems.getItem(position).getTitle()));

        assertNotEditing();
    }

    private void assertNotEditing() {
        assertNull(mutableListItems.getEditItem());
        assertTrue(mutableListItems.getEditItemPosition() < 0);
    }
}
