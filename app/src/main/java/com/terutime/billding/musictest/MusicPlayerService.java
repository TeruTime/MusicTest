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
}
