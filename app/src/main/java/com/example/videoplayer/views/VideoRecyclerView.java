package com.example.videoplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.videoplayer.model.Video;
import com.example.videoplayer.utils.BasicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public class VideoRecyclerView extends RecyclerView {

    private static final String TAG = "MainActivity";
    private int currentPlayingItemPos = 0;
    private Adapter adapter;
    private List<Video> videoDataList = new ArrayList<>();
    private boolean videoState;
    private boolean isInitialized = false;


    public VideoRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {

    }

    public void attach(List<Video> videos) {
        this.videoDataList = videos;
        attachPageSnapHelper();
        disableChangeAnimations();
        addScrollListener();
    }

    private void attachPageSnapHelper() {
        PagerSnapHelper snapHelper  = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    private void addScrollListener() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int currentVisibleItemPos;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null) {
                        throw new NullPointerException("Linear layout manager not attached to recycler view");
                    }
                    currentVisibleItemPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                    pauseExistingPlayerAndPlayCurrentPlayer(currentVisibleItemPos);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isInitialized) {
                    videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(true);
                    post(() -> adapter.notifyDataSetChanged());
                    isInitialized = true;
                }
            }
        });
    }

    private void disableChangeAnimations() {
        ItemAnimator animator = getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void pauseExistingPlayerAndPlayCurrentPlayer(int currentVisibleItemPos) {
        Log.d(TAG, "pauseExistingPlayerAndPlayCurrentPlayer: payingPos:"
                + currentPlayingItemPos + "visiblePos:" + currentVisibleItemPos);
        if (currentVisibleItemPos != -1) {
            releaseCurrentPlayingVideo();
            videoDataList.get(currentVisibleItemPos).getVideoPlayer().setPlayWhenReady(true);
            adapter.notifyDataSetChanged();
            currentPlayingItemPos = currentVisibleItemPos;
        }

    }

    private void releaseCurrentPlayingVideo() {
        if (!BasicUtils.isNullOrEmpty(videoDataList)) {
            if (currentPlayingItemPos != -1 && videoDataList.get(currentPlayingItemPos).getVideoPlayer()!= null) {
                videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(false);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
    }

    public void updateVideos(List<Video> videos){
        if (!this.videoDataList.isEmpty()) {
            this.videoDataList.clear();
        }
        this.videoDataList.addAll(videos);
    }

    public void pauseCurrentVideo(){
        if (!BasicUtils.isNullOrEmpty(videoDataList)) {
            if (currentPlayingItemPos != -1 && videoDataList.get(currentPlayingItemPos).getVideoPlayer() != null) {
                videoState = videoDataList.get(currentPlayingItemPos).getVideoPlayer().getPlayWhenReady();
                videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(false);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "pauseCurrentVideo");
            }
        }
    }

    public void restorePreviousPlayState() {
        if (!BasicUtils.isNullOrEmpty(videoDataList)) {
            if (currentPlayingItemPos != -1 && videoDataList.get(currentPlayingItemPos).getVideoPlayer() != null) {
                videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(videoState);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "restorePreviousPlayState");
            }
        }
    }

    public void releaseAllPlayers(){
        if (!BasicUtils.isNullOrEmpty(videoDataList)) {
            for (Video video : videoDataList) {
                if (video.getVideoPlayer() != null) {
                    video.getVideoPlayer().release();
                }
            }
        }
        Log.d(TAG, "releaseAllPlayers: all video released");
    }
}
