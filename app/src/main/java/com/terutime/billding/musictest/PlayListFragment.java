package com.terutime.billding.musictest;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class PlayListFragment extends Fragment implements AbsListView.OnItemClickListener
{
	 private List<MusicListItem> musicList;
	    private ListAdapter mAdapter;
	    private AbsListView mListView;
	    private OnPlayListInteractionListener mListener;
	    
	    public interface OnPlayListInteractionListener
	    {
	    	//insert functions
	    	public void onPlayListItemClick(MusicListItem item, int position);
	    }
	    
	    public static PlayListFragment newInstance(List<MusicListItem> musiclist)
	    {
	    	PlayListFragment fragment = new PlayListFragment();
	    	fragment.musicList = musiclist; 
			return fragment;
	    }
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState)
	    {
			//TODO: Remake fragment to include a couple more options, etc etc
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
	        try {
	            mListener = (OnPlayListInteractionListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnPlayListInteractionListener");
	        }
	    }

	    //Make the listener null on detach
	    @Override
	    public void onDetach() {
	        super.onDetach();
	        mListener = null;
	    }
	    
	    @Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
	    	MusicListItem item = this.musicList.get(position);
	        mListener.onPlayListItemClick(item, position);
		}

}
