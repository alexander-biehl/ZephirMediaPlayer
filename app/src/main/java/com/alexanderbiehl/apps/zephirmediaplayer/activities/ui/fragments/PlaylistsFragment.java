package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.fragments;

import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.PLAYLIST_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.domain.MediaItemUseCase.PLAYLIST_ID;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.InputType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexanderbiehl.apps.zephirmediaplayer.MainApp;
import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.PlaylistsViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel.MediaViewModel;
import com.alexanderbiehl.apps.zephirmediaplayer.common.OnClickHandler;
import com.alexanderbiehl.apps.zephirmediaplayer.databinding.FragmentPlaylistsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlaylistsFragment extends Fragment {

    private static final String TAG = PlaylistsFragment.class.getSimpleName();
    private FragmentPlaylistsBinding binding;
    private PlaylistsAdapter playlistsAdapter;
    private List<MediaItem> playlists;
    private MediaBrowser mediaBrowser;
    private ListenableFuture<MediaBrowser> browserFuture;
    private MediaViewModel mediaViewModel;
    private PlaylistsViewModel playlistsViewModel;
    private long lastHandledMessageId = -1L;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        this.playlistsViewModel = new ViewModelProvider(this).get(PlaylistsViewModel.class);
    }

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
        observePlaylistsState();
        playlistsViewModel.loadPlaylists();
        initializeBrowser();

        // Set FAB click listener to create a new playlist
        binding.fabCreatePlaylist.setOnClickListener(v ->
                createNewPlaylist()
        );
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
            playPlaylist(playlist);
            return true;
        } else if (id == R.id.action_rename_playlist) {
            showRenamePlaylistDialog(playlist);
            return true;
        } else if (id == R.id.action_delete_playlist) {
            showDeletePlaylistDialog(playlist);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onStop() {
        if (mediaBrowser != null) {
            mediaBrowser.release();
            mediaBrowser = null;
        }
        if (browserFuture != null) {
            MediaBrowser.releaseFuture(browserFuture);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeBrowser() {
        browserFuture = ((MainApp) requireActivity().getApplication())
                .getAppContainer()
                .getMediaConnectionFactory()
                .createBrowser(requireContext());
        browserFuture.addListener(() -> {
            if (browserFuture.isDone()) {
                try {
                    mediaBrowser = browserFuture.get();
                    observerViewModel();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting media browser: " + e.getMessage());
                    if (binding != null) {
                        Snackbar.make(binding.getRoot(), "Unable to connect to media service", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
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

    private void observePlaylistsState() {
        playlistsViewModel.getPlaylists().observe(getViewLifecycleOwner(), items -> {
            playlists.clear();
            if (items != null) {
                playlists.addAll(items);
            }
            playlistsAdapter.notifyDataSetChanged();
        });

        playlistsViewModel.getUiMessages().observe(getViewLifecycleOwner(), message -> {
            if (message == null || message.id == lastHandledMessageId || binding == null) {
                return;
            }
            lastHandledMessageId = message.id;
            String text = message.formatArg == null
                    ? getString(message.resId)
                    : getString(message.resId, message.formatArg);
            Snackbar.make(binding.getRoot(), text, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void observerViewModel() {
        mediaViewModel.getCurrentMedia().observe(getViewLifecycleOwner(), currentMedia -> {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Current media changed: " + currentMedia);
            }
            if (mediaBrowser != null) {
                if (currentMedia != null &&
                        currentMedia.mediaId.startsWith(PLAYLIST_PREFIX)) {
                    playPlaylist(currentMedia);
                    Log.d(TAG, "Current media: " + currentMedia.mediaMetadata.title);
                } else {
                    Log.d(TAG, "No current media");
                }
            }
        });
    }

    private void createNewPlaylist() {
        if (binding == null) {
            return;
        }

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setHint(R.string.playlist_name_hint);

        final AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.create_new_playlist)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_create, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String title = input.getText() == null ? "" : input.getText().toString().trim();
                    if (title.isEmpty()) {
                        input.setError(getString(R.string.playlist_name_required));
                        return;
                    }
                    createPlaylist(title, dialog);
                }));

        dialog.show();
    }

    private void createPlaylist(@NonNull String title, @NonNull AlertDialog dialog) {
        playlistsViewModel.createPlaylist(title);
        dialog.dismiss();
    }

    private void showRenamePlaylistDialog(@NonNull MediaItem playlistItem) {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        CharSequence currentTitle = playlistItem.mediaMetadata.title;
        input.setText(currentTitle == null ? "" : currentTitle.toString());
        input.setHint(R.string.playlist_name_hint);

        final AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_rename)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_save, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String newTitle = input.getText() == null ? "" : input.getText().toString().trim();
                    if (newTitle.isEmpty()) {
                        input.setError(getString(R.string.playlist_name_required));
                        return;
                    }
                    renamePlaylist(playlistItem.mediaId, newTitle, dialog);
                }));
        dialog.show();
    }

    private void renamePlaylist(
            @NonNull String mediaId,
            @NonNull String newTitle,
            @NonNull androidx.appcompat.app.AlertDialog dialog
    ) {
        playlistsViewModel.renamePlaylist(mediaId, newTitle);
        dialog.dismiss();
    }

    private void showDeletePlaylistDialog(@NonNull MediaItem playlistItem) {
        CharSequence title = playlistItem.mediaMetadata.title;
        String playlistTitle = title == null ? getString(R.string.playlists_fragment_title) : title.toString();
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_delete)
                .setMessage(getString(R.string.delete_playlist_confirmation, playlistTitle))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> deletePlaylist(playlistItem.mediaId, playlistTitle))
                .show();
    }

    private void deletePlaylist(@NonNull String mediaId, @NonNull String playlistTitle) {
        playlistsViewModel.deletePlaylist(mediaId, playlistTitle);
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
                            // add items to MediaViewModel queue for tracking in QueueFragment
                            mediaViewModel.setQueue(items);


                            Snackbar.make(binding.getRoot(),
                                    "Playing playlist: " + playlist.mediaMetadata.title,
                                    Snackbar.LENGTH_SHORT).show();
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_PlaylistsFragment_to_NowPlayingFragment);
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
            Integer numTracks = metadata.totalTrackCount;

            long durationMs = metadata.durationMs != null ? metadata.durationMs : 0L;
            long hours = durationMs / 1000 / 60 / 60;
            long minutes = (durationMs - (hours * 60 * 60 * 1000)) / 1000 / 60;

            if (hours == 0L) {
                holder.detailsTextView.setText(holder.itemView.getContext().getString(
                        R.string.playlist_details_format_minutes,
                        numTracks,
                        minutes
                ));
            } else {
                holder.detailsTextView.setText(holder.itemView.getContext().getString(
                        R.string.playlist_details_format,
                        numTracks,
                        hours,
                        minutes
                ));
            }

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