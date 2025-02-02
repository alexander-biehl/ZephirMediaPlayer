package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Playlist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

import java.util.List;

@Dao
public abstract class PlaylistDao implements DoaBase<Playlist> {

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :id")
    public abstract PlaylistSongs getPlaylistSongsById(final Long id);

    @Transaction
    @Insert
    public abstract long[] insertPlaylistSongs(PlaylistSongM2M... pls);
}
