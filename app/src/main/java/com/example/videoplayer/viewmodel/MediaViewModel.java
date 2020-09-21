package com.example.videoplayer.viewmodel;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.videoplayer.db.VideoRepository;
import com.example.videoplayer.model.Video;
import com.example.videoplayer.model.VideoResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public class MediaViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivity";
    private MutableLiveData<VideoResponse> videosLiveData = new MutableLiveData<>();
    private VideoRepository repository;
    private CompositeDisposable compositeDisposable;

    public MediaViewModel(@NonNull Application application) {
        super(application);
        repository = new VideoRepository(application);
        compositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<VideoResponse> fetchVideosFromDb() {
        compositeDisposable.add(repository.fetchAllVideosFromDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((videos -> onVideosResponse(new VideoResponse(videos))), this::onErrorResponse));
        return videosLiveData;
    }

    private List<Video> getVideosFromStorage() {
        List<Video> videos = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA};
        Cursor cursor = this.getApplication().getContentResolver().query(uri,
                projection, null, null, BaseColumns._ID + " ASC " + " LIMIT 30");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.d(TAG, "video id -> " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)));
                Video video = new Video(
                        Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID))),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME))
                );
                videos.add(video);
            }
            cursor.close();
        }
        return videos;
    }

    public void loadVideosInDb() {
        compositeDisposable.add(Single.fromCallable(this::getVideosFromStorage).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((videos) -> {
                    if (videos.isEmpty()) {
                        onVideosResponse(new VideoResponse(new Throwable("No videos in Storage")));
                        return;
                    }
                    insertVideoToDb(videos);
                }, this::onErrorResponse));
    }

    private void onVideosResponse(VideoResponse videos) {
        videosLiveData.postValue(videos);
    }

    private void onErrorResponse(Throwable throwable) {
        if (throwable != null) {
            videosLiveData.postValue(new VideoResponse(throwable));
            Log.d(TAG, "onErrorResponse: " + throwable.getMessage());
        }
    }

    private void insertVideoToDb(List<Video> video) {
        compositeDisposable.add(repository.insertAll(video).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "insert successful" + video.size());
                    fetchVideosFromDb();
                }));
    }

    public void updateVideoToBookmarks(Video video) {
        compositeDisposable.add(repository.update(video).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d(TAG, "update successful" + video.isBookMarked())));
    }
}