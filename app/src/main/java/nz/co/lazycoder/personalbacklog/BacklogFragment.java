package nz.co.lazycoder.personalbacklog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import nz.co.lazycoder.personalbacklog.addItemDialog.AddItemDialog;
import nz.co.lazycoder.personalbacklog.model.BacklogAdapter;

public class BacklogFragment extends Fragment {

    private ListView inProgressView;
    private BacklogAdapter inProgressAdapter;
    private ListView backlogView;
    private BacklogAdapter backlogAdapter;

    public BacklogFragment() {
        populateSampleData();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_personal_backlog, container, false);

        backlogView = (ListView) fragmentView.findViewById(R.id.backlog_list_view);

        backlogView.setAdapter(backlogAdapter);
        backlogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                inProgressAdapter.addItem(backlogAdapter.removeItem(position));
            }
        });


        inProgressView = (ListView) fragmentView.findViewById(R.id.in_progress_list_view);
        inProgressView.setAdapter(inProgressAdapter);
        inProgressView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                backlogAdapter.addItem(inProgressAdapter.removeItem(position));
            }
        });


        View addItemButton = fragmentView.findViewById(R.id.add_row);
        addItemButton.setOnClickListener(new AddItemOnClickListener());

        return fragmentView;
    }

    private void populateSampleData() {
        final int numItems = 20;
        ArrayList<BacklogItem> backlogItemArrayList = new ArrayList<BacklogItem>(numItems);
        for (int i = 0; i < numItems; i++) {
            backlogItemArrayList.add(new BacklogItem("Sample item " + i));
        }

        backlogAdapter = new BacklogAdapter(backlogItemArrayList);

        inProgressAdapter = new BacklogAdapter(Collections.<BacklogItem>emptyList());

    }

    private class AddItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AddItemDialog addItemDialog = new AddItemDialog(
                    getActivity(),
                    new AddItemDialog.OnItemCreatedListener() {
                        @Override
                        public void onItemCreated(BacklogItem item) {
                            backlogAdapter.addItem(item);
                        }
                    });
            addItemDialog.show();
        }

    }
}
