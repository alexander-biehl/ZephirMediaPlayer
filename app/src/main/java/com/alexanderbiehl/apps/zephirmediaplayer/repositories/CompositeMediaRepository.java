package com.alexanderbiehl.apps.zephirmediaplayer.repositories;

import static androidx.media3.common.MediaMetadata.MEDIA_TYPE_MUSIC;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.ArtistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.AlbumSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.ArtistAlbums;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompositeMediaRepository {

    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;
    private final PlaylistDao playlistDao;

    public CompositeMediaRepository(
            AppDatabase db
    ) {
        this.artistDao = db.artistDao();
        this.albumDao = db.albumDao();
        this.songDao = db.songDao();
        this.playlistDao = db.playlistDao();
    }

    public List<MediaItem> /*void*/ getAllArtists(/*RepositoryCallback<List<MediaItem>> callback*/) {
        List<ArtistEntity> artists = Arrays.asList(artistDao.getArtists());
        //List<MediaItem> items =
        return artists
                .stream()
                .map(ArtistEntity::asItem)
                .collect(Collectors.toList());

        //callback.onComplete(new Result.Success<>(items));
    }

    public MediaItem /*void*/ getArtistByMediaId(String mediaId/*, RepositoryCallback<MediaItem> callback*/) {
        ArtistEntity entity = artistDao.getByMediaId(mediaId);
        if (entity != null) {
            return ArtistEntity.asItem(entity);
        }
        return null;
        /*callback.onComplete(
                new Result.Success<>(
                        ArtistEntity.asItem(entity)
                )
        );*/
    }

    public List<MediaItem> /*void*/ getAllAlbums(/*RepositoryCallback<List<MediaItem>> callback*/) {
        AlbumEntity[] entities = albumDao.getAll();
        List<MediaItem> items = new ArrayList<>();
        for (AlbumEntity entity : entities) {
            ArtistEntity artist = artistDao.getById(entity.albumArtistId);
            items.add(
                    new MediaItem.Builder()
                            .setMediaId(entity.mediaId)
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setTitle(entity.title)
                                            .setArtist(artist.title)
                                            .setIsPlayable(true)
                                            .setIsBrowsable(true)
                                            .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
                                            .build()
                            ).build()
            );
        }

        return items;
        // callback.onComplete(new Result.Success<>(items));
    }

    public MediaItem /*void*/ getAlbumByMediaId(String mediaId/*, RepositoryCallback<MediaItem> callback*/) {
        AlbumEntity entity = albumDao.getByMediaId(mediaId);
        if (entity != null) {
            ArtistEntity artist = artistDao.getById(entity.albumArtistId);
            return new MediaItem.Builder()
                    .setMediaId(entity.mediaId)
                    .setMediaMetadata(
                            new MediaMetadata.Builder()
                                    .setTitle(entity.title)
                                    .setArtist(artist.title)
                                    .setIsBrowsable(true)
                                    .setIsPlayable(true)
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
                                    .build()
                    ).build();
            // callback.onComplete(new Result.Success<>(item));
        } else {
            // callback.onComplete(new Result.Error<>(new Exception("Album not found")));
            return null;
        }
    }

    public List<MediaItem> /*void*/ getAlbumsByArtistId(String mediaId/*, RepositoryCallback<List<MediaItem>> callback*/) {
        ArtistAlbums albums = artistDao.getArtistAlbumsByMediaId(mediaId);
        ArtistEntity artistEntity = albums.artistEntity;
        // AlbumEntity[] entities = albumDao.getAlbumsForArtist(artistEntity.id);
        List<MediaItem> items = new ArrayList<>();
        for (AlbumEntity entity : albums.albumEntities) {
            items.add(new MediaItem.Builder()
                    .setMediaId(entity.mediaId)
                    .setMediaMetadata(
                            new MediaMetadata.Builder()
                                    .setTitle(entity.title)
                                    .setArtist(artistEntity.title)
                                    .setIsBrowsable(true)
                                    .setIsPlayable(true)
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
                                    .build()
                    ).build());
        }
        // callback.onComplete(new Result.Success<>(items));
        return items;
    }

    public List<MediaItem> /*void*/ getAllSongs(/*RepositoryCallback<List<MediaItem>> callback*/) {
        SongEntity[] entities = songDao.getAllSongs();
        List<MediaItem> items = new ArrayList<>();
        for (SongEntity entity : entities) {
            items.add(
                    new MediaItem.Builder()
                            .setMediaId(entity.mediaId)
                            .setUri(entity.sourceUri)
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setTitle(entity.title)
                                            .setArtist(artistDao.getById(entity.songArtistId).title)
                                            .setAlbumTitle(albumDao.getById(entity.songAlbumId).title)
                                            .setIsPlayable(true)
                                            .setIsBrowsable(false)
                                            .setMediaType(MEDIA_TYPE_MUSIC)
                                            .build()
                            ).build()
            );
        }
        // callback.onComplete(new Result.Success<>(items));
        return items;
    }

    public MediaItem /*void*/ getSongByMediaId(String mediaId/*, RepositoryCallback<MediaItem> callback*/) {
        SongEntity songEntity = songDao.getByMediaId(mediaId);
        if (songEntity != null) {
            ArtistEntity artist = artistDao.getById(songEntity.songArtistId);
            AlbumEntity album = albumDao.getById(songEntity.songAlbumId);
            MediaItem item = new MediaItem.Builder()
                    .setMediaId(songEntity.mediaId)
                    .setUri(songEntity.sourceUri)
                    .setMediaMetadata(
                            new MediaMetadata.Builder()
                                    .setTitle(songEntity.title)
                                    .setArtist(artist.title)
                                    .setAlbumTitle(album.title)
                                    .setIsBrowsable(false)
                                    .setIsPlayable(true)
                                    .setMediaType(MEDIA_TYPE_MUSIC)
                                    .build()
                    ).build();
            return item;
            // callback.onComplete(new Result.Success<>(item));
        }
        return null;
    }

    public List<MediaItem> /*void*/ getSongsByAlbumId(String mediaId/*, RepositoryCallback<List<MediaItem>> callback*/) {
        AlbumSongs albumSongs = albumDao.getAlbumSongsByMediaId(mediaId);
        if (albumSongs != null) {
            AlbumEntity album = albumSongs.albumEntity;
            List<SongEntity> songs = albumSongs.songEntities;
            ArtistEntity artist = artistDao.getById(album.albumArtistId);
            List<MediaItem> items = songs.stream().map(s ->
                    new MediaItem.Builder()
                            .setMediaId(s.mediaId)
                            .setUri(s.sourceUri)
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setTitle(s.title)
                                            .setArtist(artist.title)
                                            .setAlbumTitle(album.title)
                                            .setIsPlayable(true)
                                            .setIsBrowsable(false)
                                            .setMediaType(MEDIA_TYPE_MUSIC)
                                            .setTrackNumber(Integer.valueOf(s.trackNumber))
                                            .build()
                            ).build()
            ).collect(Collectors.toList());
            return items;
            // callback.onComplete(new Result.Success<>(items));
        } else {
            // callback.onComplete(new Result.Error<>(new Exception("Not found")));
            return null;
        }
    }

    public List<MediaItem> getAllPlaylists() {
        List<PlaylistEntity> playlistEntities = playlistDao.getAllPlaylists();
        List<MediaItem> items = new ArrayList<>();
        for (PlaylistEntity entity : playlistEntities) {
            items.add(
                    new MediaItem.Builder()
                            .setMediaId(entity.mediaId)
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setTitle(entity.title)
                                            .setIsPlayable(true)
                                            .setIsBrowsable(true)
                                            .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
                                            .build()
                            ).build()
            );
        }
        return items;
    }

    public MediaItem getPlaylistByMediaId(String mediaId) {
        PlaylistEntity entity = playlistDao.getByMediaId(mediaId);
        if (entity != null) {
            return new MediaItem.Builder()
                    .setMediaId(entity.mediaId)
                    .setMediaMetadata(
                            new MediaMetadata.Builder()
                                    .setTitle(entity.title)
                                    .setIsPlayable(true)
                                    .setIsBrowsable(true)
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
                                    .build()
                    ).build();
        }
        return null;
    }

    public List<MediaItem> getSongsByPlaylistId(String mediaId) {
        PlaylistSongs ps = playlistDao.getPlaylistSongsByMediaId(mediaId);
        List<MediaItem> items = new ArrayList<>();
        for (SongEntity entity : ps.songEntities) {
            items.add(
                    new MediaItem.Builder()
                            .setMediaId(entity.mediaId)
                            .setUri(entity.sourceUri)
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setTitle(entity.title)
//                                            .setArtist(artist.title)
//                                            .setAlbumTitle(album.title)
                                            .setIsPlayable(true)
                                            .setIsBrowsable(false)
                                            .setMediaType(MEDIA_TYPE_MUSIC)
                                            .build()
                            ).build()
            );
        }
        return items;
    }

}
