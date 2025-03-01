package com.alexanderbiehl.apps.zephirmediaplayer.repositories;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The MediaItemRepository is a top-level repository that consumes the CompositeMediaRepository.
 * The CompositeMediaRepository is used to retrieve the underlying database entities, and this class
 * is responsible for converting those into MediaItem records usable by the MediaBrowserService
 * Media3Service.
 */
public class MediaItemRepository {

    private static final String ROOT_ID = "[rootID]";
    private static final String ALBUM_ID = "[albumID]";
    private static final String ARTIST_ID = "[artistID]";
    private static final String PLAYLIST_ID = "playlistID]";
    private final MediaItem rootItem;
    private final MediaItem artistsFolder;
    private final MediaItem albumsFolder;
    private final MediaItem playlistsFolder;
    private final CompositeMediaRepository compositeMediaRepository;

    public MediaItemRepository(
            CompositeMediaRepository compositeMediaRepository
    ) {
        this.compositeMediaRepository = compositeMediaRepository;
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
        switch (mediaId) {
            case ROOT_ID:
                return Arrays.asList(artistsFolder, albumsFolder, playlistsFolder);
            case ALBUM_ID:
                return compositeMediaRepository.getAllAlbums();
            case ARTIST_ID:
                return compositeMediaRepository.getAllArtists();
            case PLAYLIST_ID:
                return compositeMediaRepository.getAllPlaylists();
            default:
                return handleGetChildren(mediaId);
        }
    }

    private List<MediaItem> handleGetChildren(final String mediaId) {
        Optional<MediaItem> parentOpt = getItem(mediaId);
        return parentOpt.map(parent -> {
            switch (parent.mediaMetadata.mediaType) {
                case MediaMetadata.MEDIA_TYPE_ARTIST:
                    return compositeMediaRepository.getAlbumsByArtistId(parent.mediaId);
                case MediaMetadata.MEDIA_TYPE_ALBUM:
                    return compositeMediaRepository.getSongsByAlbumId(parent.mediaId);
                case MediaMetadata.MEDIA_TYPE_PLAYLIST:
                    return compositeMediaRepository.getSongsByPlaylistId(parent.mediaId);
                default:
                    return new ArrayList<MediaItem>();
            }
        }).orElseGet(ArrayList::new);
    }

    public Optional<MediaItem> getItem(final String mediaId) {
        MediaItem item = compositeMediaRepository.getArtistByMediaId(mediaId);
        if (item == null) {
            item = compositeMediaRepository.getAlbumByMediaId(mediaId);
            if (item == null) {
                item = compositeMediaRepository.getPlaylistByMediaId(mediaId);
                if (item == null) {
                    item = compositeMediaRepository.getSongByMediaId(mediaId);
                    if (item == null) {
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
