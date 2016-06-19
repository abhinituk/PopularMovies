package com.sunshine.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhishek on 12-05-2016.
 */
public class MovieData implements Parcelable {

    String ImageUrl;
    String overview;
    String release_date;
    String title;
    double vote_average;
    int movieId;
    double popularity;
    double vote_count;


    MovieData(String title, String overview, double vote_average, String release_date, String ImageUrl, int movieId, double popularity
            , double vote_count) {
        this.ImageUrl = ImageUrl;
        this.title = title;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.movieId= movieId;
        this.popularity=popularity;
        this.vote_count=vote_count;
    }


    protected MovieData(Parcel in) {
        ImageUrl = in.readString();
        overview = in.readString();
        release_date = in.readString();
        title = in.readString();
        vote_average = in.readDouble();
        movieId= in.readInt();
        popularity=in.readDouble();
        vote_count=in.readDouble();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ImageUrl);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(title);
        dest.writeDouble(vote_average);
        dest.writeInt(movieId);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_count);

    }
}
