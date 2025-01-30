package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity
public class Album extends EntityBase {

    @ColumnInfo(name = "title")
    public String title;

    public long albumArtistId;

}
