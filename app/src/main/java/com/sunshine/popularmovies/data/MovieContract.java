package com.sunshine.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abhishek on 15-06-2016.
 */
public class MovieContract {

    //Defining the content authority
    public static final String CONTENT_AUTHORITY="com.sunshine.popularmovies";

    //Defining the Base Content Uri
    public static Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    //Defining path for table used in database
    public static final String PATH_MOVIE="movie";


    public static final class MovieEntry implements BaseColumns
    {

        //Content Uri used for Movie Table
        public static final Uri CONTENT_URI=  BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        //Defining the table name
        public static final String TABLE_NAME="movie";

        //Column with the primary key i.e. Movie ID
        public static final String COLUMN_MOVIE_ID="movie_id";

        //Title of the movie
        public static final String COLUMN_MOVIE_TITLE="title";

        //Poster Path is used to display the movie thumbnail
        public static final String COLUMN_MOVIE_POSTER_PATH="poster_path";

        //Overview is used in detail view of particular movie
        public static final String COLUMN_MOVIE_OVERVIEW="overview";

        //Release date is displayed in detail fragment
        public static final String COLUMN_MOVIE_RELEASE_DATE="release_date";

        //Popularity is displayed in detail fragment
        public static final String COLUMN_MOVIE_POPULARITY="popularity";

        //Vote Count is used in detail fragment
        public static final String COLUMN_MOVIE_VOTE_COUNT="vote_count";

        //vote average is used in detail fragment
        public static final String COLUMN_MOVIE_VOTE_AVG="vote_average";

        public static Uri buildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildMovieWithMovieIdUri(int movie_id)
        {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movie_id)).build();
        }

        public static int getMovieId(Uri uri)
        {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }
}
