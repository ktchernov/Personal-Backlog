package nz.co.lazycoder.personalbacklog.view;

import nz.co.lazycoder.personalbacklog.model.DataModelController;

/**
 * Created by ktchernov on 24/08/2014.
 */
class BacklogListAdapter extends ItemListAdapter {
    public BacklogListAdapter(DataModelController dataModelController) {
        super(dataModelController.getBacklogItemList());
        dataModelController.setBacklogListener(new DataModelController.ListListener() {
            @Override
            public void onListChanged() {
                notifyDataSetChanged();
            }
        });


    }
}
