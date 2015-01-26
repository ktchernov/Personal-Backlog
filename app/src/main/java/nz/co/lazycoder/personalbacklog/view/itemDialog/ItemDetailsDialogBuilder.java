package nz.co.lazycoder.personalbacklog.view.itemDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.model.listitems.EditableListItem;

class ItemDetailsDialogBuilder {
    private Params params;

    ItemDetailsDialogBuilder(Context context) {
        params = new Params(context);
    }

    void setEditableItem(EditableListItem item) {
        params.item = item;
    }


    void setDialogTitleId(int titleId) {
        params.titleId = titleId;
    }

    void setPositiveButtonId(int positiveButtonId) {
        params.positiveButtonId = positiveButtonId;
    }

    void setListener(ItemDialogListener listener) {
        params.listener = listener;
    }

    AlertDialog create() {
        return new AlertDialogImpl(params);
    }

    private class AlertDialogImpl extends AlertDialogWithAutoKeyboard {
        private final EditText titleInputView;

        public AlertDialogImpl(final Params params) {
            super(params.context);

            setTitle(params.titleId);

            setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    params.context.getString(params.positiveButtonId),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateUnderlyingItem(params.item);

                            params.listener.onPositiveAction();
                        }
                    });

            setButton(
                    AlertDialog.BUTTON_NEGATIVE,
                    params.context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            params.listener.onNegativeAction();
                        }
                    });

            View dialogView = LayoutInflater.from(params.context).inflate(R.layout.item_dialog, null, false);
            titleInputView = (EditText) dialogView.findViewById(R.id.title_input_view);

            titleInputView.setText(params.item.getTitle());

            setupKeyboardFocus(titleInputView);

            setView(dialogView);
        }

        private void updateUnderlyingItem(EditableListItem item) {
            String title = titleInputView.getText().toString();
            item.setTitle(title);
        }
    }


    private class Params {
        final Context context;
        EditableListItem item;

        int titleId;
        int positiveButtonId;

        ItemDialogListener listener;

        Params(Context context) {
            this.context = context;

            titleId = R.string.add_item_dialog_title;
            positiveButtonId = R.string.add_item;
        }
    }

    /**
     * Created by ktchernov on 26/01/2015.
     */
    static interface ItemDialogListener {
        void onPositiveAction();
        void onNegativeAction();
    }
}
