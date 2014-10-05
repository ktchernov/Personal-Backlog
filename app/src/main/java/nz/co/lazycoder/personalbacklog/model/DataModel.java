package nz.co.lazycoder.personalbacklog.model;

/**
 * Created by ktchernov on 30/08/2014.
 */
class DataModel {

    final MutableListItems inProgressItemList;
    final MutableListItems backlogItemList;


    public DataModel() {
        inProgressItemList = new MutableListItems();
        backlogItemList = new MutableListItems();
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof DataModel) ) {
            return false;
        }

        DataModel otherModel = (DataModel)other;

        return backlogItemList.equals(otherModel.backlogItemList) &&
               inProgressItemList.equals(otherModel.inProgressItemList);
    }

}
