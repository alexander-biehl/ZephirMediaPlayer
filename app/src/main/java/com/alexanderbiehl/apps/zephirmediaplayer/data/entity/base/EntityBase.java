package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public abstract class EntityBase {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "media_id")
    public String mediaId;
}
