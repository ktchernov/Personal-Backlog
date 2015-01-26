package nz.co.lazycoder.personalbacklog.view.itemDialog;

import android.app.AlertDialog;
import android.content.Context;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.model.listitems.EditableListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;

public abstract class ItemDialogFactory {

    public interface AddItemListener {
        void onItemAdded(ListItem newListItem);
    }

    public static AlertDialog showAddItemDialog(Context context, final AddItemListener addItemListener) {
        AlertDialog dialog;
        ItemDetailsDialogBuilder builder = new ItemDetailsDialogBuilder(context);

        final EditableListItem newItem = new EditableListItem(null);
        builder.setEditableItem(newItem);

        builder.setListener(new ItemDetailsDialogBuilder.ItemDialogListener() {
            @Override
            public void onPositiveAction() {
                addItemListener.onItemAdded(newItem);
            }

            @Override
            public void onNegativeAction() {}
        });

        dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public interface EditItemListener extends ItemDetailsDialogBuilder.ItemDialogListener {
    }

    public static AlertDialog showEditItemDialog(Context context, EditableListItem item, EditItemListener listener) {
        AlertDialog dialog;
        ItemDetailsDialogBuilder builder = new ItemDetailsDialogBuilder(context);

        builder.setListener(listener);
        builder.setEditableItem(item);
        builder.setDialogTitleId(R.string.edit_item_dialog_title);
        builder.setPositiveButtonId(R.string.edit);

        dialog = builder.create();

        dialog.show();
        return dialog;
    }
}
