package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;

import java.util.List;

@Dao
public interface PlaylistDao extends DoaBase<PlaylistEntity> {

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :id")
    PlaylistSongs getPlaylistSongsById(final Long id);

    @Transaction
    @Query("SELECT * FROM playlists WHERE media_id = :mediaId")
    PlaylistSongs getPlaylistSongsByMediaId(final String mediaId);

    @Transaction
    @Insert
    long[] insertPlaylistSongs(PlaylistSongM2M... pls);

    @Query("SELECT * FROM playlists")
    List<PlaylistEntity> getAllPlaylists();

    @Query("SELECT * FROM playlists WHERE media_id = :mediaId")
    PlaylistEntity getByMediaId(final String mediaId);
}
