package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.pratham.foundation.database.domain.ContentProgress;
import java.util.List;


@Dao
public interface ContentProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ContentProgress contentProgress);

    @Query("select * from ContentProgress where studentId=:currentStudentID AND resourceId=:resourceId AND label=:label")
    List<ContentProgress> getContentNodeProgress(String currentStudentID, String resourceId, String label);

    @Query("select * from ContentProgress where sentFlag=0")
    List<ContentProgress> getAllContentNodeProgress();

    @Query("update ContentProgress set sentFlag=1 where sentFlag=0")
    public void setSentFlag();

    @Insert
    public void addContentProgressList(List<ContentProgress> contentProgressList);

    @Query("select * from ContentProgress where studentId=:currentStudentID AND resourceId=:resourceId AND label=:resourceProgress")
    List<ContentProgress> getProgressByStudIDAndResID(String currentStudentID, String resourceId, String resourceProgress);
}