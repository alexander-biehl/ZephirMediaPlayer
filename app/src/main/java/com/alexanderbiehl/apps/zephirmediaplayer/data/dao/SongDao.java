package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;

@Dao
public interface SongDao extends DoaBase<Song> {

    @Query(value = "SELECT * FROM song")
    Song[] getAllSongs();

    @Query(value = "SELECT * FROM song WHERE id = :id")
    Song getById(final Long id);

    @Query("SELECT * FROM song WHERE media_id = :mediaId")
    Song getByMediaId(String mediaId);
}
