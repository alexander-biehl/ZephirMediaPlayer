package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.Constants;
import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.MainActivity;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.adapters.MediaListRecyclerViewAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main.MusicQueueViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentMediaListBinding;

/**
 * A fragment representing a list of Items.
 */
public class MediaListFragment extends Fragment implements MediaListRecyclerViewAdapter.IFragmentBridge {

    private static final String TAG = MediaListFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private MusicQueueViewModel viewModel;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MediaListFragment newInstance(int columnCount) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(MusicQueueViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            final MediaListRecyclerViewAdapter adapter = new MediaListRecyclerViewAdapter(this);
            recyclerView.setAdapter(adapter);

            viewModel.getQueue().observe(requireActivity(), adapter::setMedia);
        }
        return view;
    }

    @Override
    public void onMediaSelected(View view) {
        MediaListRecyclerViewAdapter.ViewHolder holder =
                (MediaListRecyclerViewAdapter.ViewHolder)recyclerView.findContainingViewHolder(view);
        MediaBrowserCompat.MediaItem item = holder.mItem;
        Log.d(TAG, "Retrieved Mediaitem: " + item.getMediaId() + ": " + item.toString());
        viewModel.setCurrentMediaID(item.getMediaId());
    }
}