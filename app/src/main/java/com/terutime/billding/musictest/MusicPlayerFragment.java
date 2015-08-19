package com.terutime.billding.musictest;

/**
 * Created by drdc on 2015-07-22.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class MusicPlayerFragment extends Fragment
{
    //private MediaPlayer mediaPlayer;
    public TextView songName, duration, songAlbum;
    public MusicListItem musicListItem;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    //private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private OnMusicPlayerListener listener;
    //specific pause button replaced with a start/stop button
    //private ImageButton pauseButton;
    private ImageButton playButton;
    private ImageButton fwdButton;
    private SquareImageView albumCover;

    //variables to store the size of the album art imageview
    //The default size of the image view is set at 200 by 200
    private int albumCoverHeight = 200;
    private int albumCoverWidth = 200;

    public PlayerReceiver playerReceiver = new PlayerReceiver();

    private static final int CURRENT_TIME = 1101;
    private static final String ACTION_PLAY = "com.terutime.billding.musicTest.ACTION_PLAY";
    private static final String UPDATE_TIME = "com.terutime.billding.musictest.UPDATE_TIME";
    private static final String ACTION_PAUSE = "com.terutime.billding.musictest.ACTION_PAUSE";
    private boolean isPlaying = false;

    public interface OnMusicPlayerListener
    {
        void onMediaPlayerClick(int id);
        void onMediaRetrieveTime();
        void onMediaPlayerPause();
        void onMediaPlayerFwd();
        void onMediaPlayerSeek(int progress);
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
        songAlbum = (TextView) rootView.findViewById(R.id.songAlbum);
        //mediaPlayer = MediaPlayer.create(this, R.raw.sample_song);
        finalTime = musicListItem.getSongDuration();
        duration = (TextView) rootView.findViewById(R.id.songDuration);
        seekbar = (SeekBar) rootView.findViewById(R.id.seekBar);
        songName.setText(musicListItem.getSongTitle());
        songAlbum.setText(musicListItem.getSongAlbum());
        //pauseButton = (ImageButton) rootView.findViewById(R.id.media_pause);
        playButton = (ImageButton) rootView.findViewById(R.id.media_play);
        fwdButton = (ImageButton) rootView.findViewById(R.id.media_ff);
        albumCover = (SquareImageView) rootView.findViewById(R.id.mp3Image);

        //Use a view tree observer to obtain the size of the image view dynamically
        /*ViewTreeObserver viewTreeObserver = albumCover.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                albumCover.getViewTreeObserver().removeOnPreDrawListener(this);
                albumCoverHeight = album
                return true;
            }
        });*/

        //TODO:change image size of the album art to be the size of the album cover size
        //albumCover.setImageBitmap(Bitmap.createScaledBitmap(musicListItem.getSongAlbumArt(), 200, 200, false));
        //albumCover.setImageBitmap(musicListItem.getSongAlbumArt());
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri, musicListItem.getSongAlbumID());
        try 
        {
			albumCover.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), albumArtUri));
		} 
        catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			albumCover.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.audio_file));
		} 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        seekbar.setMax((int) finalTime);
        //seekbar.setClickable(false);

        //Set the methods for changing the position of seekbar to seek through a song.
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                double timeRemaining = finalTime - progress;
                String durationTime = (String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
                duration.setText(durationTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //stop the music from playing when we are seeking to a specific time.
                listener.onMediaPlayerPause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.onMediaPlayerSeek(progress);
                //boolean check to see if the player is paused or not when seeking. If paused, keep paused
                if(isPlaying)
                    listener.onMediaPlayerClick(0);
            }
        });

        /*pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMediaPlayerPause();
            }
        });*/

        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isPlaying)
                    listener.onMediaPlayerClick(1);
                else
                    listener.onMediaPlayerPause();
            }
        });

        fwdButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onMediaPlayerFwd();
            }
        });

        //For now lets set the receiver to be local so that we can only send it messages from inside this application.
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(playerReceiver, createFilter());

        return rootView;
    }

    @Override
    public void onResume()
    {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(playerReceiver, createFilter());
        super.onResume();
    }

    @Override
    public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(playerReceiver);
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Register a listener with the activity that instantiated the fragment
        try
        {
            listener = (OnMusicPlayerListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMusicPlayerListener");
        }
    }

    //Make the listener null on detach
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //Implement a broadcast receiver
    public class PlayerReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(UPDATE_TIME))
            {
                //update the timer on the screen
                //currently the time is set to time remaining
                timeElapsed = intent.getDoubleExtra("TimeElapsed", 0);
                String timeLeft = intent.getStringExtra("Duration");
                seekbar.setProgress((int) timeElapsed);
                duration.setText(timeLeft);

                //Checks to see if the playback status of the music service is the same as the one
                // recorded in this fragment. If it isn't, change the boolean, and then change the icon for play/stop button
                boolean currentPlayStatus = intent.getBooleanExtra("IsPlaying", false);
                if(currentPlayStatus != isPlaying)
                {
                    isPlaying = currentPlayStatus;
                    if(currentPlayStatus)
                        playButton.setImageResource(R.drawable.ic_media_pause);
                    else
                        playButton.setImageResource(R.drawable.ic_media_play);
                }
            }
            /*else if(intent.getAction().equals(ACTION_PLAY))
            {
                playButton.setImageResource(R.drawable.ic_media_pause);
                isPlaying = true;
            }
            else if(intent.getAction().equals(ACTION_PAUSE))
            {
                playButton.setImageResource(R.drawable.ic_media_play);
                isPlaying = false;
            }*/
        }
    }

    //method to create an intent filter that is used for the Player Receiver
    public IntentFilter createFilter()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_TIME);

        return filter;
    }

    /*public static class DurationHandler extends Handler
    {
        private final WeakReference<MusicPlayerFragment> mFragment;

        public DurationHandler(MusicPlayerFragment fragment)
        {
            mFragment = new WeakReference<MusicPlayerFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MusicPlayerFragment fragment = mFragment.get();
            if(fragment != null) {
                if (msg.what == CURRENT_TIME)
                {
                    //run the update seekbar time method
                    timeElapsed = (double)msg.obj;
                    seekbar.setProgress((int) timeElapsed);
                    double timeRemaining = finalTime - timeElapsed;
                    duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                            TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
                }
            }
        }
    }

    private final DurationHandler durationHandler = new DurationHandler(this);

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            listener.onMediaRetrieveTime();
            //timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            //seekbar.setProgress((int) timeElapsed);
            //set time remaing
            //double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };*/
}