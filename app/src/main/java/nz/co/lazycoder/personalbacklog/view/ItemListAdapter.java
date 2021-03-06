package nz.co.lazycoder.personalbacklog.view;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import nz.co.lazycoder.personalbacklog.R;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItem;
import nz.co.lazycoder.personalbacklog.model.listitems.ListItems;

public class ItemListAdapter extends BaseAdapter
{
    public interface OnMenuItemClickListener {
        void onMenuItemClick(int listItemPosition, int menuItemId);
    }

    private ListItems items;
    private int menuResourceId;
    private OnMenuItemClickListener menuItemClickListener;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            final View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.backlog_item, parent, false);
            viewHolder = new ViewHolder(cellView, position);
            cellView.setTag(viewHolder);

            convertView = cellView;
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemPosition = position;

        ListItem listItem = items.getItem(position);
        viewHolder.textView.setText(listItem.getTitle());

        return convertView;
    }

    public void setOptionsMenu(int menuResourceId, OnMenuItemClickListener menuItemClickListener) {
        this.menuResourceId = menuResourceId;
        this.menuItemClickListener = menuItemClickListener;
    }

    private class ViewHolder {

        public TextView textView;
        public View optionsHandle;
        public int itemPosition;

        public ViewHolder(final View itemView, final int itemPosition) {
            this.itemPosition = itemPosition;
            textView = (TextView) itemView.findViewById(R.id.backlog_text_view);
            optionsHandle = itemView.findViewById(R.id.options_handle);
            optionsHandle.setTag(this);

            if (menuItemClickListener == null) {
                optionsHandle.setVisibility(View.GONE);
            }
            else {
                optionsHandle.setOnClickListener(OPTIONS_MENU_ON_CLICK_LISTENER);
            }
        }

    }

    private final OptionsMenuOnClickListener OPTIONS_MENU_ON_CLICK_LISTENER = new OptionsMenuOnClickListener();

    private class OptionsMenuOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View itemView) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ViewHolder viewHolder = (ViewHolder) (itemView.getTag());
                    menuItemClickListener.onMenuItemClick(viewHolder.itemPosition, menuItem.getItemId());
                    return true;
                }
            });
            popupMenu.inflate(menuResourceId);
            popupMenu.show();
        }
    }
}
