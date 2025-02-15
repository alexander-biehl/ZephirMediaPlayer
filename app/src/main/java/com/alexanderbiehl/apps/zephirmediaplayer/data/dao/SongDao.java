package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

import java.util.List;

@Dao
public interface SongDao extends DoaBase<SongEntity> {

    @Query(value = "SELECT * FROM songs")
    SongEntity[] getAllSongs();

    @Query(value = "SELECT * FROM songs WHERE id = :id")
    SongEntity getById(final Long id);

    @Query("SELECT * FROM songs WHERE media_id = :mediaId")
    SongEntity getByMediaId(String mediaId);

    @Insert
    long[] insertAll(SongEntity... songEntities);

    @Insert
    List<Long> insertAll(List<SongEntity> entities);
}
