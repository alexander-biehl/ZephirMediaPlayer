package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ALBUM_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ARTIST_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ITEM_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.PLAYLIST_PREFIX;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;

import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.AlbumRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.ArtistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.PlaylistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.SongRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            case ARTIST_ID -> artistRepository.getArtists()
                    .stream()
                    .map(Artist::asItem)
                    .collect(Collectors.toList());
            case ALBUM_ID -> albumRepository.getAlbums()
                    .stream()
                    .map(AlbumEntity::asItem)
                    .collect(Collectors.toList());
            case PLAYLIST_ID -> playlistRepository.getAll()
                    .stream()
                    .map(PlaylistEntity::toItem)
                    .collect(Collectors.toList());
            //default -> handleGetChildren(mediaId);
            default -> throw new IllegalStateException("Unexpected value: " + mediaId);
        };
    }

    /*
    TODO need to refactor this and getItem. If MediaItemUseCase is handling
    the usecase of getting internal elements as MediaItems, each of the underlying
    repositories should return their own record types and this should handle the conversion
    Need to come up with a better way of deciding to query for songs, albums, artists or playlists
    in getItem.
     */
//    private List<MediaItem> handleGetChildren(String mediaId) {
//        Optional<MediaItem> parentOption = getItem(mediaId);
//        return parentOption.map(parent -> {
//            return switch (parent.mediaMetadata.mediaType) {
//                case MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS -> 
//                        artistRepository.getAlbumsByArtistId(mediaId)
//                                .albums
//                                .stream()
//                                .map(AlbumEntity::asItem)
//                                .collect(Collectors.toList());
//                case MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS ->
//                        albumRepository.getSongsByAlbumId(mediaId);
//                case MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS ->
//                        playlistRepository.getSongsByPlaylistId(mediaId);
//                default -> new ArrayList<MediaItem>();
//            };
//        }).orElseGet(ArrayList::new);
//    }

    public Optional<MediaItem> getItem(final String mediaId) {
        if (mediaId == null || mediaId.isEmpty()) {
            return Optional.empty();
        }

        if (mediaId.startsWith(ITEM_PREFIX)) {
            return Optional.of(SongEntity.toItem(songRepository.getById(mediaId)));
        } else if (mediaId.startsWith(ALBUM_PREFIX)) {
            return Optional.of(AlbumEntity.asItem(albumRepository.getById(mediaId)));
        } else if (mediaId.startsWith(ARTIST_PREFIX)) {
            //return Optional.of(ArtistEntity.asItem(artistRepository.getById(mediaId)));
            return Optional.empty();
        } else if (mediaId.startsWith(PLAYLIST_PREFIX)) {
            return Optional.of(PlaylistEntity.toItem(playlistRepository.getByMediaId(mediaId)));
        }
//        MediaItem item = artistRepository.getById(mediaId);
//        if (item == null) {
//            item = albumRepository.getById(mediaId);
//            if (item == null) {
//                item = playlistRepository.getById(mediaId);
//                if (item == null) {
//                    item = songRepository.getById(mediaId);
//                    if (item == null) {
//                        // If no item found, return an empty Optional
//                        return Optional.empty();
//                    }
//                }
//            }
//        }
//        return Optional.of(item);
        return Optional.empty();
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
