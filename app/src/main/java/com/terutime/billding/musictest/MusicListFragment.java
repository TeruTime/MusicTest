package com.terutime.billding.musictest;

import android.app.Activity;
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


import java.util.ArrayList;
import java.util.List;

/**
 * Created by drdc on 2015-07-21.
 */
public class MusicListFragment extends Fragment implements AbsListView.OnItemClickListener
{
    private List<MusicListItem> musicList;
    private ListAdapter mAdapter;
    private AbsListView mListView;
    private OnFragmentInteractionListener mListener;

    //Placeholder Interface for now
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
        public void onListItemClick(MusicListItem item);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_musiclist, container, false);

        //Create selection and projection strings for the database query
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        //Create a cursor object to navigate through the database
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        musicList = new ArrayList<MusicListItem>();

        //Store the retrieved media objects (song data) into an container and place it into an arraylist
        while(cursor.moveToNext())
        {
            MusicListItem song = new MusicListItem(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getLong(5));
            musicList.add(song);
        }

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
}
