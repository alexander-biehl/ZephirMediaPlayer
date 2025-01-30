package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;

@Dao
public interface SongDao {

    @Insert
    void insert(Song song);
}
