package com.example.videoplayer.utils;

import android.view.View;

/**
 * @author Lokesh chennamchetty
 * @date 21/09/2020
 */
public class ViewUtils {


    private static void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public static void setGone(View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            setVisibility(view, View.GONE);
        }
    }

    public static void setVisible(View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            setVisibility(view, View.VISIBLE);

        }
    }
}
