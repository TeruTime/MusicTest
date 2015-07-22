package com.terutime.billding.musictest;

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
    MediaPlayer mediaPlayer = null;
    private Looper mServiceLooper;
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

    public MusicPlayerService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        /*HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);*/

        //if(intent.getAction().equals(ACTION_PLAY))
        //{`
        long id = intent.getLongExtra("MUSIC_ID", 0);
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

        MediaPlayer mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared (MediaPlayer player)
            {
                player.start();
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
