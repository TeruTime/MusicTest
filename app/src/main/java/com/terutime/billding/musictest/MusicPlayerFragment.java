package com.terutime.billding.musictest;

/**
 * Created by drdc on 2015-07-22.
 */

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerFragment extends Fragment
{
    //private MediaPlayer mediaPlayer;
    public TextView songName, duration;
    public MusicListItem musicListItem;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;

    public interface OnMusicPlayerListener
    {
        public void onMediaPlayerClick(int id);
    }

    public static MusicPlayerFragment newInstance(MusicListItem musicListItem)
    {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        fragment.musicListItem = musicListItem;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_musicplayer, container, false);

        songName = (TextView) rootView.findViewById(R.id.songName);
        //mediaPlayer = MediaPlayer.create(this, R.raw.sample_song);
        finalTime = musicListItem.getSongDuration();
        duration = (TextView) rootView.findViewById(R.id.songDuration);
        seekbar = (SeekBar) rootView.findViewById(R.id.seekBar);
        songName.setText(musicListItem.getSongTitle());

        seekbar.setMax((int) finalTime);
        seekbar.setClickable(false);

        return rootView;
    }
}