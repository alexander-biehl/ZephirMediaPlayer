package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;


import static com.alexanderbiehl.apps.zephirmediaplayer.domain.MediaItemUseCase.PLAYLIST_ID;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.MainApp;
import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.adapters.MediaListRecyclerViewAdapter;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.common.OnClickHandler;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers.MediaBrowserWrapper;
import com.alexanderbiehl.apps.zephirmediaplayer.common.wrappers.MediaBrowserWrapperImpl;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.AddMediaItemsToPlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.GetPlaylistsUseCase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A fragment representing a list of Items.
 */
public class MediaListFragment extends Fragment {

    private static final String TAG = MediaListFragment.class.getSimpleName();


    private static final String ARG_COLUMN_COUNT = "column-count";
    public final Stack<MediaItem> treeBackStack;
    private final List<MediaItem> subMediaList;
    public MediaBrowserWrapper mediaBrowser;
    public MediaViewModel mediaViewModel;
    public FloatingActionButton fab;
    private ListenableFuture<MediaBrowser> browserFuture;
    private MediaListRecyclerViewAdapter mediaAdapter;
    private int mColumnCount = 1;
    private boolean firstCurrentMediaEmission = true;


    public MediaListFragment() {
        this.subMediaList = new ArrayList<>();
        this.treeBackStack = new Stack<>();
    }

    // for testing
    public MediaListFragment(MediaViewModel viewModel, MediaBrowserWrapper mediaBrowser) {
        this.mediaViewModel = viewModel;
        this.mediaBrowser = mediaBrowser;
        this.subMediaList = new ArrayList<>();
        this.treeBackStack = new Stack<>();
    }


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
        if (browserFuture != null) {
            MediaBrowser.releaseFuture(browserFuture);
        }
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

        RecyclerView recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            Context context = view.getContext();

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Add dividers between items
            DividerItemDecoration itemDecoration = new DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
            );
            recyclerView.addItemDecoration(itemDecoration);

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
        firstCurrentMediaEmission = true;

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                popPathStack();
            }
        });

        fab = view.findViewById(R.id.toNowPlaying);
        // set FAB to navigate to NowPlayingFragment
        fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    Log.d(TAG, "Back clicked!");
                    popPathStack();
                    // make sure we return true to indicate that the action was successful
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();

        if (v instanceof RecyclerView) {
            MediaListRecyclerViewAdapter adapter = (MediaListRecyclerViewAdapter) ((RecyclerView) v).getAdapter();
            if (adapter == null) {
                Log.d(TAG, "Adapter was null");
                return;
            }
            MediaItem item = adapter.getContextMenuItem();

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Item: " + item.toString());
            }
            if (Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
                inflater.inflate(R.menu.menu_media_item, menu);
            }
        }
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
        browserFuture = ((MainApp) requireActivity().getApplication())
                .getAppContainer()
                .getMediaConnectionFactory()
                .createBrowser(requireContext());
        browserFuture.addListener(() -> {
            if (browserFuture.isDone()) {
                try {
                    mediaBrowser = new MediaBrowserWrapperImpl(browserFuture.get());
                    observeViewModel();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to initialize media browser", e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Unable to connect to media service", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void observeViewModel() {
        this.mediaViewModel.getCurrentMedia().observe(getViewLifecycleOwner(), item -> {
            if (mediaBrowser == null || item == null) {
                return;
            }

            // Ignore stale playlist-folder state when this observer re-attaches after back navigation.
            if (firstCurrentMediaEmission) {
                firstCurrentMediaEmission = false;
                if (PLAYLIST_ID.equals(item.mediaId)) {
                    return;
                }
            }

            if (item.mediaId.equals(PLAYLIST_ID)) {
                // Guard against duplicate/late emissions navigating form the wrong destination.
                final var navController = NavHostFragment.findNavController(this);
                final var currentDestination = navController.getCurrentDestination();
                if (currentDestination != null &&
                        currentDestination.getId() == R.id.MediaListFragment &&
                        currentDestination.getAction(R.id.action_mediaList_toPlaylists) != null) {
                    navController.navigate(R.id.action_mediaList_toPlaylists);
                } else {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Not navigating to playlists because current destination is " +
                                (currentDestination != null ? currentDestination.getId() : "null") +
                                " and action is " +
                                (currentDestination != null ? currentDestination.getAction(R.id.action_mediaList_toPlaylists) : "null"));
                    }
                }
            } else if (Boolean.TRUE.equals(item.mediaMetadata.isBrowsable)) {
                pushPathStack(item);
            } else if (Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
                handlePlay(item);
            }
        });
    }

    public void handlePlay(MediaItem item) {
        int position = subMediaList.indexOf(item);
        List<MediaItem> playQueue = new ArrayList<>(subMediaList.subList(position, subMediaList.size()));
        mediaBrowser.addMediaItems(playQueue);
        mediaBrowser.prepare();
        mediaBrowser.play();
        mediaViewModel.setQueue(playQueue);
        // navigate to Now Playing
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    public void openSubFolder(MediaItem item) {
        ActionBar supportActionBar =
                ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(item.mediaMetadata.title);
        }

        ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture =
                mediaBrowser.getChildren(
                        item.mediaId,
                        0,
                        Integer.MAX_VALUE,
                        null
                );
        childrenFuture.addListener(() -> {
            try {
                if (childrenFuture.isDone()) {
                    // TODO we need to check if the service returns an error here
                    subMediaList.clear();
                    List<MediaItem> items = childrenFuture.get().value;
                    // prevent NPE if the service returns null
                    if (items == null || items.isEmpty()) {
                        Log.d(TAG, "No items found in this folder");
                        Toast.makeText(requireContext(), "Folder is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    subMediaList.addAll(items);
                    subMediaList.sort(this::sortMediaItems);
                    // mediaAdapter.notifyItemRangeChanged(0, items.size());
                    // keep this for now since itemRangeChanged causes an exception
                    mediaAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Got media list of " + subMediaList.size() + " items.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load folder children", e);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Unable to open folder", Toast.LENGTH_SHORT).show();
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    public void addChildItemsToQueue(@NonNull MediaItem parent) {
        ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture =
                mediaBrowser.getChildren(
                        parent.mediaId,
                        0,
                        Integer.MAX_VALUE,
                        null
                );
        childrenFuture.addListener(() -> {
            if (childrenFuture.isDone()) {
                try {
                    List<MediaItem> queuedItems = childrenFuture.get().value;
                    if (queuedItems != null && !queuedItems.isEmpty()) {
                        List<MediaItem> sortedItems = new ArrayList<>(queuedItems);
                        sortedItems.sort(this::sortMediaItems);
                        mediaBrowser.addMediaItems(mediaBrowser.getMediaItemCount(), sortedItems);
                        mediaViewModel.addToQueue(sortedItems);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Failed to add child items to queue", e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Unable to add items to queue", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    public void addMediaItemToQueue(@NonNull MediaItem item) {
        // if the item is Browsable AND Playable (i.e. it is an album / playlist)
        if (Boolean.TRUE.equals(item.mediaMetadata.isBrowsable) &&
                Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
            Log.d(TAG, "Item is browsable and playable, adding child items to queue");
            addChildItemsToQueue(item);
        } else if (Boolean.TRUE.equals(item.mediaMetadata.isPlayable)) {
            Log.d(TAG, "Item is playable, adding to queue");
            if (mediaBrowser != null) {
                int mediaItemCount = mediaBrowser.getMediaItemCount();
                mediaBrowser.addMediaItem(
                        mediaItemCount,
                        item
                );
                mediaViewModel.addToQueue(item);
            }
        }
    }

    public void addMediaItemToPlaylist(@NonNull MediaItem item) {
        if (mediaBrowser == null) {
            Toast.makeText(requireContext(), "Unable to add to playlist right now", Toast.LENGTH_SHORT).show();
            return;
        }

        resolvePlaylistMediaItems(item, mediaItems -> loadPlaylistsAndShowPicker(mediaItems));
    }

    private void resolvePlaylistMediaItems(@NonNull MediaItem sourceItem, @NonNull Consumer<List<MediaItem>> onResolved) {
        if (Boolean.TRUE.equals(sourceItem.mediaMetadata.isBrowsable) &&
                Boolean.TRUE.equals(sourceItem.mediaMetadata.isPlayable)) {
            ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture =
                    mediaBrowser.getChildren(sourceItem.mediaId, 0, Integer.MAX_VALUE, null);
            childrenFuture.addListener(() -> {
                try {
                    if (!childrenFuture.isDone()) {
                        return;
                    }
                    List<MediaItem> items = childrenFuture.get().value;
                    onResolved.accept(items == null ? Collections.emptyList() : new ArrayList<>(items));
                } catch (Exception e) {
                    Log.e(TAG, "Unable to resolve playlist items", e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Unable to load items to add", Toast.LENGTH_SHORT).show();
                    }
                    onResolved.accept(Collections.emptyList());
                }
            }, ContextCompat.getMainExecutor(requireActivity()));
        } else {
            onResolved.accept(Collections.singletonList(sourceItem));
        }
    }

    private void loadPlaylistsAndShowPicker(@NonNull List<MediaItem> mediaItems) {
        MainApp app = (MainApp) requireActivity().getApplication();
        GetPlaylistsUseCase getPlaylistsUseCase = app.getAppContainer().getGetPlaylistsUseCase();
        AddMediaItemsToPlaylistUseCase addMediaItemsToPlaylistUseCase =
                app.getAppContainer().getAddMediaItemsToPlaylistUseCase();

        getPlaylistsUseCase.execute(result -> requireActivity().runOnUiThread(() -> {
            if (!(result instanceof Result.Success<?> success) || success.data == null) {
                Toast.makeText(requireContext(), getString(R.string.no_playlists_available), Toast.LENGTH_SHORT).show();
                return;
            }

            @SuppressWarnings("unchecked")
            List<MediaItem> playlistItems = (List<MediaItem>) success.data;
            List<PlaylistEntity> playlists = playlistItems.stream().map(item -> {
                PlaylistEntity entity = new PlaylistEntity();
                entity.mediaId = item.mediaId;
                entity.title = item.mediaMetadata.title == null ? "" : item.mediaMetadata.title.toString();
                return entity;
            }).collect(Collectors.toList());
            if (playlists.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_playlists_available), Toast.LENGTH_SHORT).show();
                return;
            }

            showPlaylistPickerDialog(addMediaItemsToPlaylistUseCase, playlists, mediaItems);
        }));
    }

    private void showPlaylistPickerDialog(
            @NonNull AddMediaItemsToPlaylistUseCase addMediaItemsToPlaylistUseCase,
            @NonNull List<PlaylistEntity> playlists,
            @NonNull List<MediaItem> mediaItems
    ) {
        CharSequence[] playlistTitles = playlists.stream()
                .map(playlist -> playlist.title)
                .toArray(CharSequence[]::new);
        final int[] selectedIndex = {0};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_playlist)
                .setSingleChoiceItems(playlistTitles, 0, (dialog, which) -> selectedIndex[0] = which)
                .setPositiveButton(R.string.action_add, (dialog, which) -> {
                    PlaylistEntity selectedPlaylist = playlists.get(selectedIndex[0]);
                    addItemsToPlaylist(addMediaItemsToPlaylistUseCase, selectedPlaylist, mediaItems);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void addItemsToPlaylist(
            @NonNull AddMediaItemsToPlaylistUseCase addMediaItemsToPlaylistUseCase,
            @NonNull PlaylistEntity selectedPlaylist,
            @NonNull List<MediaItem> mediaItems
    ) {
        addMediaItemsToPlaylistUseCase.execute(selectedPlaylist, mediaItems, result ->
                requireActivity().runOnUiThread(() -> {
                    if (result instanceof Result.Success<?>) {
                        Toast.makeText(requireContext(),
                                getString(R.string.added_to_playlist, selectedPlaylist.title),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                getString(R.string.failed_to_add_to_playlist),
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void popPathStack() {
        if (treeBackStack.isEmpty()) {
            requireActivity().finish();
            return;
        }
        treeBackStack.pop();
        if (treeBackStack.isEmpty()) {
            requireActivity().finish();
            return;
        }
        openSubFolder(treeBackStack.peek());
    }

    public void pushPathStack(final MediaItem item) {
        if (treeBackStack.isEmpty() ||
                !treeBackStack.peek().mediaId.equals(item.mediaId)) {
            treeBackStack.push(item);
        }
        openSubFolder(item);
    }

    public int sortMediaItems(MediaItem a, MediaItem b) {
        if (a.mediaMetadata.trackNumber != null && b.mediaMetadata.trackNumber != null) {
            return a.mediaMetadata.trackNumber - b.mediaMetadata.trackNumber;
        } else {
            return a.mediaMetadata.title.toString().compareTo(
                    b.mediaMetadata.title.toString());
        }
    }

    public class MediaListViewClickHandler implements OnClickHandler {

        @Override
        public void onClick(int position, MediaItem item) {
            MediaItem selectedItem = subMediaList.get(position);

            // if we clicked a playable but not browsable MediaItem (i.e. a song), directly play it
            // instead of adding to viewmodel, otherwise when back is clicked in NowPlayingFragment
            // it'll just jump back to the playable
            // MediaItem and then back to NowPlaying
            if (Boolean.TRUE.equals(selectedItem.mediaMetadata.isPlayable) &&
                    Boolean.FALSE.equals(selectedItem.mediaMetadata.isBrowsable)) {
                handlePlay(item);
            } else {
                mediaViewModel.setCurrentMedia(selectedItem);
            }
        }
    }
}