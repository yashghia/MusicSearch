/*
* Assignment 5
* ResultsActivity.Java
* Prabhakar Seeda Teja and Yash Ghia
* */

package com.example.teja.homework06;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    public List<TrackResults> trackResultsList = new ArrayList<TrackResults>();
    public List<TrackResults> trackFavoriteList = new ArrayList<TrackResults>();
    String trackName, artistName;
    TrackResults tracker;
    ListAdapter adapter;
    public final static String TRACK_DETAILS = "trackdetails";
    public final static String TRACK_ARRAY = "similartrack";
    String trackURL = "https://ws.audioscrobbler.com/2.0/?format=json&limit=10&method=track.getsimilar&api_key=bdebb829c13038655ef949113ad7daab";
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setTitle("Search Results");
        if (getIntent().getExtras() != null) {
            trackResultsList = (List<TrackResults>) getIntent().getExtras().getSerializable(MainActivity.TRACK_RESULTS);

            trackFavoriteList = getSharedValues();
            for (TrackResults favTrack:trackFavoriteList) {
                for (TrackResults track:trackResultsList){
                    if(track.getTrackUrl().equals(favTrack.getTrackUrl())){
                        track.setFavorite(true);
                    }
                }
            }

            ListAdapter adapter = new ListAdapter(this, trackResultsList);
            list = (ListView) findViewById(R.id.resultsListView);
            list.setAdapter(adapter);
            adapter.setNotifyOnChange(true);
            adapter.notifyDataSetChanged();
            //list.setOnItemClickListener((AdapterView.OnItemClickListener) this);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    Log.d("The url in encoded ","is"+trackURL);
                    new GetSimilarAsyncTask(ResultsActivity.this, tracker).execute(trackURL);
                    trackURL = "https://ws.audioscrobbler.com/2.0/?format=json&limit=10&method=track.getsimilar&api_key=bdebb829c13038655ef949113ad7daab";
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
                    Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
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
