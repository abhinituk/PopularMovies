package com.sunshine.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abhishek on 12-05-2016.
 */
public class CustomMovieAdapter extends ArrayAdapter<MovieData> {

    public CustomMovieAdapter(Context context, List<MovieData> resource) {
        super(context,0,resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData movieData=getItem(position);
        PosterImageViewHolder holder;
        if(convertView == null) {
            holder=new PosterImageViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movies,parent,false);
            holder.poster = (ImageView) convertView.findViewById(R.id.grid_item_imageview);
            convertView.setTag(holder);
        }
        else {
            holder = (PosterImageViewHolder) convertView.getTag();
        }

        Picasso.with(getContext())
                .load(movieData.ImageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.poster);

        return convertView;
    }
}
