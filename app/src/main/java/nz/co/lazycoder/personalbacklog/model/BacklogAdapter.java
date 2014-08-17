package nz.co.lazycoder.personalbacklog.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import nz.co.lazycoder.personalbacklog.BacklogItem;
import nz.co.lazycoder.personalbacklog.R;

/**
 * Created by ktchernov on 16/08/2014.
 */
public class BacklogAdapter extends BaseAdapter
{
    private List<BacklogItem> items;

    public BacklogAdapter(List<BacklogItem> items) {
        this.items = new LinkedList<BacklogItem>(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View recycleView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (recycleView == null) {
            recycleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.backlog_item, parent, false);
            viewHolder = new ViewHolder(recycleView);
            recycleView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) recycleView.getTag();
        }
        BacklogItem backlogItem = items.get(index);
        viewHolder.textView.setText(backlogItem.getTitle());

        return recycleView;
    }

    public BacklogItem removeItem(int position) {
        BacklogItem removedItem = items.remove(position);
        notifyDataSetChanged();
        return removedItem;
    }

    public void addItem(BacklogItem backlogItem) {
        items.add(0, backlogItem);
        notifyDataSetChanged();
    }

    class ViewHolder {

        public TextView textView;

        public ViewHolder(View itemView) {
            textView = (TextView) itemView.findViewById(R.id.backlog_text_view);
        }
    }
}
