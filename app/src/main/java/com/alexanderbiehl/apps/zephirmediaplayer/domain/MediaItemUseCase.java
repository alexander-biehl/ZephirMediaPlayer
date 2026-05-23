package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ALBUM_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ARTIST_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.ITEM_PREFIX;
import static com.alexanderbiehl.apps.zephirmediaplayer.database.entity.util.EntityExtractor.PLAYLIST_PREFIX;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MediaItem.LocalConfiguration;
import androidx.media3.common.util.UnstableApi;

import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.PlaylistRepositoryGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MediaItemUseCase {

    public static final String PLAYLIST_ID = "[playlistID]";
    private static final String ROOT_ID = "[rootID]";
    private static final String ALBUM_ID = "[albumID]";
    private static final String ARTIST_ID = "[artistID]";

    private final MediaItem rootItem;
    private final MediaItem artistsFolder;
    private final MediaItem albumsFolder;
    private final MediaItem playlistsFolder;

    private final PlaylistRepositoryGateway playlistRepository;
    private final SongRepositoryGateway songRepository;
    private final AlbumRepositoryGateway albumRepository;
    private final ArtistRepositoryGateway artistRepository;

    public MediaItemUseCase(
            PlaylistRepositoryGateway playlistRepository,
            SongRepositoryGateway songRepository,
            AlbumRepositoryGateway albumRepository,
            ArtistRepositoryGateway artistRepository
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
            default -> handleGetChildren(mediaId);
        };
    }

    private List<MediaItem> handleGetChildren(final String mediaId) {
        Optional<MediaItem> parentOption = getItem(mediaId);
        if (parentOption.isEmpty()) {
            return new ArrayList<>();
        }
        MediaItem parent = parentOption.get();
        Integer parentType = parent.mediaMetadata.mediaType;
        if (parentType == null) {
            return new ArrayList<>();
        }
        return switch (parentType) {
            case MediaMetadata.MEDIA_TYPE_ARTIST -> {
                Artist artist = artistRepository.getAlbumsByArtistId(mediaId);
                if (artist == null || artist.albums == null) {
                    yield new ArrayList<>();
                }
                yield artist.albums.stream().map(album ->
                                new MediaItem.Builder()
                                        .setMediaId(album.mediaId)
                                        .setMediaMetadata(new MediaMetadata.Builder()
                                                .setTitle(album.title)
                                                .setArtist(artist.title)
                                                .setIsBrowsable(true)
                                                .setIsPlayable(true)
                                                .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
                                                .build())
                                        .build())
                        .collect(Collectors.toList());
            }
            case MediaMetadata.MEDIA_TYPE_ALBUM -> albumRepository.getSongsByAlbumId(mediaId)
                    .stream()
                    .map(SongEntity::toItem)
                    .collect(Collectors.toList());
            case MediaMetadata.MEDIA_TYPE_PLAYLIST -> playlistRepository.getSongsByPlaylistId(mediaId);
            default -> new ArrayList<>();
        };
    }

    public Optional<MediaItem> getItem(final String mediaId) {
        if (mediaId == null || mediaId.isEmpty()) {
            return Optional.empty();
        }

        if (mediaId.startsWith(ITEM_PREFIX)) {
            return Optional.of(SongEntity.toItem(songRepository.getById(mediaId)));
        } else if (mediaId.startsWith(ALBUM_PREFIX)) {
            return Optional.of(AlbumEntity.asItem(albumRepository.getById(mediaId)));
        } else if (mediaId.startsWith(ARTIST_PREFIX)) {
            return Optional.ofNullable(artistRepository.getById(mediaId)).map(Artist::asItem);
        } else if (mediaId.startsWith(PLAYLIST_PREFIX)) {
            return Optional.of(PlaylistEntity.toItem(playlistRepository.getByMediaId(mediaId)));
        }
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
        LocalConfiguration localConfiguration = foundLocalItem.localConfiguration;
        if (localConfiguration == null) {
            return Optional.empty();
        }
        MediaMetadata metadata = foundLocalItem.mediaMetadata.buildUpon()
                .populate(remoteItem.mediaMetadata).build();
        return Optional.of(
                remoteItem.buildUpon()
                        .setMediaMetadata(metadata)
                        .setUri(localConfiguration.uri)
                        .build()
        );
    }
}
