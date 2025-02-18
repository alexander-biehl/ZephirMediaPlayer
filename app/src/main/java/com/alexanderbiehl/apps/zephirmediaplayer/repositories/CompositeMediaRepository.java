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
import java.util.stream.Collectors;

/**
 * The CompositeMediaRepository handles operations on Song/Artist/Album/PlaylistEntities.
 */
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

    public List<MediaItem> getAllArtists() {
        List<ArtistEntity> artists = Arrays.asList(artistDao.getArtists());
        return artists
                .stream()
                .map(ArtistEntity::asItem)
                .collect(Collectors.toList());
    }

    public MediaItem getArtistByMediaId(String mediaId) {
        ArtistEntity entity = artistDao.getByMediaId(mediaId);
        if (entity != null) {
            return ArtistEntity.asItem(entity);
        }
        return null;
    }

    public List<MediaItem> getAllAlbums() {
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
    }

    public MediaItem getAlbumByMediaId(String mediaId) {
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
        } else {
            return null;
        }
    }

    public List<MediaItem> getAlbumsByArtistId(String mediaId) {
        ArtistAlbums albums = artistDao.getArtistAlbumsByMediaId(mediaId);
        ArtistEntity artistEntity = albums.artistEntity;
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
        return items;
    }

    public List<MediaItem> getAllSongs() {
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
        return items;
    }

    public MediaItem getSongByMediaId(String mediaId) {
        SongEntity songEntity = songDao.getByMediaId(mediaId);
        if (songEntity != null) {
            ArtistEntity artist = artistDao.getById(songEntity.songArtistId);
            AlbumEntity album = albumDao.getById(songEntity.songAlbumId);
            return new MediaItem.Builder()
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
        }
        return null;
    }

    public List<MediaItem> getSongsByAlbumId(String mediaId) {
        AlbumSongs albumSongs = albumDao.getAlbumSongsByMediaId(mediaId);
        if (albumSongs != null) {
            AlbumEntity album = albumSongs.albumEntity;
            List<SongEntity> songs = albumSongs.songEntities;
            ArtistEntity artist = artistDao.getById(album.albumArtistId);
            return songs.stream().map(s ->
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
        } else {
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
