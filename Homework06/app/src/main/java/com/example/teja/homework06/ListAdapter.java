/*
* Assignment 5
* ListAdapter.Java
* Prabhakar Seeda Teja and Yash Ghia
* */

package com.example.teja.homework06;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by teja on 10/11/17.
 */

public class ListAdapter extends ArrayAdapter<TrackResults> {
    //ImageButton favoriteButton;
    private Activity context;
    private List<TrackResults> trackResults;
    public List<TrackResults> trackResultsShared = new ArrayList<TrackResults>();
    MainActivity main = new MainActivity();
    public ListAdapter(Activity context, List<TrackResults> resultsList) {
        super(context, R.layout.resultslist_row, (List<TrackResults>) resultsList);
        this.context = context;
        this.trackResults = resultsList;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        //ViewHolder holder;
        //final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.resultslist_row, null,true);
        Log.d("the povalues are","in adapter" + position);
        final TrackResults trackData = getItem(position);
        TextView name = (TextView) rowView.findViewById(R.id.trackName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.smallImage);
        TextView artistName = (TextView) rowView.findViewById(R.id.artistName);
        name.setText(trackData.getTrackName());
        artistName.setText(trackData.getArtist());
        final ImageButton favoriteButton = (ImageButton) rowView.findViewById(R.id.favoriteImage);
        favoriteButton.setId(position);
        if (trackData.getFavorite()==false) {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
        }else{
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteButton.getId()==position && trackData.getFavorite() == false){
                    favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                    Toast.makeText(context,"Added to Favorite List",Toast.LENGTH_LONG).show();
                    favoriteButton.setId(position+1);
                    trackData.setFavorite(true);
                    trackResultsShared = getSharedValues();
                    if (trackResultsShared!=null){
                        if(!trackResultsShared.contains(trackData)) {
                            trackResultsShared.add(trackData);
                        }
                        MainActivity.trackResultsList = trackResultsShared;
                        Log.d("Peas","Fell"+MainActivity.trackResultsList);
                    }else {
                        MainActivity.trackResultsList.add(trackData);
                    }
                    storeSharedPreferences(MainActivity.trackResultsList);

                } else {
                    favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(context,"Removed from Favorite List",Toast.LENGTH_LONG).show();
                    favoriteButton.setId(position+1);
                    trackData.setFavorite(false);
                    trackResultsShared = getSharedValues();
                    for (TrackResults favTrack:trackResultsShared) {
                        if (favTrack.getTrackUrl().equals(trackData.getTrackUrl())) {
                            trackResultsShared.remove(favTrack);
                            break;
                        }
                    }
                    for (TrackResults movieTrack:MainActivity.trackResultsList) {
                        if (movieTrack.getTrackUrl().equals(trackData.getTrackUrl())) {
                            MainActivity.trackResultsList.remove(movieTrack);
                            break;
                        }
                    }
                    //MainActivity.trackResultsList.remove(trackData);
                    storeSharedPreferences(MainActivity.trackResultsList);

                }
                notifyDataSetChanged();
            }
        });
        favoriteButton.setFocusable(false);
        Picasso.with(context).load(trackData.getSmallImageUrl()).into(imageView);
        return rowView;
    }
    public List<TrackResults> getSharedValues(){
        SharedPreferences preferences = context.getSharedPreferences("Hello",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("favorite", null);
        Type type = new TypeToken<ArrayList<TrackResults>>() {}.getType();
        ArrayList<TrackResults> arrayList = gson.fromJson(json, type);
        return arrayList;
    }
    public void storeSharedPreferences(List<TrackResults> trackDatas){
        SharedPreferences preferences = context.getSharedPreferences("Hello",Context.MODE_PRIVATE);
        Gson gsonNew = new Gson();
        SharedPreferences.Editor prefEditor = preferences.edit();
        String json2 = gsonNew.toJson(trackDatas);
        prefEditor.putString("favorite",json2);
        prefEditor.commit();
    }
}
