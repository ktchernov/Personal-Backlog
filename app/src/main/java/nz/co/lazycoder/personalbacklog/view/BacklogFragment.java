package nz.co.lazycoder.personalbacklog.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.io.AsyncSaveQueuer;
import nz.co.lazycoder.personalbacklog.io.FileStringSaver;
import nz.co.lazycoder.personalbacklog.io.SaveQueuer;
import nz.co.lazycoder.personalbacklog.model.DataModelController;
import nz.co.lazycoder.personalbacklog.model.listitems.EditableListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItemsEditor;
import nz.co.lazycoder.personalbacklog.view.itemDialog.ItemDialogFactory;

import static nz.co.lazycoder.personalbacklog.view.OutsideBoundsDragSortController.DragSortControllerListener;
import static nz.co.lazycoder.personalbacklog.view.itemDialog.ItemDialogFactory.AddItemListener;
import static nz.co.lazycoder.personalbacklog.view.itemDialog.ItemDialogFactory.EditItemListener;

public class BacklogFragment extends Fragment {

    private static final String TAG = BacklogFragment.class.getSimpleName();

    private DataModelController dataModelController;

    @Bind(R.id.in_progress_list_view) DragSortListView inProgressView;
    @Bind(R.id.backlog_list_view) DragSortListView backlogView;
    @Bind(R.id.add_fab) View addButtonView;

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
        View fragmentView = inflater.inflate(R.layout.fragment_personal_backlog, container, false);

        ButterKnife.bind(this, fragmentView);

        addButtonView.setOnClickListener(new AddItemToBacklogOnClickListener());

        showHideAddButton = new ShowHideFloatingActionButton(addButtonView);

        backlogView = (DragSortListView) fragmentView.findViewById(R.id.backlog_list_view);
        setupDragListView(backlogView, dataModelController.getBacklogEditor(), new BacklogDragSortControllerListener());

        setupDragListView(inProgressView, dataModelController.getInProgressEditor(), new InProgressDragSortControllerListener());

        setupModelsAndListeners();

        return fragmentView;
    }

    private void setupModelsAndListeners() {
        final ItemListAdapter backlogListAdapter = new BacklogListAdapter(dataModelController);
        backlogView.setAdapter(backlogListAdapter);

        Context context = getActivity();
        backlogListAdapter.setOptionsMenu(R.menu.item_popup_menu,
                new OnMenuItemClickListener(context, dataModelController.getBacklogEditor()));

        ItemListAdapter inProgressListAdapter = new InProgressListAdapter(dataModelController);
        inProgressView.setAdapter(inProgressListAdapter);

        inProgressListAdapter.setOptionsMenu(R.menu.item_popup_menu,
                new OnMenuItemClickListener(context, dataModelController.getInProgressEditor()));
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
            } else {
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

    private class AddItemToBacklogOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ItemDialogFactory.showAddItemDialog(
                    getActivity(),
                    new AddItemListener() {
                        @Override
                        public void onItemAdded(ListItem newListItem) {
                            dataModelController.getBacklogEditor().add(newListItem);
                        }
                    });
        }

    }

    private static class OnMenuItemClickListener implements ItemListAdapter.OnMenuItemClickListener {

        private final Context context;
        private final ListItemsEditor editor;

        OnMenuItemClickListener (Context context, ListItemsEditor editor) {
            this.context = context;
            this.editor = editor;
        }

        @Override
        public void onMenuItemClick(int listItemPosition, int menuItemId) {
            switch(menuItemId) {
                case R.id.menu_delete:
                    editor.remove(listItemPosition);
                    break;
                case R.id.menu_edit:
                    showEditDialog(editor, listItemPosition);
                    break;
            }
        }

        private void showEditDialog(final ListItemsEditor editor, int listItemPosition) {
            EditableListItem item = editor.edit(listItemPosition);
            ItemDialogFactory.showEditItemDialog(context, item, new EditItemListener() {
                @Override
                public void onPositiveAction() {
                    editor.acceptEdit();
                }

                @Override
                public void onNegativeAction() {
                    editor.rejectEdit();
                }
            });
        }
    }
}
