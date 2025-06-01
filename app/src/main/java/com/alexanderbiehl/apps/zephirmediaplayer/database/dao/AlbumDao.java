package com.alexanderbiehl.apps.zephirmediaplayer.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.AlbumSongs;

@Dao
public interface AlbumDao extends DoaBase<AlbumEntity> {

    @Insert
    void insertAll(AlbumEntity... albumEntities);

    @Query("SELECT * FROM albums WHERE media_id = :mediaId")
    AlbumEntity getByMediaId(final String mediaId);

    @Query("SELECT * FROM albums WHERE id = :id")
    AlbumEntity getById(final Long id);

    @Query("SELECT * FROM albums")
    AlbumEntity[] getAll();

    @Transaction
    @Query("SELECT * FROM albums WHERE media_id = :mediaId")
    AlbumSongs getAlbumSongsByMediaId(final String mediaId);

    @Query("SELECT * from albums WHERE albumArtistId = :artistId")
    AlbumEntity[] getAlbumsForArtist(final Long artistId);
}
