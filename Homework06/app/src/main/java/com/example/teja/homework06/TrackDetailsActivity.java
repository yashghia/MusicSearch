/*
* Assignment 5
* TrackDetailsActivity.Java
* Prabhakar Seeda Teja and Yash Ghia
* */

package com.example.teja.homework06;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class TrackDetailsActivity extends AppCompatActivity {
    ListView similarListData;
    String trackName, artistName;
    public List<TrackResults> trackResultsList = new ArrayList<TrackResults>();
    public final static String TRACK_DETAILS = "trackdetails";
    public final static String TRACK_ARRAY = "similartrack";
    TrackResults tracker;
    public List<TrackResults> trackFavoriteList = new ArrayList<TrackResults>();
    String trackURL = "https://ws.audioscrobbler.com/2.0/?format=json&limit=10&method=track.getsimilar&api_key=bdebb829c13038655ef949113ad7daab";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        setTitle("Track Details");
        trackResultsList = (List<TrackResults>) getIntent().getExtras().getSerializable(ResultsActivity.TRACK_ARRAY);
        TrackResults trackDetails = (TrackResults) getIntent().getExtras().getSerializable(ResultsActivity.TRACK_DETAILS);
        TextView nameTextView = (TextView) findViewById(R.id.nameText);
        nameTextView.setText(trackDetails.getTrackName());
        TextView artistTextView = (TextView) findViewById(R.id.artistText);
        artistTextView.setText(trackDetails.getArtist());
        ImageView largeImage = (ImageView) findViewById(R.id.largeImageView);
        Picasso.with(this).load(trackDetails.getLargeImageUrl()).into(largeImage);
        TextView urlText = (TextView) findViewById(R.id.urlText);
        urlText.setText(trackDetails.getTrackUrl());
        if (trackResultsList.size() > 0) {
            trackFavoriteList = getSharedValues();
            for (TrackResults favTrack:trackFavoriteList) {
                for (TrackResults track:trackResultsList){
                    if(track.getTrackName().equals(favTrack.getTrackName())){
                        track.setFavorite(true);
                    }
                }
            }
            ListAdapter adapter = new ListAdapter(this, trackResultsList);
            similarListData = (ListView) findViewById(R.id.similarList);
            similarListData.setAdapter(adapter);
            adapter.setNotifyOnChange(true);
            similarListData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                    TextView trackView = (TextView) view.findViewById(R.id.trackName);
                    TextView artistView = (TextView) view.findViewById(R.id.artistName);
                    trackName = trackView.getText().toString();
                    artistName = artistView.getText().toString();
                    try {
                        trackURL = getEncodedUrl();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tracker = trackResultsList.get(position);
                    Log.d("The url in encoded","is"+trackURL);
                    new GetSimilarAsyncTask(TrackDetailsActivity.this, tracker).execute(trackURL);
                    trackURL = "";
                }
            });
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.HomeButton:
                Intent intent = new Intent(TrackDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.QuitButton:
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                return true;
        }
        return true;
    }
    public String getEncodedUrl() throws UnsupportedEncodingException {
        artistName = URLEncoder.encode(artistName, "UTF-8");
        //artistName.replaceAll(" ", "%20");
        trackName = URLEncoder.encode(trackName, "UTF-8");
        //trackName.replaceAll(" ", "%20");
        return trackURL+"&artist="+artistName+"&track="+trackName;
    }
    public List<TrackResults> getSharedValues(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Hello", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("favorite", null);
        Type type = new TypeToken<ArrayList<TrackResults>>() {}.getType();
        ArrayList<TrackResults> arrayList = gson.fromJson(json, type);
        return arrayList;
    }
}
