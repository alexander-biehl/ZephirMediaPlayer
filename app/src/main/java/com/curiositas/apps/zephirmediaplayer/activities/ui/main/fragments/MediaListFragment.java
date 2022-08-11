package com.curiositas.apps.zephirmediaplayer.activities.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curiositas.apps.zephirmediaplayer.R;
import com.curiositas.apps.zephirmediaplayer.activities.MainActivity;
import com.curiositas.apps.zephirmediaplayer.activities.adapters.MediaListRecyclerViewAdapter;
import com.curiositas.apps.zephirmediaplayer.activities.ui.main.MusicQueueViewModel;

/**
 * A fragment representing a list of Items.
 */
public class MediaListFragment extends Fragment implements MediaListRecyclerViewAdapter.IFragmentBridge {

    private static final String TAG = MediaListFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
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
        //mediaItems = ((MainActivity)getActivity()).getMediaItems();
        viewModel = ((MainActivity) requireActivity()).viewModel;
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
            recyclerView.setOnClickListener(v -> {
                //MediaBrowserCompat.MediaItem item = adapter.
            });
            viewModel.getQueue().observe(requireActivity(), mediaItems -> {
                adapter.setMedia(mediaItems);
            });
        }
        return view;
    }

    @Override
    public void onMediaSelected(View view) {
        MediaListRecyclerViewAdapter.ViewHolder holder =
                (MediaListRecyclerViewAdapter.ViewHolder)recyclerView.findContainingViewHolder(view);
        MediaBrowserCompat.MediaItem item = holder.mItem;
        Log.d(TAG, "Retrieved Mediaitem: " + item.getMediaId() + ": " + item.toString());
    }
}