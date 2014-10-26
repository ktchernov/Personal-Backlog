package nz.co.lazycoder.personalbacklog.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

class ShowHideFloatingActionButton {
    private final View mView;
    private final int mShow;
    private final int mHide;

    public ShowHideFloatingActionButton(View view) {
        mView = view;
        mShow = com.shamanland.fab.R.anim.floating_action_button_show;
        mHide = com.shamanland.fab.R.anim.floating_action_button_hide;
    }

    public void show() {
        if (mView.getVisibility() != View.VISIBLE) {
            mView.setVisibility(View.VISIBLE);
            animate(mShow);
        }
    }

    public void hide() {
        if (mView.getVisibility() == View.VISIBLE) {
            mView.setVisibility(View.GONE);
            animate(mHide);
        }
    }

    private void animate(int anim) {
        if (anim != 0) {
            Animation a = AnimationUtils.loadAnimation(mView.getContext(), anim);

            mView.startAnimation(a);
        }
    }
}
