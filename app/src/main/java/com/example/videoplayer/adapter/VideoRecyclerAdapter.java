package com.example.videoplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.R;
import com.example.videoplayer.databinding.LayoutVideoItemBinding;
import com.example.videoplayer.listeners.ItemClickListeners;
import com.example.videoplayer.model.Video;
import com.example.videoplayer.utils.BasicUtils;

import java.util.List;

/**
 * @author Lokesh chennamchetty
 * @date 20/09/2020
 */
public class VideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MainActivity";
    private List<Video> videos;
    private Context context;
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
        LayoutVideoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_video_item, parent, false);
        return new VideoPlayerViewHolder(binding,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VideoPlayerViewHolder)holder).onBind(videos.get(position),itemClickListeners);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        releasePlayer((VideoPlayerViewHolder) holder);
    }

    private void releasePlayer(@NonNull VideoPlayerViewHolder viewHolder) {
        if (viewHolder.getLayoutPosition()!= -1 &&
                videos.get(viewHolder.getLayoutPosition()).getVideoPlayer()!= null) {
            Log.d(TAG, "onViewRecycled: position released"+ viewHolder.getLayoutPosition());
            videos.get(viewHolder.getLayoutPosition()).getVideoPlayer().release();
            videos.get(viewHolder.getLayoutPosition()).setVideoPlayer(null);
        }
    }

    @Override
    public int getItemCount() {
        if (BasicUtils.isNullOrEmpty(videos)) {
            return  0;
        }
        return videos.size();
    }

    @Override
    public long getItemId(int position) {
        return videos.get(position).getId();
    }
}
