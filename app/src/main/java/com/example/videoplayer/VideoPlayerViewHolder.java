package com.example.videoplayer;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.exoplayer2.ui.PlayerView;
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

    private PlayerView playerView;
    private Context context;
    private Video mediaObject;
    private ItemClickListeners itemClickListeners;
    private ImageView bookmarkBtn;
    private ImageView playImageView;
    private TextView textView;



    public VideoPlayerViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        playerView = itemView.findViewById(R.id.video_view);
        bookmarkBtn = itemView.findViewById(R.id.bookmarkBtn);
        playImageView = itemView.findViewById(R.id.playBtn);
        textView = itemView.findViewById(R.id.title);
    }

    public void onBind(Video mediaObject, ItemClickListeners itemClickListeners) {
        this.mediaObject = mediaObject;
        this.itemClickListeners = itemClickListeners;
        textView.setText(mediaObject.getName());
        initializePlayer();
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
        playerView.setPlayer(mediaObject.getVideoPlayer());
        playerView.setUseController(false);
        if (mediaObject.getVideoPlayer().getPlayWhenReady()) {
            playImageView.setVisibility(View.GONE);
        } else {
            playImageView.setVisibility(View.VISIBLE);
        }
        if (mediaObject.isBookMarked()) {
            bookmarkBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_select));
        } else {
            bookmarkBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_unselect));
        }
        bookmarkBtn.setOnClickListener(view -> itemClickListeners.onBookMarkClick(mediaObject));
        itemView.setOnClickListener(view -> itemClickListeners.itemClick(mediaObject));
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
