package nz.co.lazycoder.personalbacklog.model;

/**
 * Created by ktchernov on 24/08/2014.
 */
public class BacklogListAdapter extends ItemListAdapter {
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
