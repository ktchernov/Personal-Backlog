package nz.co.lazycoder.personalbacklog.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.model.ListItem;
import nz.co.lazycoder.personalbacklog.model.ListItems;

/**
* Created by ktchernov on 16/08/2014.
*/
class ItemListAdapter extends BaseAdapter
{
    private ListItems items;

    ItemListAdapter(ListItems items) {
        this.items = items;
    }

    public static int getDragHandleId() {
        return R.id.drag_handle;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View recycleView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (recycleView == null) {
            final View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.backlog_item, parent, false);
            viewHolder = new ViewHolder(cellView);
            cellView.setTag(viewHolder);

            recycleView = cellView;
        }
        else {
            viewHolder = (ViewHolder) recycleView.getTag();
        }

        if (position == items.getSelectedItemIndex()) {
            recycleView.setBackgroundColor(parent.getResources().getColor(android.R.color.holo_blue_light));
        }
        else {
            recycleView.setBackgroundColor(Color.TRANSPARENT);
        }
        ListItem listItem = items.getItem(position);
        viewHolder.textView.setText(listItem.getTitle());

        return recycleView;
    }

    class ViewHolder {

        public TextView textView;

        public ViewHolder(View itemView) {
            textView = (TextView) itemView.findViewById(R.id.backlog_text_view);
        }
    }
}
