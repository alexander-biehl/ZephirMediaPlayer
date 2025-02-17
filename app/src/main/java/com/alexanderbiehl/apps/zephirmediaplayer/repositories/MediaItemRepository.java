package com.alexanderbiehl.apps.zephirmediaplayer.repositories;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        }).orElseGet(ArrayList<MediaItem>::new);
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

    @OptIn(markerClass = UnstableApi.class)
    public MediaItem expandItem(MediaItem item) {
        MediaMetadata metadata = item.mediaMetadata.buildUpon()
                .populate(item.mediaMetadata).build();
        return item.buildUpon()
                .setMediaMetadata(metadata)
                .setUri(item.localConfiguration.uri)
                .build();
    }
}
