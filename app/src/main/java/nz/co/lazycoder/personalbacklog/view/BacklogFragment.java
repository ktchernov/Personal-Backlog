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

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.io.File;
import java.io.IOException;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.addItemDialog.AddItemDialog;
import nz.co.lazycoder.personalbacklog.io.AsyncSaveQueuer;
import nz.co.lazycoder.personalbacklog.io.FileStringSaver;
import nz.co.lazycoder.personalbacklog.io.SaveQueuer;
import nz.co.lazycoder.personalbacklog.model.DataModelController;
import nz.co.lazycoder.personalbacklog.model.ListItem;
import nz.co.lazycoder.personalbacklog.model.ListItemsEditor;

public class BacklogFragment extends Fragment {

    private static final String TAG = BacklogFragment.class.getSimpleName();

    private DataModelController dataModelController;

    private DragSortListView inProgressView;
    private DragSortListView backlogView;
    private View addButtonView;


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

        backlogView = (DragSortListView) fragmentView.findViewById(R.id.backlog_list_view);
        setupDragListView(backlogView);

        inProgressView = (DragSortListView) fragmentView.findViewById(R.id.in_progress_list_view);
        setupDragListView(inProgressView);

        addButtonView = fragmentView.findViewById(R.id.add_row);

        setupModelsAndListeners();


        View addItemButton = fragmentView.findViewById(R.id.add_row);
        addItemButton.setOnClickListener(new AddItemOnClickListener());

        return fragmentView;
    }

    private void setupDragListView(DragSortListView dragSortListView) {
        dragSortListView.setDropListener(new ListDropListener());
        dragSortListView.setRemoveListener(new ListRemoveListener());

        DragSortController dragSortController = new DragSortController(dragSortListView);
        dragSortController.setDragHandleId(ItemListAdapter.getDragHandleId());

        dragSortListView.setFloatViewManager(dragSortController);
        dragSortListView.setOnTouchListener(dragSortController);
    }

    private void setupModelsAndListeners() {
        backlogListAdapter = new BacklogListAdapter(dataModelController);
        backlogView.setAdapter(backlogListAdapter);

        backlogView.setOnItemLongClickListener(new AdapterOnItemLongClickListener(dataModelController.getBacklogEditor()));

        backlogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dataModelController.moveItemFromBacklogToInProgress(position);
            }
        });


        inProgressListAdapter = new InProgressListAdapter(dataModelController);
        inProgressView.setAdapter(inProgressListAdapter);
        inProgressView.setOnItemLongClickListener(new AdapterOnItemLongClickListener(dataModelController.getInProgressEditor()));
        inProgressView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dataModelController.getInProgressEditor().remove(position);
            }
        });
    }

    private void disableEditing() {
        inProgressView.setEnabled(false);
        backlogView.setEnabled(false);
        addButtonView.setEnabled(false);
    }

    private void enableEditing() {
        inProgressView.setEnabled(true);
        backlogView.setEnabled(true);
        addButtonView.setEnabled(true);
    }

    private class AddItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AddItemDialog addItemDialog = new AddItemDialog(
                    getActivity(),
                    new AddItemDialog.OnItemCreatedListener() {
                        @Override
                        public void onItemCreated(ListItem item) {
                            dataModelController.getBacklogEditor().add(item);
                        }
                    });
            addItemDialog.show();
        }

    }

    private class ActionModeCallback implements ActionMode.Callback {
        private ListItemsEditor editor;

        public ActionModeCallback(ListItemsEditor editor) {
            this.editor = editor;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.item_context_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            disableEditing();
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete_item:
                    editor.removeSelected();
                    actionMode.finish();
                    return true;
                default:
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            BacklogFragment.this.actionMode = null;
            editor.select(-1);
            enableEditing();
        }
    }

    private class AdapterOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private ListItemsEditor editor;

        AdapterOnItemLongClickListener(ListItemsEditor editor) {
            this.editor = editor;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (actionMode != null) {
                return false;
            }

            actionMode = getActivity().startActionMode(new ActionModeCallback(editor));
            editor.select(position);
            return true;
        }
    }


    private class ListDropListener implements DragSortListView.DropListener {
        @Override
        public void drop(int from, int to) {
            dataModelController.getBacklogEditor().move(from, to);
        }
    }

    private class ListRemoveListener implements DragSortListView.RemoveListener {
        @Override
        public void remove(int which) {
            dataModelController.getBacklogEditor().remove(which);
        }
    }
}
