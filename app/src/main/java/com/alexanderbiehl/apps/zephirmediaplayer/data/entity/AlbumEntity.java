package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity(tableName = "albums")
public class AlbumEntity extends EntityBase {

    @ColumnInfo(name = "title")
    public String title;

    public long albumArtistId;

    @Ignore
    public AlbumEntity(String title, Long artistId) {
        super();
        this.title = title;
        this.albumArtistId = artistId;
    }

    @Ignore
    public AlbumEntity(String title) {
        super();
        this.title = title;
    }

    public AlbumEntity() {
        super();
    }

}
