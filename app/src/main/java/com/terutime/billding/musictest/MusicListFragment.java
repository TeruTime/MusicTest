package com.terutime.billding.musictest;

import android.app.Activity;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by drdc on 2015-07-21.
 */
public class MusicListFragment extends Fragment implements AbsListView.OnItemClickListener
{
    private List<MusicListItem> musicList;
    private ListAdapter mAdapter;
    private AbsListView mListView;
    private OnFragmentInteractionListener mListener;
    private HashMap<Long, Bitmap> albumList;

    //Placeholder Interface for now
    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(String id);
        void onListItemClick(MusicListItem item);
    }

    public MusicListFragment()
    {
    }

    public static MusicListFragment newInstance()
    {
        MusicListFragment fragment = new MusicListFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle args)
    {
    	super.onCreate(args);
    	loadMediaList();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_musiclist, container, false);

        mAdapter = new MusicListAdapter(getActivity(), musicList);
        mListView = (AbsListView) rootView.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        return rootView;
    }

    //Attach the interface on activity start
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTabInteractionListener");
        }
    }

    //Make the listener null on detach
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //Retrieve a the musicListItem at the clicked location.
        MusicListItem item = this.musicList.get(position);
        mListener.onListItemClick(item);
    }
    
    public void loadMediaList()
    {
    	//Create selection and projection strings for the database query
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };

        //Create a cursor object to navigate through the database
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        musicList = new ArrayList<MusicListItem>();
        albumList = new HashMap<Long, Bitmap>();

        //Store the retrieved media objects (song data) into an container and place it into an arraylist
        while(cursor.moveToNext())
        {
            MusicListItem song = new MusicListItem(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getLong(6),
                    cursor.getLong(7));

            Long key = Long.valueOf(song.getSongAlbumID());
            if(albumList.containsKey(key))
            	song.setSongAlbumArt(albumList.get(key));
            else
            {
	            Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
	            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, song.getSongAlbumID());
	
	            Bitmap bitmap = null;
	            Bitmap scaledBitmap = null;
	            //use scaled bitmap to display the album art on the listview
	            //Might want to use keep a scaled bitmap, and then completely reload and create a new bitmap for the new music player page.
	            //TODO: dynamically load all of the images that are on the screen at once
	            //Maybe store all of the images into a hashmap with the album id as the key?
	            try
	            {
	                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), albumArtUri);
	                if(bitmap != null)
	                	scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
	            }
	            catch (FileNotFoundException exception)
	            {
	                exception.printStackTrace();
	                //Replace the bitmap with a default audio file picture
	                bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.audio_file);
	                scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
	            }
	            catch (IOException e)
	            {
	                e.printStackTrace();
	            }
	
	            //Add the bitmap to the MusicList item holder
	            song.setSongAlbumArt(scaledBitmap);
	            albumList.put(key, scaledBitmap);
	            	
	            bitmap = null;
            }

            musicList.add(song);
        }
    }
}
