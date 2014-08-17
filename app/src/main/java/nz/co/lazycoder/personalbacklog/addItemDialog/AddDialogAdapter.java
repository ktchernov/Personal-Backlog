package nz.co.lazycoder.personalbacklog.addItemDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import nz.co.lazycoder.personalbacklog.R;

/**
 * Created by ktchernov on 17/08/2014.
 */
public class AddDialogAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View reuseView, ViewGroup parent) {
        if (reuseView == null) {
            reuseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item_dialog, parent, false);

            final View view = reuseView.findViewById(R.id.title_input_view);
            reuseView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    view.requestFocus();
                }
            });
        }
        return reuseView;
    }
}
