package com.alexanderbiehl.apps.zephirmediaplayer.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.ArtistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Playlist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.AlbumSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.ArtistAlbums;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.ArtistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

@Database(entities = {
        Song.class,
        Artist.class,
        Album.class,
        Playlist.class,
        PlaylistSongM2M.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
    public abstract PlaylistDao playlistDao();
}
