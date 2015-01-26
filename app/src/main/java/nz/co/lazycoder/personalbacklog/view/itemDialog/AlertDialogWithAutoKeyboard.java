package nz.co.lazycoder.personalbacklog.view.itemDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/** Custom implementation of Alert Dialog that sets up the view as we want and brings up the keyboard */
public class AlertDialogWithAutoKeyboard extends AlertDialog {

    protected AlertDialogWithAutoKeyboard(Context context) {
        super(context);
    }

    protected void setupKeyboardFocus(final EditText titleInputView) {
        // A workaround to force focus on the title edit view and bring up the soft keyboard.
        // Based on the workaround here: http://blog.ropardo.ro/2012/08/02/showing-keyboard-automatically-in-android-dialogs/

        titleInputView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                showKeyboard();

                titleInputView.requestFocus();
                titleInputView.setSelection(titleInputView.getText().length());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyboard();
    }

    private void showKeyboard() {
        InputMethodManager inputMgr = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard() {
        InputMethodManager inputMgr = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.toggleSoftInput(0, 0);
    }
}
