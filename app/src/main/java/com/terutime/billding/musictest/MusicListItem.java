package com.terutime.billding.musictest;

/**
 * Created by drdc on 2015-07-21.
 */
public class MusicListItem
{
    private long songID;
    private String songArtist;
    private String songTitle;
    private String songData;
    private String songDisplayName;
    private long songDuration;
    private int ratingStars;

    //Constructor for the music object container
    public MusicListItem(long songID, String songArtist, String songTitle, String songData, String songDisplayName, long songDuration)
    {
        this.songID = songID;
        this.songArtist = songArtist;
        this.songTitle = songTitle;
        this.songData = songData;
        this.songDisplayName = songDisplayName;
        this.songDuration = songDuration;
    }

    //Properties for Song ID
    public long getSongID()
    {
        return songID;
    }

    public void setSongID(long songID)
    {
        this.songID = songID;
    }

    //Properties for Song Artist
    public String getSongArtist()
    {
        return songArtist;
    }

    public void setSongArtist(String songArtist)
    {
        this.songArtist = songArtist;
    }

    //Properties for Song Title
    public String getSongTitle()
    {
        return songTitle;
    }

    public void setSongTitle(String songTitle)
    {
        this.songTitle = songTitle;
    }

    //Properties for Song Data
    public String getSongData()
    {
        return songData;
    }

    public void setSongData(String songData)
    {
        this.songData = songData;
    }

    //Properties for Song Display Name
    public String getSongDisplayName()
    {
        return songDisplayName;
    }

    public void setSongDisplayName(String songDisplayName)
    {
        this.songDisplayName = songDisplayName;
    }

    //Properties for Song Duration
    public long getSongDuration()
    {
        return songDuration;
    }

    public void setSongDuration(long songDuration)
    {
        this.songDuration = songDuration;
    }

    //Properties for rating starts
    //Star ratings go from 1-5 with 0 being not rated, and 6 being the Absolute Best of the 5 stars
    public int getRatingStars() { return ratingStars; }

    public void setRatingStars(int ratingStars)
    {
        //Error check to make sure that the numbers don't excced the given range
        if(ratingStars < 0)
            ratingStars = 0;
        if(ratingStars > 6)
            ratingStars = 6;

        this.ratingStars = ratingStars;
    }

}
