package com.example.videoplayer.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.databinding.LayoutVideoItemBinding;
import com.example.videoplayer.listeners.ItemClickListeners;
import com.example.videoplayer.model.Video;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public class VideoPlayerViewHolder extends RecyclerView.ViewHolder  {

    private Context context;
    private Video mediaObject;
    private LayoutVideoItemBinding binding;



    public VideoPlayerViewHolder(@NonNull LayoutVideoItemBinding binding, Context context) {
        super(binding.getRoot());
        this.binding = binding;
        this.context = context;
    }

    public void onBind(Video mediaObject, ItemClickListeners itemClickListeners) {
        this.mediaObject = mediaObject;
        initializePlayer();
        binding.setMediaObject(this.mediaObject);
        binding.setListener(itemClickListeners);
        binding.executePendingBindings();
    }

    private void initializePlayer() {
        if (mediaObject.getVideoPlayer()== null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            mediaObject.setVideoPlayer(ExoPlayerFactory.newSimpleInstance(context, trackSelector));
            buildMediaSource();
        }
        mediaObject.getVideoPlayer().setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
    }

    private void buildMediaSource() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "RecyclerView VideoPlayer"));
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .createMediaSource(mediaObject.getUri());
        mediaObject.getVideoPlayer().prepare(mediaSource);
    }

}
