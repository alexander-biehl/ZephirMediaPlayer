package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity(tableName = "playlists")
public class Playlist extends EntityBase {

    public String title;
}
