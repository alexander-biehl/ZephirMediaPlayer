package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;

@Dao
public interface ArtistDao extends DoaBase<Artist> {

    @Query(value = "SELECT * FROM artist")
    Artist[] getArtists();

    @Query(value = "SELECT * FROM artist WHERE id = :id")
    Artist getById(long id);

}
