package nz.co.lazycoder.personalbacklog.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditTextWithActiveKeyboard extends EditText {

    private boolean keyboardVisible;

    private ViewTreeObserver.OnGlobalLayoutListener keyboardTrackingGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            keyboardVisible = !keyboardVisible;
        }
    };

    public EditTextWithActiveKeyboard(Context context) {
        super(context);
    }

    public EditTextWithActiveKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextWithActiveKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected  void onAttachedToWindow() {
        super.onAttachedToWindow();

        setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {

                    if (hasFocus) {
                        showKeyboard();
                    }
                }
            });

        }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {

        if (visibility == VISIBLE && visibility != getVisibility() ) {
//            requestFocus();
//            showKeyboard();
//            setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean hasFocus) {
//
//                    if (hasFocus) {
//                        showKeyboard();
//                    }
////                    else {
////                        requestFocus();
////                    }
//                }
//            });


            getRootView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardTrackingGlobalLayoutListener);
        }
//        else {
//            getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardTrackingGlobalLayoutListener);
//            hideKeyboard();
//        }
    }

    @Override
    protected void onDetachedFromWindow() {


        super.onDetachedFromWindow();
    }

    private void showKeyboard() {
        /*if (!keyboardVisible) */ {
            InputMethodManager inputMgr = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//            inputMgr.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
            keyboardVisible = true;
        }
    }

    private void hideKeyboard() {
        if (keyboardVisible) {
            InputMethodManager inputMgr = (InputMethodManager) getContext().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputMgr.toggleSoftInput(0, 0);
//            inputMgr.hide
        }
    }
}
