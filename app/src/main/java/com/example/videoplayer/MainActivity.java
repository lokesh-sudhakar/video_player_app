package com.example.videoplayer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.videoplayer.databinding.ActivityMainBinding;
import com.example.videoplayer.listeners.ItemClickListeners;
import com.example.videoplayer.model.Video;
import com.example.videoplayer.utils.BasicUtils;
import com.example.videoplayer.utils.ViewUtils;
import com.example.videoplayer.viewmodel.MediaViewModel;
import com.example.videoplayer.adapter.VideoRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListeners {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<Video> videoDataList = new ArrayList<>();
    private VideoRecyclerAdapter adapter;
    private MediaViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        initViews();
    }

    private void initViews() {
        initRecyclerView();
        listenToVideoDataFromDb();
        ViewUtils.setGone(binding.recyclerView);
        ViewUtils.setVisible(binding.progressBar);
    }

    private void listenToVideoDataFromDb() {
        viewModel.fetchVideosFromDb().observe(this, videoResponse -> {
            if (videoResponse.getError()!= null) {
                Toast.makeText(this, videoResponse.getError().getMessage(),Toast.LENGTH_SHORT).show();
                ViewUtils.setGone(binding.progressBar);
                return;
            }
            if (BasicUtils.isNullOrEmpty(videoResponse.getVideos())) {
                if(isExternalStoragePermissionGranted()) {
                    ViewUtils.setVisible(binding.progressBar);
                    viewModel.loadVideosInDb();
                }else {
                    ViewUtils.setGone(binding.progressBar);
                    requestPermission();
                }
            } else {
                ViewUtils.setGone(binding.progressBar);
                ViewUtils.setVisible(binding.recyclerView);
                adapter.updateVideos(videoResponse.getVideos());
                binding.recyclerView.updateVideos(videoResponse.getVideos());
            }
        });
    }

    private void initRecyclerView() {
        adapter = new VideoRecyclerAdapter(videoDataList,getApplicationContext(),this);
        adapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.attach(videoDataList);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
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
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
                    Toast.makeText(this, "Permission Denied, You will not be able to see videos from local storage .",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.recyclerView.restorePreviousPlayState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.recyclerView.pauseCurrentVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.recyclerView.releaseAllPlayers();
    }

    @Override
    public void itemClick(Video mediaData) {
        mediaData.getVideoPlayer()
                .setPlayWhenReady(!mediaData.getVideoPlayer().getPlayWhenReady());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBookMarkClick(Video mediaData) {
        if (mediaData.isBookMarked()) {
            Toast.makeText(this, "Removed from bookmark",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Added to bookmark",Toast.LENGTH_SHORT).show();
        }
        mediaData.setBookMarked(!mediaData.isBookMarked());
        viewModel.updateVideoToBookmarks(mediaData);
        adapter.notifyDataSetChanged();
    }
}
