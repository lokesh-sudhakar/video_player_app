package com.example.videoplayer.model;

import java.util.List;

/**
 * @author Lokesh chennamchetty
 * @date 22/09/2020
 */
public class VideoResponse {

    private List<Video> videos;
    private Throwable error;


    public VideoResponse(List<Video> videos) {
        this.videos = videos;
    }

    public VideoResponse(Throwable error) {
        this.error = error;
    }


    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Throwable getError() {
        return error;
    }
}
