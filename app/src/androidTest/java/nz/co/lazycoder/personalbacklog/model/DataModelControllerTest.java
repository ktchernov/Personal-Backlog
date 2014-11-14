package nz.co.lazycoder.personalbacklog.model;

import android.test.InstrumentationTestCase;

import org.mockito.Mockito;

import nz.co.lazycoder.personalbacklog.io.SaveQueuer;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItemsEditor;

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
    private ListItemsEditor backlogListEditor;
    private ListItemsEditor inProgressListEditor;
    private ListItem testItem;

    public void setUp() {
        saveQueuer = mock(SaveQueuer.class);
        dataModelController = new DataModelController(saveQueuer);

        backlogListener = mock(DataModelController.ListListener.class);
        inProgressListener = mock(DataModelController.ListListener.class);

        dataModelController.setBacklogListener(backlogListener);
        dataModelController.setInProgressListener(inProgressListener);

        backlogListEditor = dataModelController.getBacklogEditor();
        inProgressListEditor = dataModelController.getInProgressEditor();
    }

    private void addTestItem() {
        testItem = new ListItem("test");
        backlogListEditor.add(0, testItem);
    }

    private void addTestItemToInProgress() {
        testItem = new ListItem("test");
        inProgressListEditor.add(0, testItem);
    }

    private void assertNoBacklogItems() {
        assertEquals(0, dataModelController.getBacklogItemList().size());
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
        assertEquals(0, dataModelController.getInProgressItemList().size());
    }

    private void verifyOneSaveQueued() {
        verify(saveQueuer).queueSave(anyString(), any(SaveQueuer.SaveListener.class));
    }

    private void verifyNoSavesQueued() {
        verify(saveQueuer, never()).queueSave(anyString(), any(SaveQueuer.SaveListener.class));
    }

    public void testAddItemToBacklogListEditor() {
        addTestItem();

        assertEquals(1, dataModelController.getBacklogItemList().size());
        assertSame(testItem, dataModelController.getBacklogItemList().getItem(0));

        assertBacklogListenerNotifications(1);
        assertNoInProgressListenerNotification();
        assertNoInProgressItems();

        verifyOneSaveQueued();
    }

    public void testDataModelRemoveBacklogItem() {
        addTestBacklogItemWithoutNotifications();

        dataModelController.getBacklogEditor().remove(0);

        assertBacklogListenerNotifications(1);
        assertNoInProgressListenerNotification();

        assertNoBacklogItems();
        assertNoInProgressItems();

        verifyOneSaveQueued();
    }

    public void testDataModelMoveValidItemFromBacklogToInProgress() {
        addTestBacklogItemWithoutNotifications();

        dataModelController.moveItemFromBacklogToInProgress(0);

        assertBacklogListenerNotifications(1);
        assertInProgressListenerNotifications(1);

        assertEquals(1, dataModelController.getInProgressItemList().size());
        assertSame(testItem, dataModelController.getInProgressItemList().getItem(0));

        assertNoBacklogItems();

        verifyOneSaveQueued();
    }

    public void testDataModelMoveInvalidItemFromBacklogToInProgressShouldFail() {
        boolean exceptionThrown = false;
        try {
            dataModelController.moveItemFromBacklogToInProgress(10);
        }
        catch (Exception exception) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        assertNoBacklogListenerNotification();
        assertNoInProgressListenerNotification();

        verifyNoSavesQueued();
    }

    public void testDataModelMoveInvalidItemFromInProgressToBacklogShouldFail() {
        boolean exceptionThrown = false;
        try {
            dataModelController.moveItemFromInProgressToBacklog(10);
        }
        catch (Exception exception) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        assertNoBacklogListenerNotification();
        assertNoInProgressListenerNotification();

        verifyNoSavesQueued();
    }

    public void testChangeBacklogListenerShouldTakeAffectForEditors() {
        DataModelController.ListListener newBacklogListener = mock(DataModelController.ListListener.class);
        dataModelController.setBacklogListener(newBacklogListener);
        addTestItem();

        verify(backlogListener, never()).onListChanged();
        verify(newBacklogListener).onListChanged();
    }

    public void testChangeInProgressListenerShouldTakeAffectForEditors() {
        DataModelController.ListListener newInProgressListener = mock(DataModelController.ListListener.class);
        dataModelController.setInProgressListener(newInProgressListener);
        addTestItemToInProgress();

        verify(inProgressListener, never()).onListChanged();
        verify(newInProgressListener).onListChanged();
    }

    public void testAcceptEditInBacklogListEditorShouldQueueSave() {
        addTestBacklogItemWithoutNotifications();

        dataModelController.getBacklogEditor().edit(0);
        dataModelController.getBacklogEditor().acceptEdit();

        assertBacklogListenerNotifications(2);

        verifyOneSaveQueued();
    }

    public void testRejectEditInBacklogListEditorShouldQueueNoSaves() {
        addTestBacklogItemWithoutNotifications();

        dataModelController.getBacklogEditor().edit(0);
        dataModelController.getBacklogEditor().rejectEdit();

        assertBacklogListenerNotifications(2);

        verifyNoSavesQueued();
    }

    private void addTestBacklogItemWithoutNotifications() {
        addTestItem();

        // clear notifications from test data setup
        Mockito.reset(saveQueuer, backlogListener);
    }

}
