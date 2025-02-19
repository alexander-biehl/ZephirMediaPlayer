package com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Dao
public interface DoaBase<T extends EntityBase> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(T entity);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);

}
