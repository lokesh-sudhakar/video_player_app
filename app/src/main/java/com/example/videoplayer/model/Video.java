package com.example.videoplayer.model;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.File;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */

@Entity(tableName = "video_table")
public class Video {

    @PrimaryKey
    @OnConflictStrategy
    private long id;

    private String path;

    private String name;

    private boolean isBookMarked = false;

    @Ignore
    private Uri uri;
    @Ignore
    private Bitmap thumbNail;
    @Ignore
    private SimpleExoPlayer videoPlayer;


    public Video(long id, String path, String name) {
        this.path = path;
        this.name = name;
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        if (uri==null) {
            this.uri = Uri.fromFile(new File(path));
        }
        return this.uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(Bitmap thumbNail) {
        this.thumbNail = thumbNail;
    }

    public SimpleExoPlayer getVideoPlayer() {
        return videoPlayer;
    }

    public void setVideoPlayer(SimpleExoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isBookMarked() {
        return isBookMarked;
    }

    public void setBookMarked(boolean bookMarked) {
        isBookMarked = bookMarked;
    }
}
