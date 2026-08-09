package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ALBUM_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ARTIST_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.PLAYLIST_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.domain.MediaItemUseCase.PLAYLIST_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.PlaylistRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaItemUseCaseTests {

    private FakePlaylistRepository playlistGateway;
    private FakeAlbumRepository albumGateway;
    private FakeArtistRepository artistGateway;
    private MediaItemUseCase useCase;

    @Before
    public void setUp() {
        playlistGateway = new FakePlaylistRepository();
        FakeSongRepository songGateway = new FakeSongRepository();
        albumGateway = new FakeAlbumRepository();
        artistGateway = new FakeArtistRepository();
        useCase = new MediaItemUseCase(playlistGateway, songGateway, albumGateway, artistGateway);
    }

    @Test
    public void getRoot_returnsRootMediaItem() {
        MediaItem root = useCase.getRoot();

        assertEquals("[rootID]", root.mediaId);
        assertEquals("Root Folder", String.valueOf(root.mediaMetadata.title));
        assertTrue(root.mediaMetadata.isBrowsable);
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED), root.mediaMetadata.mediaType);
    }

    @Test
    public void getChildren_forRootReturnsTopLevelFolders() {
        List<MediaItem> children = useCase.getChildren("[rootID]");

        assertEquals(3, children.size());
        assertEquals("[artistID]", children.get(0).mediaId);
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS), children.get(0).mediaMetadata.mediaType);
        assertEquals("[albumID]", children.get(1).mediaId);
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS), children.get(1).mediaMetadata.mediaType);
        assertEquals(PLAYLIST_ID, children.get(2).mediaId);
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS), children.get(2).mediaMetadata.mediaType);
    }

    @Test
    public void getChildren_getArtistsReturnsArtists() {
        artistGateway.artist = new Artist(ARTIST_PREFIX + "artist-1", "Artist One");

        List<MediaItem> children = useCase.getChildren("[artistID]");

        assertEquals(1, children.size());
        assertEquals(ARTIST_PREFIX + "artist-1", children.get(0).mediaId);
        assertEquals("Artist One", String.valueOf(children.get(0).mediaMetadata.title));
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_ARTIST), children.get(0).mediaMetadata.mediaType);
    }

    @Test
    public void getChildren_getArtistsReturnsEmptyListWhenNoArtistsExist() {
        artistGateway.artist = null;

        List<MediaItem> children = useCase.getChildren("[artistID]");

        assertEquals(0, children.size());
    }

    @Test
    public void getChildren_getAlbumsReturnsAlbums() {
        albumGateway.album = new AlbumEntity();
        albumGateway.album.mediaId = ALBUM_PREFIX + "album-1";
        albumGateway.album.title = "Album One";

        List<MediaItem> children = useCase.getChildren("[albumID]");

        assertEquals(1, children.size());
        assertEquals(ALBUM_PREFIX + "album-1", children.get(0).mediaId);
        assertEquals("Album One", String.valueOf(children.get(0).mediaMetadata.title));
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_ALBUM), children.get(0).mediaMetadata.mediaType);
    }

    @Test
    public void getChildren_getAlbumsReturnsEmptyListWhenNoAlbumsExist() {
        albumGateway.album = null;

        List<MediaItem> children = useCase.getChildren("[albumID]");

        assertEquals(0, children.size());
    }

    @Test
    public void getChildren_forArtistReturnsAlbums() {
        artistGateway.artistWithAlbums = new Artist("artist-1", "Artist One");
        artistGateway.artistWithAlbums.albums = List.of(
                new Album("album-1", "Album One", artistGateway.artistWithAlbums),
                new Album("album-2", "Album Two", artistGateway.artistWithAlbums)
        );
        artistGateway.artist = artistGateway.artistWithAlbums;

        List<MediaItem> children = useCase.getChildren(ARTIST_PREFIX + "artist-1");

        assertEquals(2, children.size());
        assertEquals("album-1", children.get(0).mediaId);
        assertEquals("Album One", String.valueOf(children.get(0).mediaMetadata.title));
        assertEquals("Artist One", String.valueOf(children.get(0).mediaMetadata.artist));
        assertEquals(Integer.valueOf(MediaMetadata.MEDIA_TYPE_ALBUM), children.get(0).mediaMetadata.mediaType);
    }

    @Test
    public void getChildren_forAlbumReturnsEmptyListWhenNoSongsExist() {
        albumGateway.album = new AlbumEntity();
        albumGateway.album.mediaId = ALBUM_PREFIX + "album-1";
        albumGateway.album.title = "Album One";
        albumGateway.songsForAlbum = List.of();

        List<MediaItem> children = useCase.getChildren(ALBUM_PREFIX + "album-1");

        assertEquals(0, children.size());
    }

    @Test
    public void getChildren_forPlaylistReturnsPlaylistSongs() {
        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = PLAYLIST_ID + "playlist-1";
        playlist.title = "Road Trip";
        playlistGateway.playlists = List.of(playlist);

        List<MediaItem> children = useCase.getChildren(PLAYLIST_ID);

        assertEquals(1, children.size());
        assertEquals(playlist.mediaId, children.get(0).mediaId);
        assertEquals("Road Trip", String.valueOf(children.get(0).mediaMetadata.title));
    }

    @Test
    public void getItem_mapsAlbumArtistAndPlaylistPrefixes() {
        AlbumEntity album = new AlbumEntity();
        album.mediaId = ALBUM_PREFIX + "album-1";
        album.title = "Album One";
        albumGateway.album = album;

        artistGateway.artist = new Artist("artist-1", "Artist One");

        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = PLAYLIST_PREFIX + "playlist-1";
        playlist.title = "Road Trip";
        playlistGateway.playlist = playlist;

        Optional<MediaItem> albumItem = useCase.getItem(album.mediaId);
        Optional<MediaItem> artistItem = useCase.getItem(ARTIST_PREFIX + "artist-1");
        Optional<MediaItem> playlistItem = useCase.getItem(playlist.mediaId);

        assertTrue(albumItem.isPresent());
        assertEquals("Album One", String.valueOf(albumItem.get().mediaMetadata.title));
        assertTrue(artistItem.isPresent());
        assertEquals("Artist One", String.valueOf(artistItem.get().mediaMetadata.title));
        assertTrue(playlistItem.isPresent());
        assertEquals("Road Trip", String.valueOf(playlistItem.get().mediaMetadata.title));
    }

    @Test
    public void expandItem_returnsEmptyForAlbumWithoutLocalConfiguration() {
        MediaItem remote = new MediaItem.Builder()
                .setMediaId(ALBUM_PREFIX + "album-1")
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle("Remote Title")
                        .setArtist("Remote Artist")
                        .build())
                .build();

        albumGateway.album = new AlbumEntity();
        albumGateway.album.mediaId = ALBUM_PREFIX + "album-1";
        albumGateway.album.title = "Album One";

        Optional<MediaItem> expanded = useCase.expandItem(remote);

        assertTrue(expanded.isEmpty());
    }

    private static class FakePlaylistRepository implements PlaylistRepository {
        List<PlaylistEntity> playlists = new ArrayList<>();
        PlaylistEntity playlist;

        @Override
        public void create(@NonNull PlaylistEntity entity, @NonNull RepositoryCallback<Void> callback) {
        }

        @Override
        public void rename(@NonNull String mediaId, @NonNull String newTitle, @NonNull RepositoryCallback<Void> callback) {
        }

        @Override
        public void delete(@NonNull String mediaId, @NonNull RepositoryCallback<Void> callback) {
        }

        @Override
        public void getAll(@NonNull RepositoryCallback<List<PlaylistEntity>> callback) {
            callback.onComplete(new Result.Success<>(playlists));
        }

        @Override
        public List<PlaylistEntity> getAll() {
            return playlists;
        }

        @Override
        public List<MediaItem> getSongsByPlaylistId(@NonNull String mediaId) {
            return List.of();
        }

        @Override
        public void addMediaItemsToPlaylist(@NonNull PlaylistEntity playlist, @NonNull List<MediaItem> mediaItems, @NonNull RepositoryCallback<Void> callback) {
        }

        @Override
        public PlaylistEntity getByMediaId(@NonNull String mediaId) {
            return playlist;
        }
    }

    private static class FakeSongRepository implements SongRepository {
        SongEntity song;

        @Override
        public SongEntity getById(@NonNull String mediaId) {
            return song;
        }
    }

    private static class FakeAlbumRepository implements AlbumRepository {
        List<SongEntity> songsForAlbum = new ArrayList<>();
        AlbumEntity album;

        @Override
        public List<SongEntity> getSongsByAlbumId(@NonNull String mediaId) {
            return songsForAlbum;
        }

        @Override
        public AlbumEntity getById(@NonNull String mediaId) {
            return album;
        }

        @Override
        public List<AlbumEntity> getAlbums() {
            return album == null ? List.of() : List.of(album);
        }

        @Override
        public List<AlbumEntity> getAlbumsByArtistId(long artistId) {
            return album == null ? List.of() : List.of(album);
        }
    }

    private static class FakeArtistRepository implements ArtistRepository {
        Artist artist;
        Artist artistWithAlbums;

        @Override
        public Artist getAlbumsByArtistId(@NonNull String mediaId) {
            return artistWithAlbums;
        }

        @Override
        public Artist getById(@NonNull String mediaId) {
            return artist;
        }

        @Override
        public List<Artist> getArtists() {
            return artist == null ? List.of() : List.of(artist);
        }
    }
}









