package com.example.videoplayer.listeners;

import com.example.videoplayer.model.Video;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public interface ItemClickListeners {

    void itemClick(Video mediaData);

    void onBookMarkClick(Video mediaData);
}
