package nz.co.lazycoder.personalbacklog.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import nz.co.lazycoder.personalbacklog.R;

/**
 * Created by ktchernov on 16/08/2014.
 */
class ItemListAdapter extends BaseAdapter
{
    private ListItems items;

    ItemListAdapter(ListItems items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.getCount();
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
    public View getView(int position, View recycleView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (recycleView == null) {
            recycleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.backlog_item, parent, false);
            viewHolder = new ViewHolder(recycleView);
            recycleView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) recycleView.getTag();
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
