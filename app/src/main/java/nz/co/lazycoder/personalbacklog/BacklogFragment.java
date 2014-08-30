package nz.co.lazycoder.personalbacklog;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

import nz.co.lazycoder.personalbacklog.addItemDialog.AddItemDialog;
import nz.co.lazycoder.personalbacklog.model.BacklogListAdapter;
import nz.co.lazycoder.personalbacklog.model.DataModelController;
import nz.co.lazycoder.personalbacklog.model.InProgressListAdapter;
import nz.co.lazycoder.personalbacklog.model.ListItem;

public class BacklogFragment extends Fragment {

    private static final String TAG = BacklogFragment.class.getSimpleName();

    private final DataModelController dataModelController = new DataModelController();

    private ListView inProgressView;
    private ListView backlogView;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_personal_backlog, container, false);

        backlogView = (ListView) fragmentView.findViewById(R.id.backlog_list_view);
        inProgressView = (ListView) fragmentView.findViewById(R.id.in_progress_list_view);

        setupModelsAndListeners();


        View addItemButton = fragmentView.findViewById(R.id.add_row);
        addItemButton.setOnClickListener(new AddItemOnClickListener());

        return fragmentView;
    }

    private void setupModelsAndListeners() {
        backlogView.setAdapter(new BacklogListAdapter(dataModelController));
        backlogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dataModelController.moveItemFromBacklogToInProgress(position);
            }
        });


        inProgressView.setAdapter(new InProgressListAdapter(dataModelController));
        inProgressView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dataModelController.removeItemFromInProgress(position);
            }
        });
    }


    private class AddItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AddItemDialog addItemDialog = new AddItemDialog(
                    getActivity(),
                    new AddItemDialog.OnItemCreatedListener() {
                        @Override
                        public void onItemCreated(ListItem item) {
                            dataModelController.addItemToBacklog(item);
                        }
                    });
            addItemDialog.show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO: move this
        File saveFile = new File(getActivity().getExternalCacheDir(), "pbl.json");
        try {
            dataModelController.toDisk(saveFile);
        } catch (IOException ex) {
            Log.v(TAG, "Could not save to disk", ex);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: move this
        File saveFile = new File(getActivity().getExternalCacheDir(), "pbl.json");
        try {
            dataModelController.fromDisk(saveFile);
        } catch (IOException ex) {
            Log.v(TAG, "Could not load from disk", ex);
        }

        setupModelsAndListeners();
    }
}
