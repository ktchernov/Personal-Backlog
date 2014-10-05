package nz.co.lazycoder.personalbacklog.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by ktchernov on 5/10/2014.
 */
class DataModelSerializer {

    private GsonBuilder gsonBuilder;
    public static double VERSION = 0.2;

    DataModelSerializer(){
        gsonBuilder = new GsonBuilder().setVersion(VERSION);
        gsonBuilder = gsonBuilder.registerTypeAdapter(MutableListItems.class, new MutableListItems.MutableListItemsJsonSerializer());
    }

    public DataModel deserialize(String fromString) {
        final Gson gson = gsonBuilder.create();
        DataModel model = gson.fromJson(fromString, DataModel.class);
        if (model == null) {
            model = new DataModel();
        }
        return model;
    }

    public String serialize(DataModel dataModel) {
        final Gson gson = gsonBuilder.create();
        return gson.toJson(dataModel);
    }

}
