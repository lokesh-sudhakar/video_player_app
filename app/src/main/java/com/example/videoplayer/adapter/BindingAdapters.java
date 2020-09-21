package com.example.videoplayer.adapter;

import androidx.databinding.BindingAdapter;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * @author Lokesh chennamchetty
 * @date 21/09/2020
 */
public class BindingAdapters {

    @BindingAdapter({"android:setPlayer"})
    public static void setPlayer(PlayerView view, SimpleExoPlayer player) {
        view.setPlayer(player);
    }
}
