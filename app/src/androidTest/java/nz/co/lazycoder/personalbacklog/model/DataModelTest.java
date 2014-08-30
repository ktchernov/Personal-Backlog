package nz.co.lazycoder.personalbacklog.model;

import android.test.InstrumentationTestCase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by ktchernov on 30/08/2014.
 */
public class DataModelTest  extends InstrumentationTestCase {

    private DataModel dataModel;

    private static final ListItem[] testItems = { new ListItem("title1"), new ListItem("title2"), new ListItem("title3") };

    public void setUp() {
        dataModel = new DataModel();
    }

    public void tearDown() {

    }

    public void testConstructorShouldInitEmpty() {
        assertNoBacklogItems();
        assertNoInProgressItems();
    }

    public void testAddInProgressItem() {
        addTestDataToInProgress();

        assertNoBacklogItems();
        assertNumberOfInProgressItemsIs(testItems.length);
        assertInProgressSameAsTestItemsInReverseOrder();

    }

    private void addTestDataToInProgress() {
        for (int curIndex = 0; curIndex < testItems.length; curIndex++) {
            dataModel.addToInProgress(testItems[curIndex]);
        }
    }

    private void assertInProgressSameAsTestItemsInReverseOrder() {
        for (int curIndex = 0; curIndex < testItems.length; curIndex++) {
            assertSame(testItems[testItems.length - curIndex - 1], dataModel.getInProgressItemList().getItem(curIndex));
        }
    }

    public void testAddBacklogItem() {
        addTestDataToBacklog();

        assertNoInProgressItems();
        assertNumberOfBacklogItemsIs(testItems.length);
        assertBacklogSameAsTestItemsInReverseOrder();

    }

    private void addTestDataToBacklog() {
        for (ListItem testItem : testItems) {
            dataModel.addToBacklog(testItem);
        }
    }

    private void assertBacklogSameAsTestItemsInReverseOrder() {
        for (int curIndex = 0; curIndex < testItems.length; curIndex++) {
            assertSame(testItems[testItems.length - curIndex - 1], dataModel.getBacklogItemList().getItem(curIndex));
        }
    }


    public void testRemoveBacklogItem() {
        addTestDataToBacklog();

        assertSame(testItems[testItems.length - 1], dataModel.removeFromBacklog(0));
        assertNumberOfBacklogItemsIs(testItems.length - 1);

        for (int curIndex = 1; curIndex < testItems.length; curIndex++) {
            assertSame(testItems[testItems.length - curIndex - 1], dataModel.getBacklogItemList().getItem(curIndex - 1));
        }
    }

    public void testRemoveBacklogItemShouldFailOnInvalidInput() {
        boolean exceptionThrown = false;
        try {
            dataModel.removeFromBacklog(0);
        }
        catch (Exception ex) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }


    public void testRemoveInProgressItemShouldFailOnInvalidInput() {
        boolean exceptionThrown = false;
        try {
            dataModel.removeFromInProgress(0);
        }
        catch (Exception ex) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }


    private void assertNoInProgressItems() {
        assertNumberOfInProgressItemsIs(0);
    }

    private void assertNumberOfInProgressItemsIs(int count) {
        assertEquals(count, dataModel.getInProgressItemList().getCount());
    }

    private void assertNoBacklogItems() {
        assertNumberOfBacklogItemsIs(0);
    }

    private void assertNumberOfBacklogItemsIs(int count) {
        assertEquals(count, dataModel.getBacklogItemList().getCount());
    }


    public void testSerialize() {
        addTestDataToBacklog();
        addTestDataToInProgress();

        String serializedString = dataModel.serialize();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(serializedString);

        assertEquals(DataModel.VERSION, jsonObject.get("version").getAsString());

        JsonArray inProgressArray = jsonObject.get("inProgressItemList").getAsJsonArray();
        assertEquals(testItems.length, inProgressArray.size());

        JsonArray backLogArray = jsonObject.get("backlogItemList").getAsJsonArray();
        assertEquals(testItems.length, backLogArray.size());
    }


    public void testSerializeAndDeserialize() {
        addTestDataToBacklog();
        addTestDataToInProgress();

        String serializedString = dataModel.serialize();

        DataModel deserializedModel = DataModel.deserialize(serializedString);

        assertNotSame(dataModel, deserializedModel);
        assertEquals(dataModel, deserializedModel);

        assertEquals(dataModel.getInProgressItemList().getCount(), deserializedModel.getInProgressItemList().getCount());
        assertEquals(dataModel.getBacklogItemList().getCount(), deserializedModel.getBacklogItemList().getCount());
    }

    public void testDeserializeEmpty() {
        dataModel = DataModel.deserialize("");

        assertNoBacklogItems();
        assertNoInProgressItems();
    }

}
