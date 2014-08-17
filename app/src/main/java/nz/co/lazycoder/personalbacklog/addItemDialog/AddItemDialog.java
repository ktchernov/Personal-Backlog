package nz.co.lazycoder.personalbacklog.addItemDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import nz.co.lazycoder.personalbacklog.BacklogItem;
import nz.co.lazycoder.personalbacklog.R;

/**
 * Created by ktchernov on 17/08/2014.
 */
public class AddItemDialog {
    private final AlertDialog alertDialog;

    public interface OnItemCreatedListener {
        public void onItemCreated(BacklogItem item);
    }

    public AddItemDialog(Context context, OnItemCreatedListener onItemCreatedListener) {
       alertDialog = new AlertDialogImpl(context, onItemCreatedListener);
    }


    public void show() {
        alertDialog.show();
    }

    /** Custom implementation of Alert Dialog that sets up the view as we want and brings up the keyboard */
    private class AlertDialogImpl extends AlertDialog {
        private boolean keyboardVisible;

        private final OnItemCreatedListener onItemCreatedListener;

        private final EditText titleInputView;

        public AlertDialogImpl(final Context context, final OnItemCreatedListener onItemCreatedListener) {
            super(context);
            this.onItemCreatedListener = onItemCreatedListener;

            setTitle(R.string.add_item_dialog_title);

            setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    context.getString(R.string.add_item),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String title = titleInputView.getText().toString();
                            onItemCreatedListener.onItemCreated(new BacklogItem(title));
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

            setupKeyboardFocus(dialogView, titleInputView);

            setView(dialogView);
        }

        private void setupKeyboardFocus(final View dialogView, final View titleInputView) {
            // A workaround to force focus on the title edit view and bring up the soft keyboard.
            // Based on the workaround here: http://blog.ropardo.ro/2012/08/02/showing-keyboard-automatically-in-android-dialogs/

            titleInputView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    InputMethodManager inputMgr = (InputMethodManager) getContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);

                    titleInputView.requestFocus();

                    keyboardVisible = true;
                }
            });

            dialogView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    keyboardVisible = !keyboardVisible;
                }
            });
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (keyboardVisible) {
                hideKeyboard();
            }
        }

        private void hideKeyboard() {
            InputMethodManager inputMgr = (InputMethodManager) getContext().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMgr.toggleSoftInput(0, 0);
        }
    }
}
