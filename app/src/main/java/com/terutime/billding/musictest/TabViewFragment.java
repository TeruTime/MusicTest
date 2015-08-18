package com.terutime.billding.musictest;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTabInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabViewFragment extends Fragment {

    private String mParenString;

    private OnTabInteractionListener mListener;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTabInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // TODO: Rename and change types and number of parameters
    public static TabViewFragment newInstance(String param1, String param2) {
        TabViewFragment fragment = new TabViewFragment();
        return fragment;
    }

    public TabViewFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_view, container, false);
    }

    @Override
    public void onResume()
    {
        ViewPager viewPager = (ViewPager)getView().findViewById(R.id.pager);
        viewPager.setAdapter(new SimpleFragmentStatePageAdapter(getChildFragmentManager(), mParenString));
        super.onResume();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnTabInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTabInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private static class SimpleFragmentStatePageAdapter extends FragmentStatePagerAdapter
    {
        private String hostingLevel;

        public SimpleFragmentStatePageAdapter(android.support.v4.app.FragmentManager fm, String hostingLevel)
        {
            super(fm);
            this.hostingLevel = hostingLevel;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            if(position == 0)
            {
                return MusicListFragment.newInstance();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }
    }
}
