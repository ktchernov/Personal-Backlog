package nz.co.lazycoder.personalbacklog.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import static nz.co.lazycoder.personalbacklog.view.OutsideBoundsDragSortController.DragSortControllerListener;

public class BacklogFragment extends Fragment {

    private static final String TAG = BacklogFragment.class.getSimpleName();

    private DataModelController dataModelController;

    private DragSortListView inProgressView;
    private DragSortListView backlogView;
    private ShowHideFloatingActionButton showHideAddButton;


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

        View addButtonView = fragmentView.findViewById(R.id.add_row);
        addButtonView.setOnClickListener(new AddItemOnClickListener());

        showHideAddButton = new ShowHideFloatingActionButton(addButtonView);

        backlogView = (DragSortListView) fragmentView.findViewById(R.id.backlog_list_view);
        setupDragListView(backlogView, dataModelController.getBacklogEditor(), new BacklogDragSortControllerListener());

        inProgressView = (DragSortListView) fragmentView.findViewById(R.id.in_progress_list_view);
        setupDragListView(inProgressView, dataModelController.getInProgressEditor(), new InProgressDragSortControllerListener());

        setupModelsAndListeners();

        return fragmentView;
    }

    private void setupModelsAndListeners() {
        ItemListAdapter backlogListAdapter = new BacklogListAdapter(dataModelController);
        backlogView.setAdapter(backlogListAdapter);

        backlogListAdapter.setOptionsMenu(R.menu.item_popup_menu, new ItemListAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int listItemPosition, int menuItemId) {
                dataModelController.getBacklogEditor().remove(listItemPosition);
            }
        });

        ItemListAdapter inProgressListAdapter = new InProgressListAdapter(dataModelController);
        inProgressView.setAdapter(inProgressListAdapter);

        inProgressListAdapter.setOptionsMenu(R.menu.item_popup_menu, new ItemListAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int listItemPosition, int menuItemId) {
                dataModelController.getInProgressEditor().remove(listItemPosition);
            }
        });
    }

    private void setupDragListView(DragSortListView dragSortListView, ListItemsEditor editor,
                                   DragSortControllerListener dragSortControllerListener) {
        dragSortListView.setDropListener(new ListDropListener(editor));
        dragSortListView.setRemoveListener(new ListRemoveListener(editor));

        DragSortController dragSortController = new OutsideBoundsDragSortController(
                dragSortListView, dragSortControllerListener);

        dragSortController.setDragHandleId(ItemListAdapter.getDragHandleId());
        dragSortController.setRemoveEnabled(true);

        dragSortListView.setFloatViewManager(dragSortController);
        dragSortListView.setOnTouchListener(dragSortController);
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

    private static class ListDropListener implements DragSortListView.DropListener {

        private final ListItemsEditor editor;

        ListDropListener(ListItemsEditor editor) {
            this.editor = editor;
        }

        @Override
        public void drop(int from, int to) {
            editor.move(from, to);
        }
    }

    private static class ListRemoveListener implements DragSortListView.RemoveListener {

        private final ListItemsEditor editor;

        ListRemoveListener(ListItemsEditor editor) {
            this.editor = editor;
        }

        @Override
        public void remove(int which) {
            editor.remove(which);
        }
    }

    private class BacklogDragSortControllerListener implements DragSortControllerListener {

        @Override
        public void onDragStarted() {
            showHideAddButton.hide();
        }

        @Override
        public void onDragFinished() {
            showHideAddButton.show();
        }

        @Override
        public void onScrollWithoutDragging(boolean down) {
            if (down) {
                showHideAddButton.hide();
            }
            else {
                showHideAddButton.show();
            }
        }

        @Override
        public void onDraggedOutsideBounds(int position, boolean down) {
            if (!down) {
                dataModelController.moveItemFromBacklogToInProgress(position);
            }
        }
    }

    private class InProgressDragSortControllerListener implements DragSortControllerListener {

        @Override
        public void onDragStarted() {}

        @Override
        public void onDragFinished() {}

        @Override
        public void onScrollWithoutDragging(boolean down) {}

        @Override
        public void onDraggedOutsideBounds(int position, boolean down) {
            if (down) {
                dataModelController.moveItemFromInProgressToBacklog(position);
            }
        }
    }
}
