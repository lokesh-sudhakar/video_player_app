package com.example.videoplayer.db;

import android.app.Application;

import com.example.videoplayer.model.Video;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public class VideoRepository {

    private VideoDao videoDao;

    public VideoRepository(Application application) {
        VideoDatabase db = VideoDatabase.getDatabase(application);
        videoDao = db.videoDao();
    }

    public Single<List<Video>> fetchAllVideosFromDb() {
        return videoDao.getAllVideos();
    }

    public Completable insert(Video video) {
        return videoDao.insert(video);
    }

    public Completable update(Video video) {
        return videoDao.updateVideo(video);
    }
}
