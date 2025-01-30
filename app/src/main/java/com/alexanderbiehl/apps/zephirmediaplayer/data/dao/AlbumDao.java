package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;

@Dao
public interface AlbumDao {

    @Insert
    void insert(Album album);
}
