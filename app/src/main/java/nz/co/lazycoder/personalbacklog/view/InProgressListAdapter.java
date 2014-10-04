package nz.co.lazycoder.personalbacklog.view;

import nz.co.lazycoder.personalbacklog.model.DataModelController;

/**
 * Created by ktchernov on 24/08/2014.
 */
class InProgressListAdapter extends ItemListAdapter {

    public InProgressListAdapter(DataModelController dataModelController) {
        super(dataModelController.getInProgressItemList());
        dataModelController.setInProgressListener(new DataModelController.ListListener() {
            @Override
            public void onListChanged() {
                notifyDataSetChanged();
            }
        });
    }
}
