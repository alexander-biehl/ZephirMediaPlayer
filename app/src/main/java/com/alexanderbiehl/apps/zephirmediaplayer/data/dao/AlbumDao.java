package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;

@Dao
public interface AlbumDao {

    @Insert
    void insert(Album album);

    @Insert
    void insertAll(Album... albums);

    @Update
    void update(Album album);

    @Delete
    void delete(Album album);
}
