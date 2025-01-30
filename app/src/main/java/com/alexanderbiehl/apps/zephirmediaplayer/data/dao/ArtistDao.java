package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;

@Dao
public interface ArtistDao {

    @Insert
    void insert(Artist artist);
}
