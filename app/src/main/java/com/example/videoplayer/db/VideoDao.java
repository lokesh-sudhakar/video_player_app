package com.example.videoplayer.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.videoplayer.model.Video;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */

@Dao
public interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Video video);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Video> videos);

    @Update
    Completable updateVideo(Video video);


    @Query("SELECT * from video_table")
    Single<List<Video>> getAllVideos();

}
