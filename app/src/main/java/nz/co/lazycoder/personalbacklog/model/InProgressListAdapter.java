package nz.co.lazycoder.personalbacklog.model;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class InProgressListAdapter extends ItemListAdapter {

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
