package com.terutime.billding.musictest;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MusicPlayerService extends Service
{
    private static final String ACTION_PLAY = "com.terutime.billding.musicTest.ACTION_PLAY";
    private static final String UPDATE_TIME = "com.terutime.billding.musictest.UPDATE_TIME";
    private static final String ACTION_PAUSE = "com.terutime.billding.musictest.ACTION_PAUSE";
    private static final int MUSIC_PLAY = 1;
    private static final int MUSIC_PAUSE = 2;

    private NotificationManager mNM;
    private int NOTFICATION = 14352;
    private final IBinder mBinder = new LocalBinder();
    private Callbacks activity;

    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();

    MediaPlayer mediaPlayer = null;

    public class LocalBinder extends Binder
    {
        MusicPlayerService getService()
        {
            return MusicPlayerService.this;
        }
    }

    public interface Callbacks
    {
        public void updateClient(int data);
    }

    /*private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {

        }
    }
    @Override
    public void onCreate()
    {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }*/

    public MusicPlayerService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        /*HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);*/

        //if(intent.getAction().equals(ACTION_PLAY))
        //{`
        if(intent != null)
        {
            preparePlayer(intent);
            //}
            //return stick as we want the service to always be running as long as music is loaded into it
            //chances are the only time this service is not on is when the application has just started
            return START_STICKY;
        }
        else
        {
            return START_NOT_STICKY;
        }
    }

    public void registerClient(Activity activity)
    {
        this.activity = (Callbacks)activity;
    }

    //Method to prepare the media player with a new song.
    public void preparePlayer(Intent intent)
    {
        //create a URI from the id given from an intent
        long id = intent.getLongExtra("MUSIC_ID", 0);
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player)
            {
                startPlayback();
            }
        });

        //Placeholder for now, but will update to handle the media player heading into the Error state
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        //try to load up the data stream for the audio file
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            mediaPlayer.setDataSource(getApplicationContext(), contentUri);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //prepare the media player asynchronously to minimize load on the UI thread
        mediaPlayer.prepareAsync();
    }

    public int startPlayback()
    {
        mediaPlayer.start();
        //LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(ACTION_PLAY));

        //start the timer for keeping track of time duration of the song
        //TODO: Encase this into a if block because we do not want to run this if MusicPlayerFragment is not running.
        //TODO: Create a boolean in that should be triggered during onBackPressed so we can tell the service no to send broadcasts
        /*TODO: There probably should be a method just to run the updateSeekBarTime thread, but such use cases
        have not been determined yet*/
        durationHandler.postDelayed(updateSeekBarTime, 100);
        finalTime = mediaPlayer.getDuration();
        return mediaPlayer.getCurrentPosition();
    }

    //Pauses playback of the song
    public void pausePlayback()
    {
        mediaPlayer.pause();
        //LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(ACTION_PAUSE));
    }

    //returns the current position of the song (what the current time of the song is)
    public int currentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }

    //Skips the song forward a set amount of time
    //TODO: pretty much useless, eventually change this into skip forward to the next song.
    public void skipForward()
    {
        //check if we can go forward at forwardTime seconds before song ends
        if ((timeElapsed + forwardTime) <= finalTime)
        {
            timeElapsed = timeElapsed + forwardTime;
            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    //Jumps to the specific time the user moves to on the seekbar.
    public void seekToTime(int seekTime)
    {
        mediaPlayer.seekTo(seekTime);
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            //listener.onMediaRetrieveTime();
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            //seekbar.setProgress((int) timeElapsed);
            //set time remaing
            //TODO: change the time left stuff to time elapsed. **Look at std music player for example for displaying time
            double timeRemaining = finalTime - timeElapsed;
            String duration = (String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //Broadcast the duration left and the timeElapsed to the MusicPlayerFragment
            Intent intent = new Intent(UPDATE_TIME);
            intent.putExtra("Duration", duration);
            intent.putExtra("TimeElapsed", timeElapsed);
            intent.putExtra("IsPlaying", mediaPlayer.isPlaying());
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    /*public void showNotification()
    {

    }*/


    //Create messenger between activity and this service.
    //Also remember how to do fragment transactions and communication from Activity to Fragment to allow for updating of the music player
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        mNM.cancel(NOTFICATION);

        if(mediaPlayer != null)
            mediaPlayer.release();
    }
}
