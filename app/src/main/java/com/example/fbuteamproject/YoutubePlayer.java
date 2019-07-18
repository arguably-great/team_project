package com.example.fbuteamproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayer extends YouTubePlayerFragment {

    private YouTubePlayerView youTubePlayerView;

    public void onCreate(Bundle var1) {
        super.onCreate(var1);
    }

    @Override
    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        youTubePlayerView = (YouTubePlayerView)super.onCreateView(var1,var2,var3);
        return youTubePlayerView;
    }

    public YouTubePlayerView getYouTubePlayerView() {
        return youTubePlayerView;
    }
}
