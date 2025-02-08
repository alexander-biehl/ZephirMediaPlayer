package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.ArtistAlbums;

@Dao
public interface ArtistDao extends DoaBase<ArtistEntity> {

    @Query(value = "SELECT * FROM artists")
    ArtistEntity[] getArtists();

    @Query(value = "SELECT * FROM artists WHERE id = :id")
    ArtistEntity getById(long id);

    @Query("SELECT * FROM artists WHERE media_id = :mediaId")
    ArtistEntity getByMediaId(final String mediaId);

    @Transaction
    @Query("SELECT * FROM artists WHERE media_id = :mediaId")
    ArtistAlbums getArtistAlbumsByMediaId(final String mediaId);
}
