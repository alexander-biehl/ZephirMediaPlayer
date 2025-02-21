package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * A fragment representing a list of Items.
 */
public class MediaListFragment extends Fragment {

    private static final String TAG = MediaListFragment.class.getSimpleName();


    private static final String ARG_COLUMN_COUNT = "column-count";
    private final List<MediaItem> subMediaList;
    private final Stack<MediaItem> treeBackStack;
    private ListenableFuture<MediaBrowser> browserFuture;
    private MediaBrowser mediaBrowser;
    private MediaViewModel mediaViewModel;
    private MediaListRecyclerViewAdapter mediaAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private int mColumnCount = 1;

    // TODO's
    /*
     * add a context menu to each list item so that we can add to queue
     *
     * */

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaListFragment() {
        this.subMediaList = new ArrayList<>();
        this.treeBackStack = new Stack<>();
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
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                popPathStack();
            }
        });
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // override the default Menu back button to instead pop the stack of MediaItems
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Back clicked!");
            popPathStack();
            // make sure we return true to indicate that the action was successful
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            Context context = view.getContext();

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mediaAdapter = new MediaListRecyclerViewAdapter(subMediaList, new MediaListViewClickHandler());
            recyclerView.setAdapter(mediaAdapter);
            registerForContextMenu(recyclerView);
        }

        this.mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.toNowPlaying);
        // set FAB to navigate to NowPlayingFragment
        fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        // TODO check if browsable & playable, display options accordingly
        if (v instanceof RecyclerView) {
            MediaItem item = ((MediaListRecyclerViewAdapter) ((RecyclerView) v).getAdapter()).getContextMenuItem();
            Log.d(TAG, "Item: " + item.toString());
        }
        inflater.inflate(R.menu.menu_media_item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_queue:
                Log.d(TAG, "Add to queue clicked!");
                addMediaItemToQueue(mediaAdapter.getContextMenuItem());
                return true;
            case R.id.add_to_playlist:
                Log.d(TAG, "Add to playlistEntity clicked!");
                addMediaItemToPlaylist(mediaAdapter.getContextMenuItem());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
                    observeViewModel();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void observeViewModel() {
        this.mediaViewModel.getCurrentMedia().observe(requireActivity(), item -> {
            if (mediaBrowser != null) {
                if (Boolean.TRUE.equals(item.mediaMetadata.isBrowsable)) {
                    pushPathStack(item);
                } else if (Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
                    handlePlay(item);
                }
            }
        });
    }

    private void handlePlay(MediaItem item) {
        int position = subMediaList.indexOf(item);
        List<MediaItem> playQueue = subMediaList.subList(position, subMediaList.size());
        mediaBrowser.addMediaItems(playQueue);
        mediaBrowser.prepare();
        mediaBrowser.play();
        mediaViewModel.setQueue(playQueue);
        // navigate to Now Playing
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
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
                subMediaList.sort((a, b) -> {
                    if (a.mediaMetadata.trackNumber != null && b.mediaMetadata.trackNumber != null) {
                        return a.mediaMetadata.trackNumber - b.mediaMetadata.trackNumber;
                    } else {
                        return a.mediaMetadata.title.toString().compareTo(
                                b.mediaMetadata.title.toString());
                    }
                });
                mediaAdapter.notifyDataSetChanged();
                Log.d(TAG, "Got media list of " + subMediaList.size() + " items.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void addChildItemsToQueue(@NonNull MediaItem parent) {
        ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture =
                mediaBrowser.getChildren(
                        parent.mediaId,
                        0,
                        Integer.MAX_VALUE,
                        null
                );
        childrenFuture.addListener(() -> {
            // TODO how do we handle issue when a parent of a parent is clicked?
            // could recursively get all the children and add them to the final queue?

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void addMediaItemToQueue(@NonNull MediaItem item) {
        if (Boolean.TRUE.equals(item.mediaMetadata.isBrowsable)) {
            Log.d(TAG, "Item is browsable, adding child items to queue");
            addChildItemsToQueue(item);
        } else if (Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
            Log.d(TAG, "Item is playable, adding to queue");
            if (mediaBrowser != null) {
                int mediaItemCount = mediaBrowser.getMediaItemCount();
                int position = mediaItemCount != 0 ? mediaItemCount - 1 : 0;
                mediaBrowser.addMediaItem(
                        position,
                        item
                );
            }
        }
    }

    private void addMediaItemToPlaylist(@NonNull MediaItem item) {
        //TODO need to open UI to select which playlist to add to
        // perhaps add a new fragment for the playlists, have it
        // return its result (the selected playlist) and then populate
    }

    private void popPathStack() {
        treeBackStack.pop();
        if (treeBackStack.isEmpty()) {
            requireActivity().finish();
            return;
        }
        openSubFolder(treeBackStack.peek());
    }

    private void pushPathStack(final MediaItem item) {
        treeBackStack.push(item);
        openSubFolder(item);
    }

    private class MediaListViewClickHandler implements MediaListRecyclerViewAdapter.OnClickHandler {

        @Override
        public void onClick(int position, MediaItem item) {
            MediaItem selectedItem = subMediaList.get(position);
            mediaViewModel.setCurrentMedia(selectedItem);
        }
    }
}