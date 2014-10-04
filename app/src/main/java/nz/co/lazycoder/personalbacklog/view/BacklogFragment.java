package nz.co.lazycoder.personalbacklog.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.addItemDialog.AddItemDialog;
import nz.co.lazycoder.personalbacklog.io.AsyncSaveQueuer;
import nz.co.lazycoder.personalbacklog.io.FileStringSaver;
import nz.co.lazycoder.personalbacklog.io.SaveQueuer;
import nz.co.lazycoder.personalbacklog.model.DataModelController;
import nz.co.lazycoder.personalbacklog.model.ListItem;

public class BacklogFragment extends Fragment {

    private static final String TAG = BacklogFragment.class.getSimpleName();

    private DataModelController dataModelController;

    private ListView inProgressView;
    private ListView backlogView;
    private ActionMode actionMode;

    private ItemListAdapter backlogListAdapter;
    private ItemListAdapter inProgressListAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // TODO: this should probably not live in the Fragment
        FileStringSaver fileSaver = new FileStringSaver(getSaveFile());
        SaveQueuer saveQueuer = new AsyncSaveQueuer(fileSaver);

        dataModelController = new DataModelController(saveQueuer);

        loadDataFromDisk();
    }

    private void loadDataFromDisk() {
        File saveFile = getSaveFile();
        try {
            dataModelController.fromDisk(saveFile);
        } catch (IOException ex) {
            Log.v(TAG, "Could not load from disk", ex);
        }
    }

    private File getSaveFile() {
        return new File(getActivity().getFilesDir(), "pbl.json");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_personal_backlog, container, false);

        backlogView = (ListView) fragmentView.findViewById(R.id.backlog_list_view);
        inProgressView = (ListView) fragmentView.findViewById(R.id.in_progress_list_view);

        setupModelsAndListeners();


        View addItemButton = fragmentView.findViewById(R.id.add_row);
        addItemButton.setOnClickListener(new AddItemOnClickListener());

        return fragmentView;
    }

    private void setupModelsAndListeners() {
        backlogListAdapter = new BacklogListAdapter(dataModelController);
        backlogView.setAdapter(backlogListAdapter);

        backlogView.setOnItemLongClickListener(new AdapterOnItemLongClickListener(true));

        backlogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dataModelController.moveItemFromBacklogToInProgress(position);
            }
        });


        inProgressListAdapter = new InProgressListAdapter(dataModelController);
        inProgressView.setAdapter(inProgressListAdapter);
        inProgressView.setOnItemLongClickListener(new AdapterOnItemLongClickListener(false));
        inProgressView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dataModelController.removeItemFromInProgress(position);
            }
        });
    }


    private class AddItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AddItemDialog addItemDialog = new AddItemDialog(
                    getActivity(),
                    new AddItemDialog.OnItemCreatedListener() {
                        @Override
                        public void onItemCreated(ListItem item) {
                            dataModelController.addItemToBacklog(item);
                        }
                    });
            addItemDialog.show();
        }

    }

    private class ActionModeCallback implements ActionMode.Callback {
        private int selectedItemPosition;
        private boolean backlog;

        public ActionModeCallback(boolean backlog, int selectedItemPosition) {
            this.backlog = backlog;
            this.selectedItemPosition = selectedItemPosition;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.item_context_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete_item:
                    if (backlog) {
                        dataModelController.removeItemFromBacklog(selectedItemPosition);
                    }
                    else {
                        dataModelController.removeItemFromInProgress(selectedItemPosition);
                    }
                    actionMode.finish();
                    return true;
                default:
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            BacklogFragment.this.actionMode = null;
            if (backlog) {
                backlogListAdapter.setSelection(-1);
            }
            else {
                inProgressListAdapter.setSelection(-1);
            }
        }
    }

    private class AdapterOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private boolean isBacklogItem;

        AdapterOnItemLongClickListener(boolean isBacklogItem) {
            this.isBacklogItem = isBacklogItem;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (actionMode != null) {
                return false;
            }

            actionMode = getActivity().startActionMode(new ActionModeCallback(isBacklogItem, position));
            ((ItemListAdapter) adapterView.getAdapter()).setSelection(position);
            return true;
        }
    }
}
