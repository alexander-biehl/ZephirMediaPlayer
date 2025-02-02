package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Playlist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

@Dao
public interface PlaylistDao extends DoaBase<Playlist> {

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :id")
    PlaylistSongs getPlaylistSongsById(final Long id);

    @Insert
    void addToPlaylist(PlaylistSongM2M... ps);
}
