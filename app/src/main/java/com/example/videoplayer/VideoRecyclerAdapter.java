package com.example.videoplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.listeners.ItemClickListeners;
import com.example.videoplayer.model.Video;
import com.example.videoplayer.utils.BasicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public class VideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MainActivity";
    List<Video> videos;
    Context context;
    private ItemClickListeners itemClickListeners;

    public VideoRecyclerAdapter(List<Video> videos,Context context, ItemClickListeners itemClickListeners) {
        this.videos = videos;
        this.context = context;
        this.itemClickListeners = itemClickListeners;
    }

    public void updateVideos(List<Video> videos) {
        if (!this.videos.isEmpty()) {
            this.videos.clear();
        }
        this.videos.addAll(videos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_item, parent, false);
        return new VideoPlayerViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VideoPlayerViewHolder)holder).onBind(videos.get(position),itemClickListeners);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        VideoPlayerViewHolder viewHolder = (VideoPlayerViewHolder) holder;
        if (viewHolder.getLayoutPosition()!= -1 &&
                videos.get(viewHolder.getLayoutPosition()).getVideoPlayer()!= null) {
            Log.d(TAG, "onViewRecycled: position released"+ viewHolder.getLayoutPosition());
            videos.get(viewHolder.getLayoutPosition()).getVideoPlayer().release();
            videos.get(viewHolder.getLayoutPosition()).setVideoPlayer(null);
        }
    }

    @Override
    public int getItemCount() {
        if (BasicUtils.isNullorEmpty(videos)) {
            return  0;
        }
        return videos.size();
    }

    @Override
    public long getItemId(int position) {
        return videos.get(position).getId();
    }
}
