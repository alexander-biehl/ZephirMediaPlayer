package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;

import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.AlbumRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.ArtistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.PlaylistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.SongRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaItemUseCase {

    public static final String PLAYLIST_ID = "[playlistID]";
    private static final String TAG = MediaItemUseCase.class.getSimpleName();
    private static final String ROOT_ID = "[rootID]";
    private static final String ALBUM_ID = "[albumID]";
    private static final String ARTIST_ID = "[artistID]";

    private final MediaItem rootItem;
    private final MediaItem artistsFolder;
    private final MediaItem albumsFolder;
    private final MediaItem playlistsFolder;

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public MediaItemUseCase(
            PlaylistRepository playlistRepository,
            SongRepository songRepository,
            AlbumRepository albumRepository,
            ArtistRepository artistRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;

        rootItem = new MediaItem.Builder()
                .setMediaId(ROOT_ID)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle("Root Folder")
                                .setIsBrowsable(true)
                                .setIsPlayable(false)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                                .build()
                ).build();

        artistsFolder = new MediaItem.Builder()
                .setMediaId(ARTIST_ID)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle("Artists")
                                .setIsBrowsable(true)
                                .setIsPlayable(false)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS)
                                .build()
                ).build();

        albumsFolder = new MediaItem.Builder()
                .setMediaId(ALBUM_ID)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle("Albums")
                                .setIsBrowsable(true)
                                .setIsPlayable(false)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS)
                                .build()
                ).build();

        playlistsFolder = new MediaItem.Builder()
                .setMediaId(PLAYLIST_ID)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle("Playlists")
                                .setIsBrowsable(true)
                                .setIsPlayable(false)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS)
                                .build()
                ).build();
    }

    public MediaItem getRoot() {
        return rootItem;
    }

    public List<MediaItem> getChildren(final String mediaId) {
        return switch (mediaId) {
            case ROOT_ID -> List.of(artistsFolder, albumsFolder, playlistsFolder);
            case ARTIST_ID -> artistRepository.getArtists();
            case ALBUM_ID -> albumRepository.getAlbums();
            case PLAYLIST_ID -> playlistRepository.getAll();
            default -> handleGetChildren(mediaId);
        };
    }

    private List<MediaItem> handleGetChildren(String mediaId) {
        Optional<MediaItem> parentOption = getItem(mediaId);
        return parentOption.map(parent -> {
            return switch (parent.mediaMetadata.mediaType) {
                case MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS ->
                        artistRepository.getAlbumsByArtistId(mediaId);
                case MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS ->
                        albumRepository.getSongsByAlbumId(mediaId);
                case MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS ->
                        playlistRepository.getSongsByPlaylistId(mediaId);
                default -> new ArrayList<MediaItem>();
            };
        }).orElseGet(ArrayList::new);
    }

    public Optional<MediaItem> getItem(final String mediaId) {
        MediaItem item = artistRepository.getById(mediaId);
        if (item == null) {
            item = albumRepository.getById(mediaId);
            if (item == null) {
                item = playlistRepository.getById(mediaId);
                if (item == null) {
                    item = songRepository.getById(mediaId);
                    if (item == null) {
                        // If no item found, return an empty Optional
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.of(item);
    }

    /**
     * Since the localConfiguration is stripped out when a mediaItem is sent back and forth
     * from controller to browser, we need to re-instantiate it from the DB when we are going
     * to play
     *
     * @param remoteItem - The remote media item that we wish to play
     * @return Optional<MediaItem> The expanded MediaItem with localConfiguration
     */
    @OptIn(markerClass = UnstableApi.class)
    public Optional<MediaItem> expandItem(MediaItem remoteItem) {
        Optional<MediaItem> localItem = getItem(remoteItem.mediaId);
        if (localItem.isEmpty()) {
            return Optional.empty();
        }
        MediaItem foundLocalItem = localItem.get();
        MediaMetadata metadata = foundLocalItem.mediaMetadata.buildUpon()
                .populate(remoteItem.mediaMetadata).build();
        return Optional.of(
                remoteItem.buildUpon()
                        .setMediaMetadata(metadata)
                        .setUri(foundLocalItem.localConfiguration.uri)
                        .build()
        );
    }
}
