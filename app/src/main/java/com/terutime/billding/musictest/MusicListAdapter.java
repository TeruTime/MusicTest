package com.terutime.billding.musictest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by drdc on 2015-07-21.
 */

public class MusicListAdapter extends ArrayAdapter
{
    private Context context;
    private boolean useList = true;

    public MusicListAdapter (Context context, List items)
    {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    //Holder for the list items
    private class ViewHolder
    {
        TextView titleText;
        TextView artistText;
        TextView ratingStarText;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        MusicListItem item = (MusicListItem)getItem(position);
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
        {
            viewToUse = mInflater.inflate(R.layout.musiclist_item, null);

            //Create a new View holder
            holder = new ViewHolder();
            //map the textviews in view holder to the text fields inside of the layout files
            holder.titleText = (TextView)viewToUse.findViewById(R.id.songTitle);
            holder.artistText = (TextView)viewToUse.findViewById(R.id.songArtist);
            holder.ratingStarText = (TextView)viewToUse.findViewById(R.id.ratingStars);
            viewToUse.setTag(holder);
        }
        else
        {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.titleText.setText(item.getSongTitle());
        holder.artistText.setText(item.getSongArtist());
        holder.ratingStarText.setText("1");

        //This line is used to trigger the horizontal scrolling action
        holder.titleText.setSelected(true);
        return viewToUse;
    }
}
