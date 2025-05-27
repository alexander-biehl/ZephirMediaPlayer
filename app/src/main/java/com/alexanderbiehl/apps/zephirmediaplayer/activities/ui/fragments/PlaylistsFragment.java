package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.common.OnClickHandler;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentPlaylistsBinding;
import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsFragment extends Fragment {

    private static final String TAG = PlaylistsFragment.class.getSimpleName();
    private FragmentPlaylistsBinding binding;
    private PlaylistsAdapter playlistsAdapter;
    private List<MediaItem> playlists;
    private MediaBrowser mediaBrowser;
    private ListenableFuture<MediaBrowser> browserFuture;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize playlists list
        playlists = new ArrayList<>();

        setupRecyclerView();
        setupOptionsMenu();
        initializeBrowser();

        // Set FAB click listener to create a new playlist
        binding.fabCreatePlaylist.setOnClickListener(v ->
                createNewPlaylist()
        );
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
                    loadPlaylists();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting media browser: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void loadPlaylists() {
        // In a real implementation, you would load actual playlists from your media service
        // For demonstration, I'll create dummy playlists similar to your app's approach

        playlists.clear();
        for (int i = 0; i < 5; i++) {
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .setTitle("Playlist " + (i + 1))
                    .setArtist(i + 10 + " songs")
                    .setAlbumTitle("Total: " + (i * 10 + 30) + " min")
                    .setIsBrowsable(true)
                    .setIsPlayable(true)
                    .build();

            MediaItem playlist = new MediaItem.Builder()
                    .setMediaId("playlist_" + i)
                    .setMediaMetadata(metadata)
                    .build();

            playlists.add(playlist);
        }

        playlistsAdapter.notifyDataSetChanged();
    }

    private void setupOptionsMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_playlists, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.action_sort_playlists) {
                    // TODO: Show sort options dialog
                    Snackbar.make(binding.getRoot(), "Sort playlists", Snackbar.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_import_playlist) {
                    // TODO: Import playlist functionality
                    Snackbar.make(binding.getRoot(), "Import playlist", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.playlistsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add dividers between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Create and set the adapter
        playlistsAdapter = new PlaylistsAdapter(playlists, new PlaylistClickHandler());
        recyclerView.setAdapter(playlistsAdapter);

        // Register for context menu
        registerForContextMenu(recyclerView);
    }

    private void createNewPlaylist() {
        // TODO: Show dialog to create a new playlist
        Snackbar.make(binding.getRoot(), "Create new playlist", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = requireActivity().getMenuInflater();

        if (v instanceof RecyclerView) {
            PlaylistsAdapter adapter = (PlaylistsAdapter) ((RecyclerView) v).getAdapter();
            if (adapter == null) {
                Log.d(TAG, "Adapter was null");
                return;
            }
            MediaItem item = adapter.getContextMenuItem();

            if (item == null) {
                return;
            }

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Item: " + item);
            }

            inflater.inflate(R.menu.menu_playlists_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        MediaItem playlist = playlistsAdapter.getContextMenuItem();

        if (playlist == null) {
            return super.onContextItemSelected(item);
        }

        if (id == R.id.action_play_playlist) {
            // TODO: Play the selected playlist
            playPlaylist(playlist);
            return true;
        } else if (id == R.id.action_rename_playlist) {
            // TODO: Rename the selected playlist
            Snackbar.make(binding.getRoot(), "Rename playlist: " + playlist.mediaMetadata.title,
                    Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_delete_playlist) {
            // TODO: Delete the selected playlist
            Snackbar.make(binding.getRoot(), "Delete playlist: " + playlist.mediaMetadata.title,
                    Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void playPlaylist(MediaItem playlist) {
        if (mediaBrowser != null) {
            // This is similar to the implementation in your MediaListFragment
            ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture =
                    mediaBrowser.getChildren(
                            playlist.mediaId,
                            0,
                            Integer.MAX_VALUE,
                            null
                    );

            childrenFuture.addListener(() -> {
                try {
                    if (childrenFuture.isDone()) {
                        List<MediaItem> items = childrenFuture.get().value;
                        if (items != null && !items.isEmpty()) {
                            // Add items to queue
                            mediaBrowser.clearMediaItems();
                            mediaBrowser.addMediaItems(items);
                            mediaBrowser.prepare();
                            mediaBrowser.play();

                            Snackbar.make(binding.getRoot(),
                                    "Playing playlist: " + playlist.mediaMetadata.title,
                                    Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(binding.getRoot(), "Playlist is empty",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error playing playlist: " + e.getMessage());
                }
            }, ContextCompat.getMainExecutor(requireActivity()));
        }
    }

    @Override
    public void onStop() {
        if (mediaBrowser != null) {
            mediaBrowser.release();
            mediaBrowser = null;
        }
        MediaBrowser.releaseFuture(browserFuture);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Adapter for displaying playlists in the RecyclerView
     */
    private static class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {

        private final List<MediaItem> playlists;
        private final OnClickHandler clickHandler;
        private MediaItem contextMenuItem;

        PlaylistsAdapter(List<MediaItem> playlists, OnClickHandler clickHandler) {
            this.playlists = playlists;
            this.clickHandler = clickHandler;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_playlist, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MediaItem playlist = playlists.get(position);
            MediaMetadata metadata = playlist.mediaMetadata;

            holder.nameTextView.setText(metadata.title);
            holder.detailsTextView.setText(metadata.artist + " · " + metadata.albumTitle);

            holder.itemView.setOnClickListener(v -> {
                if (clickHandler != null) {
                    clickHandler.onClick(position, playlist);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                contextMenuItem = playlist;
                return false;  // Allow normal context menu to appear
            });
        }

        @Override
        public int getItemCount() {
            return playlists.size();
        }

        public MediaItem getContextMenuItem() {
            return contextMenuItem;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView iconView;
            final TextView nameTextView;
            final TextView detailsTextView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                iconView = itemView.findViewById(R.id.playlist_icon);
                nameTextView = itemView.findViewById(R.id.playlist_name);
                detailsTextView = itemView.findViewById(R.id.playlist_details);
            }
        }
    }

    /**
     * Implementation of OnClickHandler for playlist item clicks
     */
    private class PlaylistClickHandler implements OnClickHandler {

        @Override
        public void onClick(int position, MediaItem item) {
            MediaItem selectedPlaylist = playlists.get(position);
            // Open the playlist contents or play it directly
            playPlaylist(selectedPlaylist);
        }
    }
}