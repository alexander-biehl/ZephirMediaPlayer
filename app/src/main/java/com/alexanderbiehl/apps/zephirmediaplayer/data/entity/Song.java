package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity
public class Song extends EntityBase {

    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "track_number")
    public String trackNumber;
    @ColumnInfo(name = "source_uri")
    public String sourceUri;

    public long songArtistId;
    public long songAlbumId;
}
