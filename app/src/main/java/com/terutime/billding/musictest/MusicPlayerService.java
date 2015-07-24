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

import java.io.IOException;

public class MusicPlayerService extends Service
{
    private static final String ACTION_PLAY = "com.terutime.billding.musicTest.MusicPlayerService";
    private static final int MUSIC_PLAY = 1;
    private static final int MUSIC_PAUSE = 2;

    private NotificationManager mNM;
    private int NOTFICATION = 14352;
    private final IBinder mBinder = new LocalBinder();
    private Callbacks activity;

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
            long id = intent.getLongExtra("MUSIC_ID", 0);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }
            });

            //Placeholder for now, but will update to handle the media player heading into the Error state
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try
            {
                mediaPlayer.setDataSource(getApplicationContext(), contentUri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            mediaPlayer.prepareAsync();
            //}
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

    public int startPlayback()
    {
        mediaPlayer.start();
        return mediaPlayer.getCurrentPosition();
    }



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
