package com.alexanderbiehl.apps.zephirmediaplayer.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.AlbumSongs;

import java.util.List;

@Dao
public interface AlbumDao extends DoaBase<Album> {

    @Insert
    void insertAll(Album... albums);


    @Query("SELECT * FROM album WHERE media_id = :mediaId")
    List<AlbumSongs> getAlbumSongsByMediaId(final String mediaId);
}
