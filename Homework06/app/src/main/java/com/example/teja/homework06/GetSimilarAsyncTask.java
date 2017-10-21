/*
* Assignment 5
* GetSimilarAsyncTask.Java
* Prabhakar Seeda Teja and Yash Ghia
* */

package com.example.teja.homework06;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by teja on 10/14/17.
 */

public class GetSimilarAsyncTask extends AsyncTask<String, Integer ,ArrayList<TrackResults>> {
    Context context;
    TrackResults tracker;
    public final static String TRACK_DETAILS = "trackdetails";
    public final static String TRACK_ARRAY = "similartrack";
    public GetSimilarAsyncTask(Context activity, TrackResults tracker){
        this.context = activity;
        this.tracker = tracker;
    }
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
                Intent i = new Intent(context, TrackDetailsActivity.class);
                i.setFlags(103);
                i.putExtra(TRACK_ARRAY, (Serializable) trackResults);
                i.putExtra(TRACK_DETAILS, (Serializable) tracker);
                context.startActivity(i);
            }
        }else {
            Toast.makeText(context,"No similar tracks found",Toast.LENGTH_LONG).show();
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
            Log.d("The url value","activity is" + url);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Log.d("the response new","code is " + connection.getResponseCode());
            int statusCode = connection.getResponseCode();
            if(statusCode== HttpURLConnection.HTTP_OK){
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
        JSONObject jsonObject1 = rootObject.getJSONObject("similartracks");
        JSONArray trackArray = jsonObject1.getJSONArray("track");
        if (trackArray.length()>0) {
            for (int i = 0; i < trackArray.length(); i++) {
                JSONObject trackJSONObject = trackArray.getJSONObject(i);
                trackResults.add(TrackResults.createSimilarResults(trackJSONObject));
            }
        }
        return trackResults;

    }
}
