package nz.co.lazycoder.personalbacklog.view.addItemDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;

public class AddItemDialog {
    private final AlertDialog alertDialog;

    public interface OnItemCreatedListener {
        public void onItemCreated(ListItem item);
    }

    public AddItemDialog(Context context, OnItemCreatedListener onItemCreatedListener) {
       alertDialog = new AlertDialogImpl(context, onItemCreatedListener);
    }


    public void show() {
        alertDialog.show();
    }

    /** Custom implementation of Alert Dialog that sets up the view as we want and brings up the keyboard */
    private class AlertDialogImpl extends AlertDialog {

        private final EditText titleInputView;

        public AlertDialogImpl(final Context context, final OnItemCreatedListener onItemCreatedListener) {
            super(context);

            setTitle(R.string.add_item_dialog_title);

            setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    context.getString(R.string.add_item),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String title = titleInputView.getText().toString();
                            onItemCreatedListener.onItemCreated(new ListItem(title));
                        }
                    });

            setButton(
                    AlertDialog.BUTTON_NEGATIVE,
                    context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            View dialogView = LayoutInflater.from(context).inflate(R.layout.add_item_dialog, null, false);
            titleInputView = (EditText) dialogView.findViewById(R.id.title_input_view);

            setView(dialogView);
        }
    }
}
