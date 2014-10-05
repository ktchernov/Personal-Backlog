package nz.co.lazycoder.personalbacklog.model;

import android.test.InstrumentationTestCase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by ktchernov on 30/08/2014.
 */
public class DataModelSerializerTest extends InstrumentationTestCase {

    private DataModel dataModel;
    private DataModelSerializer dataModelSerializer;

    private static final ListItem[] testItems = { new ListItem("title1"), new ListItem("title2"), new ListItem("title3") };
    private static final ListItem[] testItemsInProgress = { new ListItem("ip_title1"), new ListItem("ip_title2") };

    public void setUp() {
        dataModel = new DataModel();
        dataModelSerializer = new DataModelSerializer();
    }


    public void testConstructorShouldInitEmpty() {
        assertNoBacklogItems();
        assertNoInProgressItems();
    }

    private void addTestDataToBacklog() {
        for (ListItem testItem : testItems) {
            dataModel.backlogItemList.add(testItem);
        }
    }

    private void addTestDataToInProgress() {
        for (ListItem testItemsInProgres : testItemsInProgress) {
            dataModel.inProgressItemList.add(testItemsInProgres);
        }
    }

    private void assertNoInProgressItems() {
        assertNumberOfInProgressItemsIs(0);
    }

    private void assertNumberOfInProgressItemsIs(int count) {
        assertEquals(count, dataModel.backlogItemList.size());
    }

    private void assertNoBacklogItems() {
        assertNumberOfBacklogItemsIs(0);
    }

    private void assertNumberOfBacklogItemsIs(int count) {
        assertEquals(count, dataModel.backlogItemList.size());
    }


    public void testSerialize() {
        addTestDataToBacklog();
        addTestDataToInProgress();

        String serializedString = dataModelSerializer.serialize(dataModel);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(serializedString);

        JsonArray inProgressArray = jsonObject.get("inProgressItemList").getAsJsonArray();
        assertEquals(testItemsInProgress.length, inProgressArray.size());

        JsonArray backLogArray = jsonObject.get("backlogItemList").getAsJsonArray();
        assertEquals(testItems.length, backLogArray.size());
    }


    public void testSerializeAndDeserialize() {
        addTestDataToBacklog();
        addTestDataToInProgress();

        String serializedString = dataModelSerializer.serialize(dataModel);

        DataModel deserializedModel = dataModelSerializer.deserialize(serializedString);

        assertNotSame(dataModel, deserializedModel);
        assertEquals(dataModel, deserializedModel);
    }

    public void testDeserializeEmpty() {
        dataModel = dataModelSerializer.deserialize("");

        assertNoBacklogItems();
        assertNoInProgressItems();
    }

}
