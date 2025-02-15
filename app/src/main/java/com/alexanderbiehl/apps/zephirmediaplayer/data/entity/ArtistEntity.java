package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity(tableName = "artists")
public class ArtistEntity extends EntityBase {
    @ColumnInfo(name = "title")
    public String title;

    @Ignore
    public ArtistEntity(String artist, String mediaId) {
        super();
        this.title = artist;
        this.mediaId = mediaId;
    }

    public ArtistEntity() {
        super();
    }
}
