/*
* Assignment 5
* TrackResults.Java
* Prabhakar Seeda Teja and Yash Ghia
* */

package com.example.teja.homework06;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by teja on 10/10/17.
 */

public class TrackResults implements Serializable {
    String trackName;
    String artist;
    String trackUrl;
    String smallImageUrl;
    String largeImageUrl;
    Boolean favorite;

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    static public TrackResults createResults(JSONObject jsonObject) throws JSONException {
        TrackResults trackObject = new TrackResults();
        trackObject.setTrackName(jsonObject.getString("name"));
        trackObject.setArtist(jsonObject.getString("artist"));
        trackObject.setTrackUrl(jsonObject.getString("url"));
        JSONArray imageUrlArray = jsonObject.getJSONArray("image");
        JSONObject smallImageObject = imageUrlArray.getJSONObject(0);
        trackObject.setSmallImageUrl(smallImageObject.getString("#text"));
        JSONObject largeImageObject = imageUrlArray.getJSONObject(2);
        trackObject.setLargeImageUrl(largeImageObject.getString("#text"));
        trackObject.setFavorite(false);
        return trackObject;
    }
    static public TrackResults createSimilarResults(JSONObject jsonObject) throws JSONException {
        TrackResults trackObject = new TrackResults();
        trackObject.setTrackName(jsonObject.getString("name"));
        JSONObject artistObject = jsonObject.getJSONObject("artist");
        trackObject.setArtist(artistObject.getString("name"));
        trackObject.setTrackUrl(artistObject.getString("url"));
        JSONArray imageUrlArray = jsonObject.getJSONArray("image");
        JSONObject smallImageObject = imageUrlArray.getJSONObject(0);
        trackObject.setSmallImageUrl(smallImageObject.getString("#text"));
        JSONObject largeImageObject = imageUrlArray.getJSONObject(2);
        trackObject.setLargeImageUrl(largeImageObject.getString("#text"));
        trackObject.setFavorite(false);
        return trackObject;
    }
    public String getArtist() {
        return artist;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public String getTrackName() {

        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }
}
