package com.terutime.billding.musictest;

import java.io.IOException;
import java.util.Locale;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements MusicListFragment.OnFragmentInteractionListener,
        MusicPlayerService.Callbacks, MusicPlayerFragment.OnMusicPlayerListener, PlayListFragment.OnPlayListInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    //SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    //ViewPager mViewPager;

    //Intent to start music player
    private static final String ACTION_PLAY = "com.terutime.billding.musicTest.MusicPlayerService";
    private MusicPlayerService mBoundService;
    private Boolean mIsBound;

    private static final String CURRENT_FRAGMENT = MainActivity.class.getCanonicalName() + ".CURRENT_FRAGMENT";
    private static final String ARG_PARENTS = "Parents";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBaseFragment();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        //mViewPager = (ViewPager) findViewById(R.id.pager);
        //mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed()
    {
    	super.onBackPressed();
    	//currently the action bar is configured to reappear whenever the back button is pressed
    	getSupportActionBar().show();
    }

    //Interfaces for MusicListFragment
    @Override
    public void onFragmentInteraction(String id)
    {

    }

    @Override
    public void onListItemClick(MusicListItem item)
    {
        //Create new fragment to display the music info and album etc...
        //However I would assume that the music player itself would be run on either the main activity
        //of as a separate thread from the UI as we want the ability to start and stop the music player
        //from anywhere in the application. Therefore we must build a service that is spawned off of the prepareAsync() task
        long id = item.getSongID();
        //Intent mIntent = new Intent(ACTION_PLAY);
        if(mBoundService == null)
        {
	        Intent mIntent = new Intent(this, MusicPlayerService.class);
	        mIntent.putExtra("MUSIC_ID", id);
	        startService(mIntent);
	        doBindService();
        }
        else
        {
        	mBoundService.resetMediaPlayer();
        	mBoundService.startNewPlaylist(id);
        }

        //Start up the musicplayer fragment
        MusicPlayerFragment playerFrag = (MusicPlayerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_musicplayer);
        if(playerFrag != null)
        {
            //only call this if there is somehow a multiplane layout
        }
        else
        {
            //initalize the music player with the current player item
            MusicPlayerFragment newFragment = MusicPlayerFragment.newInstance(item);
            
            //TODO: Make it so if the same list item is clicked on, it just loads the fragment with the seek bar in the right place instead of playing the song

            //Replace whatever is in the current fragment view with this new fragment. Then add this
            // fragment to the back stack so the user can navigate back.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.fragment_container,newFragment);
            transaction.addToBackStack(null);
            //commit the transaction
            transaction.commit();
        }
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    //Interface for MusicPlayerFragment
    @Override
    public void onMediaPlayerClick (int id)
    {
        if(mBoundService != null)
        {
            int currentPosition = mBoundService.startPlayback();
            //send message back to the Music Player using a handler

        }
    }

    public void onMediaPlayerPause()
    {
        if(mBoundService != null)
        {
            mBoundService.pausePlayback();
        }
    }

    public void onMediaPlayerFwd()
    {
        if(mBoundService != null)
        {
            mBoundService.skipForward();
        }
    }

    public void onMediaPlayerSeek(int progress)
    {
        if(mBoundService != null)
        {
            mBoundService.seekToTime(progress);
        }
    }

    @Override
    public void onMediaRetrieveTime()
    {
        if(mBoundService != null)
        {
            int currentPosition = mBoundService.currentPosition();
            //send message back to Music Player using a handler

        }
    }

    //interface for the MusicPlayerService
    @Override
    public void updateClient(int data)
    {

    }

    //Methods for creating connections between this activity and the MusicPlayerService
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            //This is called when the connection with the service has been established, giving us
            // the service object we can use to interact with the service. Because we have bound to
            // a explicit service that we know is running in our own process, we can cast its IBinder
            // to a concrete class and directly access it.
            mBoundService = ((MusicPlayerService.LocalBinder)service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            //This is called when the connection with the service has been unexpectedly disconnected
            // -- that is, its process crashed. Because it is running in our same process, we should
            // never see this happen
            mBoundService = null;
        }
    };

    void doBindService()
    {
        //Establish a connection with the service. We use an explicit class name because we want a
        // specific service implementation that we know will be running in our own process (and thus
        // won't be supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this, MusicPlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            //Detach our existing connection.
            unbindService(mConnection);
            stopService(new Intent(MainActivity.this, MusicPlayerService.class));
            mIsBound = false;
        }
    }

    public void goInto(String hostingLevel, String position)
    {
        Fragment hostingFragment = newHostingFragment(hostingLevel, position);
        addFragment(hostingFragment);
    }

    private void addBaseFragment()
    {
        Fragment hostingFragment = newHostingFragment("","");
        addFragment(hostingFragment);
    }

    private Fragment newHostingFragment (String hostingLevel, String oldPosition)
    {
        Fragment hostingFragment = new TabViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARENTS, hostingLevel + oldPosition+" > ");
        hostingFragment.setArguments(args);
        return hostingFragment;
    }

    private void addFragment(Fragment hostingFragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, hostingFragment, CURRENT_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    //This method starts the playback of the selected song from the playlist fragment
    @Override
	public void onPlayListItemClick(MusicListItem item, int position) 
    {
    	mBoundService.resetMediaPlayer();
    	mBoundService.setMpDataSource(position);
    	//TODO: Can't implement this until I implement all of the upstream changes I did to communicate between the service, fragments and shit
	}


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    /*public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0)
            {
                return MusicListFragment.newInstance();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }*/
}
