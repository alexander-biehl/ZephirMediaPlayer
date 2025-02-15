package com.alexanderbiehl.apps.zephirmediaplayer.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.ArtistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

@Database(entities = {
        SongEntity.class,
        ArtistEntity.class,
        AlbumEntity.class,
        PlaylistEntity.class,
        PlaylistSongM2M.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "music_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract SongDao songDao();

    public abstract AlbumDao albumDao();

    public abstract ArtistDao artistDao();

    public abstract PlaylistDao playlistDao();
}
