package nz.co.lazycoder.personalbacklog.model;

import android.test.InstrumentationTestCase;

import nz.co.lazycoder.personalbacklog.io.SaveQueuer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class DataModelControllerTest extends InstrumentationTestCase {
    private DataModelController dataModelController;

    private SaveQueuer saveQueuer;
    private DataModelController.ListListener backlogListener;
    private DataModelController.ListListener inProgressListener;

    public void setUp() {
        saveQueuer = mock(SaveQueuer.class);
        dataModelController = new DataModelController(saveQueuer);

        backlogListener = mock(DataModelController.ListListener.class);
        inProgressListener = mock(DataModelController.ListListener.class);

        dataModelController.setBacklogListener(backlogListener);
        dataModelController.setInProgressListener(inProgressListener);
    }

    public void testDataModelShouldInitializeEmpty() {
        assertNoBacklogItems();
        assertNoInProgressItems();
    }

    private void assertNoBacklogItems() {
        assertEquals(0, dataModelController.getBacklogItemList().getCount());
    }

    private void assertNoBacklogListenerNotification() {
        verify(backlogListener, never()).onListChanged();
    }

    private void assertBacklogListenerNotifications(int numTimes) {
        verify(backlogListener, times(numTimes)).onListChanged();
    }

    private void assertInProgressListenerNotifications(int numTimes) {
        verify(inProgressListener, times(numTimes)).onListChanged();
    }

    private void assertNoInProgressListenerNotification() {
        verify(inProgressListener, never()).onListChanged();
    }


    private void assertNoInProgressItems() {
        assertEquals(0, dataModelController.getInProgressItemList().getCount());
    }

    private void verifyAtLeastOneSaveQueued() {
        verify(saveQueuer, atLeast(1)).queueSave(anyString(), any(SaveQueuer.SaveListener.class));
    }

    private void verifyNoOneSavesQueued() {
        verify(saveQueuer, never()).queueSave(anyString(), any(SaveQueuer.SaveListener.class));
    }

    public void testDataModelAddItem() {
        final ListItem testItem = new ListItem("test");
        dataModelController.addItemToBacklog(testItem);

        assertEquals(1, dataModelController.getBacklogItemList().getCount());
        assertSame(testItem, dataModelController.getBacklogItemList().getItem(0));

        assertBacklogListenerNotifications(1);
        assertNoInProgressListenerNotification();
        assertNoInProgressItems();

        verifyAtLeastOneSaveQueued();
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

        verifyAtLeastOneSaveQueued();
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

        verifyAtLeastOneSaveQueued();
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

        verifyNoOneSavesQueued();
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

        verifyNoOneSavesQueued();
    }
}
