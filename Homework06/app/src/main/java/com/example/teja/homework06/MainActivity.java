/*
* Assignment 5
* MainActivity.Java
* Prabhakar Seeda Teja and Yash Ghia
* */

package com.example.teja.homework06;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ListView list;
    ListAdapter adapter;
    public static List<TrackResults> trackResultsList = new ArrayList<TrackResults>();
    TrackResults tracker;
    String trackName, artistName;
    String s = "",finalURL = "";
    final static String URL = "url",TRACK_RESULTS = "trackresults";
    String trackURL = "https://ws.audioscrobbler.com/2.0/?format=json&limit=10&method=track.getsimilar&api_key=bdebb829c13038655ef949113ad7daab";
    String baseURL = "https://ws.audioscrobbler.com/2.0/?format=json&method=track.search&api_key=bdebb829c13038655ef949113ad7daab&limit=20&track=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Music Search");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Hello",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("favorite", "");
        Type type = new TypeToken<ArrayList<TrackResults>>() {}.getType();
        ArrayList<TrackResults> arrayList = gson.fromJson(json, type);
        Log.d("the value on resume","id"+arrayList);
        if (trackResultsList.isEmpty() && arrayList!=null) {
            trackResultsList = arrayList;
            adapter = new ListAdapter(this, trackResultsList);
            list = (ListView) findViewById(R.id.favoriteMainView);
            list.setAdapter(adapter);
            adapter.setNotifyOnChange(true);
            saveSharedPreferences(trackResultsList);
        }else {
            adapter = new ListAdapter(this, trackResultsList);
            list = (ListView) findViewById(R.id.favoriteMainView);
            list.setAdapter(adapter);
            adapter.setNotifyOnChange(true);
            saveSharedPreferences(trackResultsList);
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                TextView trackView = (TextView) view.findViewById(R.id.trackName);
                TextView artistView = (TextView) view.findViewById(R.id.artistName);
                trackName = trackView.getText().toString();
                artistName = artistView.getText().toString();
                try {
                    trackURL = getEncodedUrlTrackDetail();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                tracker = trackResultsList.get(position);
                Log.d("The url in encoded ","is"+trackURL);
                new GetSimilarAsyncTask(MainActivity.this, tracker).execute(trackURL);
                trackURL = "https://ws.audioscrobbler.com/2.0/?format=json&limit=10&method=track.getsimilar&api_key=bdebb829c13038655ef949113ad7daab";
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void saveSharedPreferences(List<TrackResults> resultsList){
        SharedPreferences preferences2 = getApplicationContext().getSharedPreferences("Hello", MODE_PRIVATE);
        Gson gsonNew = new Gson();
        SharedPreferences.Editor prefEditor = preferences2.edit();
        String json2 = gsonNew.toJson(resultsList);
        prefEditor.putString("favorite", json2);
        prefEditor.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.HomeButton:
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
    public String getEncodedUrlTrackDetail() throws UnsupportedEncodingException {
        artistName = URLEncoder.encode(artistName, "UTF-8");
        //artistName.replaceAll(" ", "%20");
        trackName = URLEncoder.encode(trackName, "UTF-8");
        //trackName.replaceAll(" ", "%20");
        return trackURL+"&artist="+artistName+"&track="+trackName;
    }


    public String getEncodedUrl() throws UnsupportedEncodingException {
        EditText getTrack = (EditText) findViewById(R.id.editText);
        String val="";
        s = (String) getTrack.getText().toString().trim();
        if(!s.isEmpty()) {
            val = URLEncoder.encode(s, "UTF-8");
        }
        else
        {
            Toast.makeText(this,"Enter text to search",Toast.LENGTH_LONG).show();
        }
        return baseURL + val;

    }
    public void searchFunction(View view) throws UnsupportedEncodingException{
        finalURL = getEncodedUrl();
        Log.d("The url is","Hello"+finalURL);
        new GetTrackAsyncTask().execute(finalURL);
    }
    private class GetTrackAsyncTask extends AsyncTask<String, Integer ,ArrayList<TrackResults>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<TrackResults> trackResults) {
            Log.d("The trackresults in ","postexecute is " + trackResults);
            if (trackResults!=null) {
                int totalTrackCount = trackResults.size();
                super.onPostExecute(trackResults);
                if (totalTrackCount > 0) {
                    Intent i = new Intent(MainActivity.this, ResultsActivity.class);
                    i.setFlags(103);
                    i.putExtra(TRACK_RESULTS, (Serializable) trackResults);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "No music track found", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<TrackResults> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Log.d("the response","code is " + connection.getResponseCode());
                int statusCode = connection.getResponseCode();
                if(statusCode==HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = reader.readLine();
                    while(line!=null){
                        stringBuilder.append(line);
                        line = reader.readLine();
                    }
                    return parseTrackResult(stringBuilder.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<TrackResults> parseTrackResult(String in) throws JSONException {
            ArrayList<TrackResults> trackResults = new ArrayList<>();
            JSONObject rootObject = new JSONObject(in);
            JSONObject jsonObject1 = rootObject.getJSONObject("results");
            JSONObject jsonObject2 = jsonObject1.getJSONObject("trackmatches");
            JSONArray trackArray = jsonObject2.getJSONArray("track");
            for(int i=0;i<trackArray.length();i++){
                JSONObject trackJSONObject = trackArray.getJSONObject(i);
                trackResults.add(TrackResults.createResults(trackJSONObject));
            }
            return trackResults;
        }
    }
}
