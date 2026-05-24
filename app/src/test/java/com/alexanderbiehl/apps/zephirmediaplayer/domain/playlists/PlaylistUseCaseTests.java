package com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PlaylistUseCaseTests {

    private FakePlaylist gateway;

    @Before
    public void setUp() {
        gateway = new FakePlaylist();
    }

    @Test
    public void createPlaylist_trimsTitleAndDelegates() {
        CreatePlaylistUseCase useCase = new CreatePlaylistUseCase(gateway);

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute("  My Playlist  ", resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Success);
        assertEquals(1, gateway.createdEntities.size());
        assertEquals("My Playlist", gateway.createdEntities.get(0).title);
        assertNotNull(gateway.createdEntities.get(0).mediaId);
        assertTrue(gateway.createdEntities.get(0).mediaId.startsWith("[playlistEntity]"));
    }

    @Test
    public void createPlaylist_rejectsBlankTitle() {
        CreatePlaylistUseCase useCase = new CreatePlaylistUseCase(gateway);

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute("   ", resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Error);
        assertEquals(0, gateway.createdEntities.size());
    }

    @Test
    public void renamePlaylist_trimsTitleAndDelegates() {
        RenamePlaylistUseCase useCase = new RenamePlaylistUseCase(gateway);

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute("playlist-1", "  Renamed  ", resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Success);
        assertEquals("playlist-1", gateway.lastRenamedMediaId);
        assertEquals("Renamed", gateway.lastRenamedTitle);
    }

    @Test
    public void renamePlaylist_rejectsBlankTitle() {
        RenamePlaylistUseCase useCase = new RenamePlaylistUseCase(gateway);

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute("playlist-1", "", resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Error);
        assertFalse(gateway.renameCalled);
    }

    @Test
    public void deletePlaylist_delegates() {
        DeletePlaylistUseCase useCase = new DeletePlaylistUseCase(gateway);

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute("playlist-1", resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Success);
        assertEquals("playlist-1", gateway.lastDeletedMediaId);
    }

    @Test
    public void getPlaylists_mapsEntitiesToMediaItems() {
        PlaylistEntity one = new PlaylistEntity();
        one.mediaId = "playlist-1";
        one.title = "One";
        PlaylistEntity two = new PlaylistEntity();
        two.mediaId = "playlist-2";
        two.title = "Two";
        gateway.playlistsToReturn = List.of(one, two);

        GetPlaylistsUseCase useCase = new GetPlaylistsUseCase(gateway);

        AtomicReference<Result<List<MediaItem>>> resultRef = new AtomicReference<>();
        useCase.execute(result -> {
            @SuppressWarnings("unchecked")
            Result<List<MediaItem>> cast = (Result<List<MediaItem>>) result;
            resultRef.set(cast);
        });

        assertTrue(resultRef.get() instanceof Result.Success);
        @SuppressWarnings("unchecked")
        List<MediaItem> items = ((Result.Success<List<MediaItem>>) resultRef.get()).data;
        assertEquals(2, items.size());
        assertEquals("playlist-1", items.get(0).mediaId);
        assertEquals("One", items.get(0).mediaMetadata.title.toString());
        assertEquals("playlist-2", items.get(1).mediaId);
        assertEquals("Two", items.get(1).mediaMetadata.title.toString());
    }

    @Test
    public void addMediaItemsToPlaylist_rejectsEmptyList() {
        AddMediaItemsToPlaylistUseCase useCase = new AddMediaItemsToPlaylistUseCase(gateway);
        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = "playlist-1";

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute(playlist, List.of(), resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Error);
        assertFalse(gateway.addCalled);
    }

    @Test
    public void addMediaItemsToPlaylist_delegates() {
        AddMediaItemsToPlaylistUseCase useCase = new AddMediaItemsToPlaylistUseCase(gateway);
        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = "playlist-1";
        MediaItem mediaItem = new MediaItem.Builder().setMediaId("song-1").build();

        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        useCase.execute(playlist, List.of(mediaItem), resultRef::set);

        assertTrue(resultRef.get() instanceof Result.Success);
        assertTrue(gateway.addCalled);
        assertEquals("playlist-1", gateway.lastAddedPlaylist.mediaId);
        assertEquals(1, gateway.lastAddedItems.size());
        assertEquals("song-1", gateway.lastAddedItems.get(0).mediaId);
    }

    private static class FakePlaylist implements PlaylistRepository {

        final List<PlaylistEntity> createdEntities = new ArrayList<>();
        List<PlaylistEntity> playlistsToReturn = new ArrayList<>();
        boolean renameCalled;
        boolean addCalled;
        String lastRenamedMediaId;
        String lastRenamedTitle;
        String lastDeletedMediaId;
        PlaylistEntity lastAddedPlaylist;
        List<MediaItem> lastAddedItems = new ArrayList<>();

        @Override
        public void create(@NonNull PlaylistEntity entity, @NonNull RepositoryCallback<Void> callback) {
            createdEntities.add(entity);
            callback.onComplete(new Result.Success<>());
        }

        @Override
        public void rename(@NonNull String mediaId, @NonNull String newTitle, @NonNull RepositoryCallback<Void> callback) {
            renameCalled = true;
            lastRenamedMediaId = mediaId;
            lastRenamedTitle = newTitle;
            callback.onComplete(new Result.Success<>());
        }

        @Override
        public void delete(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback) {
            lastDeletedMediaId = mediaId;
            callback.onComplete(new Result.Success<>());
        }

        @Override
        public void getAll(@NonNull RepositoryCallback<List<PlaylistEntity>> callback) {
            callback.onComplete(new Result.Success<>(playlistsToReturn));
        }

        @Override
        public List<PlaylistEntity> getAll() {
            return playlistsToReturn;
        }

        @Override
        public PlaylistEntity getByMediaId(@NonNull String mediaId) {
            return null;
        }

        @Override
        public List<MediaItem> getSongsByPlaylistId(@NonNull String mediaId) {
            return List.of();
        }

        @Override
        public void addMediaItemsToPlaylist(
                @NonNull PlaylistEntity playlist,
                @NonNull List<MediaItem> mediaItems,
                @NonNull RepositoryCallback<Void> callback
        ) {
            addCalled = true;
            lastAddedPlaylist = playlist;
            lastAddedItems = new ArrayList<>(mediaItems);
            callback.onComplete(new Result.Success<>());
        }
    }
}



