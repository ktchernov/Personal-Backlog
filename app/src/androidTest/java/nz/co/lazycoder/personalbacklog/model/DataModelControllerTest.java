package nz.co.lazycoder.personalbacklog.model;

import android.test.InstrumentationTestCase;

import org.mockito.Mockito;

import java.io.File;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class DataModelControllerTest extends InstrumentationTestCase {
    private DataModelController dataModelController;

    private DataModelController.ListListener backlogListener;
    private DataModelController.ListListener inProgressListener;

    private File testFile;

    public void setUp() {
        dataModelController = new DataModelController();

        backlogListener = Mockito.mock(DataModelController.ListListener.class);
        inProgressListener = Mockito.mock(DataModelController.ListListener.class);

        dataModelController.setBacklogListener(backlogListener);
        dataModelController.setInProgressListener(inProgressListener);
    }

    public void tearDown() {
        if (testFile != null) {
            testFile.delete();
            testFile = null;
        }
    }

    public void testDataModelShouldInitializeEmpty() {
        assertNoBacklogItems();
        assertNoInProgressItems();
    }

    private void setupSomeTestData() {
        final ListItem testItem1 = new ListItem("test1");
        final ListItem testItem2 = new ListItem("test2");
        dataModelController.addItemToBacklog(testItem1);
        dataModelController.addItemToBacklog(testItem2);
        dataModelController.moveItemFromBacklogToInProgress(1);
    }

    private void assertNoBacklogItems() {
        assertEquals(0, dataModelController.getBacklogItemList().getCount());
    }

    private void assertNoBacklogListenerNotification() {
        Mockito.verify(backlogListener, Mockito.never()).onListChanged();
    }

    private void assertBacklogListenerNotifications(int numTimes) {
        Mockito.verify(backlogListener, Mockito.times(numTimes)).onListChanged();
    }

    private void assertInProgressListenerNotifications(int numTimes) {
        Mockito.verify(inProgressListener, Mockito.times(numTimes)).onListChanged();
    }

    private void assertNoInProgressListenerNotification() {
        Mockito.verify(inProgressListener, Mockito.never()).onListChanged();
    }


    private void assertNoInProgressItems() {
        assertEquals(0, dataModelController.getInProgressItemList().getCount());
    }

    public void testDataModelAddItem() {
        final ListItem testItem = new ListItem("test");
        dataModelController.addItemToBacklog(testItem);

        assertEquals(1, dataModelController.getBacklogItemList().getCount());
        assertSame(testItem, dataModelController.getBacklogItemList().getItem(0));

        assertBacklogListenerNotifications(1);
        assertNoInProgressListenerNotification();
        assertNoInProgressItems();
    }

    public void testDataModelRemoveBacklogItem() {
        final ListItem testItem = new ListItem("test");
        // TODO: use constructor instead
        dataModelController.addItemToBacklog(testItem);

        dataModelController.removeItemFromBacklog(0);

        assertBacklogListenerNotifications(2);
        assertNoInProgressListenerNotification();

        assertNoBacklogItems();
        assertNoInProgressItems();
    }

    public void testDataModelMoveValidItemFromBacklogToInProgress() {
        final ListItem testItem = new ListItem("test");
        // TODO: use constructor instead
        dataModelController.addItemToBacklog(testItem);

        dataModelController.moveItemFromBacklogToInProgress(0);

        assertBacklogListenerNotifications(2);
        assertInProgressListenerNotifications(1);

        assertEquals(1, dataModelController.getInProgressItemList().getCount());
        assertSame(testItem, dataModelController.getInProgressItemList().getItem(0));

        assertNoBacklogItems();
    }

    public void testDataModelMoveInvalidItemFromBacklogToInProgressShouldFail() {
        boolean exceptionThrown = false;
        try {
            dataModelController.moveItemFromBacklogToInProgress(0);
        }
        catch (Exception exception) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        assertNoBacklogListenerNotification();
        assertNoInProgressListenerNotification();
    }

    public void testDataModelMoveInvalidItemFromInProgressToBacklogShouldFail() {
        boolean exceptionThrown = false;
        try {
            dataModelController.moveItemFromInProgressToBacklog(0);
        }
        catch (Exception exception) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        assertNoBacklogListenerNotification();
        assertNoInProgressListenerNotification();
    }
}
