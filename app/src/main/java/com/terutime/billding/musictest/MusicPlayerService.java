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
<<<<<<< HEAD
import java.util.ArrayList;

public class MusicPlayerService extends Service
{
	private static final String ACTION_PLAY = "com.terutime.billding.musicTest.MusicPlayerService";
	MediaPlayer mediaPlayer = null;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private ArrayList<Uri> playList = new ArrayList<Uri>();
	private int currentPosition;
	//Replay current is for replaying the currently playing song
	private boolean replayCurrent = false;
	//Replay is for replaying the current playlist
	private boolean replay = false;
=======
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
>>>>>>> master

	private final class ServiceHandler extends Handler
	{
		public ServiceHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{

<<<<<<< HEAD
		}
	}

	public MusicPlayerService()
	{
	}
=======
        }
    }
    @Override
    public void onCreate()
    {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }*/

    public MusicPlayerService() {}
>>>>>>> master

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		/*HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);*/

<<<<<<< HEAD
		//if(intent.getAction().equals(ACTION_PLAY))
		//{`

		//This listener waits for the asynchronus prepare to finish
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared (MediaPlayer player)
			{
				player.start();
			}
		});

		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() 
		{	
			@Override
			public void onCompletion(MediaPlayer mp) 
			{
				//check if we are replaying the only the current song
				if(replayCurrent)
				{
					mediaPlayer.start();
					return;
				}
				
				//increment the current position of the playlist
				currentPosition++;
				
				//check if the replay playlist option is turned on.
				//If so also check to see if the incremented position exceeds the bounds of the playlist so we can either stop the playback or loop
				if(replay && currentPosition == playList.size())
					currentPosition = 0; //Could change this to currentPosition%playList.size() but I think thats kinda useless and inefficient
				else if (currentPosition == playList.size())
					return;
				
				//reset the media player so we can add a new data source.
				mediaPlayer.reset();
				
				//Set the data source for the next song and play it
				setMpDataSource();
			}
		});
		
		//convert the id we recieved from the intent into a Uri that we can use to set the current song
		long id = intent.getLongExtra("MUSIC_ID", 0);
		Uri contentUri = createUri(id);
		playList.add(contentUri);
		//Current position is 0 becuase we are just starting a new playlist
		currentPosition = 0;

		mediaPlayer = new MediaPlayer();
		//set the media player to stream music
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		setMpDataSource();
		//}
		return START_STICKY;
	}

	//Method to insert a song into the playlist
	public void insertPlaylist(int position, long id)
	{
		playList.add(position, createUri(id));
		if(position <= currentPosition)
		{
			currentPosition++;
		}
	}

	//Method to change the position of any given song in the playlist
	public void changeOrderPlaylist(int position, long id)
	{
		Uri contentUri = createUri(id);
		
		//check to see if the song position being changed is the currently playing song
		boolean sameSong = (playList.get(currentPosition) == contentUri);
		
		playList.remove(playList.indexOf(id));
		playList.add(position, contentUri);
		
		//if the song position being changed is the currently playing song then change the current position to the new position
		if(sameSong)
		{
			currentPosition = position;
		}
		//this case only applies if a) the song being changed is not the currently playing song
		else if(position <= currentPosition)
		{
			currentPosition++;
		}
	}
	
	//method to remove a song from the playlist
	public void removeSongPlaylist(int position)
	{
		playList.remove(position);
		
		//if the position of the song removed is above the current position, we need to update the current position to match
		if(position <= currentPosition)
			currentPosition--;
	}
	
	/*TODO: edit the start playback listing so that if there is no song loaded into the media player (AKA if repeat is off and we reached the end of the playlist)
	 it starts the playlist at the first song.
	 Also change the start song command to include a int position so we can change the currentPosition variable here (Clicked on a song from PlayListFragment
	 */
	
	//call this method if we want to start a new playlist
	//Use Case: i.e. we click on a new item in the list view, we do not want to start another service, but instead we want to change songs/playlists
	public void startNewPlaylist(long id)
	{
		playList = new ArrayList<Uri>();
		playList.add(createUri(id));
		currentPosition = 0;
		setMpDataSource();
	}
	
	//Mehtod to return a uri from the media metadata so we can use it start a song
	public Uri createUri(long id)
	{
		return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
	}
	
	//sets the a new data source for the media player and then asynchronously calls prepare so we can start playing the song
	public void setMpDataSource()
	{
		//load the new music file
		try
		{
			mediaPlayer.setDataSource(getApplicationContext(), playList.get(currentPosition));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		//Prepare the asynchronusly mediaplayer
		mediaPlayer.prepareAsync();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
=======
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
>>>>>>> master
}
