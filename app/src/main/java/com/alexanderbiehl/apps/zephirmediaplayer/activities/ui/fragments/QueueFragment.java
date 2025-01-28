package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters.MyQueueRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class QueueFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private final List<MediaItem> currentQueue;
    private int mColumnCount = 1;

    // TODO's
    /*
    * add mediacontroller so we can display all the items in the current queue
    * add context menu so we can remove items from queue
    * */

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QueueFragment() {
        currentQueue = new ArrayList<>();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static QueueFragment newInstance(int columnCount) {
        QueueFragment fragment = new QueueFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyQueueRecyclerViewAdapter(currentQueue));
        }
        return view;
    }
}