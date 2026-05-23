package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.R;
import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.CreatePlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.DeletePlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.GetPlaylistsUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.PlaylistRepositoryGateway;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.RenamePlaylistUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsViewModelTests {

    @Rule
    public final TestRule archTaskExecutorRule = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
                @Override
                public void executeOnDiskIO(@NonNull Runnable runnable) {
                    runnable.run();
                }

                @Override
                public void postToMainThread(@NonNull Runnable runnable) {
                    runnable.run();
                }

                @Override
                public boolean isMainThread() {
                    return true;
                }
            });
        }

        @Override
        protected void finished(Description description) {
            ArchTaskExecutor.getInstance().setDelegate(null);
        }
    };

    private FakePlaylistGateway gateway;
    private PlaylistsViewModel viewModel;

    @Before
    public void setUp() {
        gateway = new FakePlaylistGateway();
        Application application = new Application();
        viewModel = new PlaylistsViewModel(
                application,
                new GetPlaylistsUseCase(gateway),
                new CreatePlaylistUseCase(gateway),
                new RenamePlaylistUseCase(gateway),
                new DeletePlaylistUseCase(gateway)
        );
    }

    @Test
    public void loadPlaylists_populatesLiveData() {
        gateway.addPlaylist("playlist-1", "Road Trip");

        viewModel.loadPlaylists();

        assertNotNull(viewModel.getPlaylists().getValue());
        assertEquals(1, viewModel.getPlaylists().getValue().size());
        assertEquals("Road Trip", String.valueOf(viewModel.getPlaylists().getValue().get(0).mediaMetadata.title));
    }

    @Test
    public void createPlaylist_successRefreshesListAndPostsMessage() {
        gateway.addPlaylist("playlist-1", "Road Trip");

        viewModel.createPlaylist("  New Playlist  ");

        assertNotNull(viewModel.getPlaylists().getValue());
        assertEquals(2, viewModel.getPlaylists().getValue().size());
        assertEquals("New Playlist", gateway.lastCreatedPlaylist.title);
        assertNotNull(viewModel.getUiMessages().getValue());
        assertEquals(R.string.playlist_created, viewModel.getUiMessages().getValue().resId);
        assertEquals("New Playlist", viewModel.getUiMessages().getValue().formatArg);
    }

    @Test
    public void renamePlaylist_successRefreshesListAndPostsMessage() {
        gateway.addPlaylist("playlist-1", "Road Trip");

        viewModel.renamePlaylist("playlist-1", "  Renamed Playlist  ");

        assertNotNull(viewModel.getPlaylists().getValue());
        assertEquals(1, viewModel.getPlaylists().getValue().size());
        assertEquals("Renamed Playlist", String.valueOf(viewModel.getPlaylists().getValue().get(0).mediaMetadata.title));
        assertNotNull(viewModel.getUiMessages().getValue());
        assertEquals(R.string.playlist_renamed, viewModel.getUiMessages().getValue().resId);
        assertEquals("Renamed Playlist", viewModel.getUiMessages().getValue().formatArg);
    }

    @Test
    public void deletePlaylist_successRefreshesListAndPostsMessage() {
        gateway.addPlaylist("playlist-1", "Road Trip");
        gateway.addPlaylist("playlist-2", "Workout");

        viewModel.deletePlaylist("playlist-1", "Road Trip");

        assertNotNull(viewModel.getPlaylists().getValue());
        assertEquals(1, viewModel.getPlaylists().getValue().size());
        assertEquals("Workout", String.valueOf(viewModel.getPlaylists().getValue().get(0).mediaMetadata.title));
        assertNotNull(viewModel.getUiMessages().getValue());
        assertEquals(R.string.playlist_deleted, viewModel.getUiMessages().getValue().resId);
        assertEquals("Road Trip", viewModel.getUiMessages().getValue().formatArg);
    }

    private static class FakePlaylistGateway implements PlaylistRepositoryGateway {

        final List<PlaylistEntity> playlists = new ArrayList<>();
        PlaylistEntity lastCreatedPlaylist;
        String lastRenamedMediaId;
        String lastRenamedTitle;
        String lastDeletedMediaId;

        void addPlaylist(String mediaId, String title) {
            PlaylistEntity playlist = new PlaylistEntity();
            playlist.mediaId = mediaId;
            playlist.title = title;
            playlists.add(playlist);
        }

        @Override
        public void create(@NonNull PlaylistEntity entity, @NonNull RepositoryCallback<Void> callback) {
            lastCreatedPlaylist = entity;
            playlists.add(entity);
            callback.onComplete(new Result.Success<>());
        }

        @Override
        public void rename(@NonNull String mediaId, @NonNull String newTitle, @NonNull RepositoryCallback<Void> callback) {
            lastRenamedMediaId = mediaId;
            lastRenamedTitle = newTitle;
            for (PlaylistEntity playlist : playlists) {
                if (mediaId.equals(playlist.mediaId)) {
                    playlist.title = newTitle;
                    break;
                }
            }
            callback.onComplete(new Result.Success<>());
        }

        @Override
        public void delete(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback) {
            lastDeletedMediaId = mediaId;
            playlists.removeIf(playlist -> mediaId.equals(playlist.mediaId));
            callback.onComplete(new Result.Success<>());
        }

        @Override
        public void getAll(@NonNull RepositoryCallback<List<PlaylistEntity>> callback) {
            callback.onComplete(new Result.Success<>(new ArrayList<>(playlists)));
        }

        @Override
        public List<PlaylistEntity> getAll() {
            return new ArrayList<>(playlists);
        }

        @Override
        public PlaylistEntity getByMediaId(@NonNull String mediaId) {
            for (PlaylistEntity playlist : playlists) {
                if (mediaId.equals(playlist.mediaId)) {
                    return playlist;
                }
            }
            return null;
        }

        @Override
        public List<MediaItem> getSongsByPlaylistId(@NonNull String mediaId) {
            return List.of();
        }

        @Override
        public void addMediaItemsToPlaylist(@NonNull PlaylistEntity playlist, @NonNull List<MediaItem> mediaItems, @NonNull RepositoryCallback<Void> callback) {
            callback.onComplete(new Result.Success<>());
        }
    }
}




