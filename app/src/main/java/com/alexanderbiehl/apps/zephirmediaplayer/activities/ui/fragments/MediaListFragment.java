package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import static com.alexanderbiehl.apps.zephirmediaplayer.Constants.MEDIA_KEY;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.SessionToken;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters.MediaListRecyclerViewAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MediaListFragment extends Fragment {

    private static final String TAG = MediaListFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters

    private ListenableFuture<MediaBrowser> browserFuture;
    private MediaBrowser mediaBrowser;
    private final List<MediaItem> subMediaList;
    private MediaViewModel mediaViewModel;
    private MediaListRecyclerViewAdapter mediaAdapter;
    private RecyclerView recyclerView;

    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaListFragment() {
        this.subMediaList = new ArrayList<>();
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
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeBrowser();
    }

    @Override
    public void onStop() {
        MediaBrowser.releaseFuture(browserFuture);
        if (this.mediaBrowser != null) {
            this.mediaBrowser.release();
            this.mediaBrowser = null;
        }
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mediaAdapter = new MediaListRecyclerViewAdapter(subMediaList, new MediaListViewClickHandler());
            recyclerView.setAdapter(mediaAdapter);
        }

        this.mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        this.mediaViewModel.getCurrentMedia().observe(requireActivity(), item -> {
            if (mediaBrowser != null) {
                if (Boolean.TRUE.equals(item.mediaMetadata.isBrowsable)) {
                    openSubFolder(item);
                } else if (Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
                    handlePlay(item);
                }
            }
        });

        return view;
    }

    private void initializeBrowser() {
        SessionToken sessionToken =
                new SessionToken(
                        requireContext(),
                        new ComponentName(
                                requireActivity(),
                                Media3Service.class
                        )
                );
        browserFuture =
                new MediaBrowser.Builder(requireActivity(), sessionToken).buildAsync();
        browserFuture.addListener(() -> {
            if (browserFuture.isDone()) {
                try {
                    mediaBrowser = browserFuture.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void handlePlay(MediaItem item) {
        int position = subMediaList.indexOf(item);
        List<MediaItem> playQueue = subMediaList.subList(position, subMediaList.size());
        mediaBrowser.addMediaItems(playQueue);
        mediaBrowser.prepare();
        mediaBrowser.play();
        mediaViewModel.setQueue(playQueue);
    }

    private void openSubFolder(MediaItem item) {
        ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture =
                mediaBrowser.getChildren(
                        item.mediaId,
                        0,
                        Integer.MAX_VALUE,
                        null
                );
        childrenFuture.addListener(() -> {
            try {
                subMediaList.clear();
                subMediaList.addAll(childrenFuture.get().value);
                mediaAdapter.notifyDataSetChanged();
                Log.d(TAG, "Got media list of " + subMediaList.size() + " items.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private class MediaListViewClickHandler implements MediaListRecyclerViewAdapter.OnClickHandler {

        // TODO implement a longClick callback to handle adding additional items to queue
        // instead of drilling down

        @Override
        public void onClick(int position, MediaItem item) {
            MediaItem selectedItem = subMediaList.get(position);
            // Bundle bundle = new Bundle();
            // bundle.putString(MEDIA_KEY, selectedItem.mediaId);
            mediaViewModel.setCurrentMedia(selectedItem);
//            NavHostFragment.findNavController(MediaListFragment.this)
//                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        }
    }
}