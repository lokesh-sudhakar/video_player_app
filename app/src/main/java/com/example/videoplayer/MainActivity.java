package com.example.videoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.videoplayer.databinding.ActivityMainBinding;
import com.example.videoplayer.listeners.ItemClickListeners;
import com.example.videoplayer.model.Video;
import com.example.videoplayer.utils.BasicUtils;
import com.example.videoplayer.viewmodel.MediaViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ItemClickListeners {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<Video> videoDataList = new ArrayList<>();
    private PagerSnapHelper snapHelper;
    private VideoRecyclerAdapter adapter;
    private int currentPlayingItemPos = 0;
    private boolean isInitialized = false;
    private MediaViewModel viewModel;
    private ActivityMainBinding binding;
    private boolean videoState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        initViews();
    }

    private void initViews() {
        binding.recyclerView.setVisibility(View.GONE);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerView);
        initRecyclerView();
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.fetchVideosFromDb().observe(this, videos -> {
            if (BasicUtils.isNullorEmpty(videos)) {
                if(isExternalStoragePermissionGranted()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    viewModel.loadVideosInDb();
                }else {
                    binding.progressBar.setVisibility(View.GONE);
                    requestPermission();
                }
            } else {
                binding.progressBar.setVisibility(View.GONE);
                adapter.updateVideos(videos);
                recyclerScrollListener();
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void recyclerScrollListener() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    adapter.notifyDataSetChanged();
                    isInitialized = true;
                }

            }
        });
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
        if (currentPlayingItemPos != -1 && videoDataList.get(currentPlayingItemPos).getVideoPlayer()!= null) {
            videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(false);
            adapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        adapter = new VideoRecyclerAdapter(videoDataList,getApplicationContext(),this);
        adapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
        RecyclerView.ItemAnimator animator = binding.recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private boolean isExternalStoragePermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    viewModel.loadVideosInDb();
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Permission Denied, You cannot use local drive .",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: all video released");
        if (!BasicUtils.isNullorEmpty(videoDataList)) {
            for (Video video : videoDataList) {
                if (video.getVideoPlayer() != null) {
                    video.getVideoPlayer().release();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BasicUtils.isNullorEmpty(videoDataList)) {
            if (currentPlayingItemPos != -1 && videoDataList.get(currentPlayingItemPos).getVideoPlayer() != null) {
                videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(videoState);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!BasicUtils.isNullorEmpty(videoDataList)) {
            if (currentPlayingItemPos != -1 && videoDataList.get(currentPlayingItemPos).getVideoPlayer() != null) {
                videoState = videoDataList.get(currentPlayingItemPos).getVideoPlayer().getPlayWhenReady();
                videoDataList.get(currentPlayingItemPos).getVideoPlayer().setPlayWhenReady(false);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void itemClick(Video mediaData) {
        mediaData.getVideoPlayer()
                .setPlayWhenReady(!mediaData.getVideoPlayer().getPlayWhenReady());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBookMarkClick(Video mediaData) {
        mediaData.setBookMarked(!mediaData.isBookMarked());
        viewModel.updateVideoToBookmarks(mediaData);
        adapter.notifyDataSetChanged();
    }


}
