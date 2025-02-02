package com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Dao
public interface DoaBase<T extends EntityBase> {

    @Insert
    long insert(T entity);

//    @Insert
//    void insertAll(T... entities);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);

}
