package com.sunshine.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATEBASE_VERSION = 7;
    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATEBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_MOVIE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " STRING NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL" +
                        " )";

        final String CREATE_TABLE_TRAILER=
                "CREATE TABLE "+ MovieContract.TrailerEntry.TABLE_NAME+" ("+
                        MovieContract.TrailerEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        MovieContract.TrailerEntry.COL_TRAILER_ID+" STRING NOT NULL, "+
                        MovieContract.TrailerEntry.COL_TRAILER_NAME+" TEXT NOT NULL, "+
                        MovieContract.TrailerEntry.COL_TRAILER_SOURCE+" TEXT NOT NULL, "+
                        "UNIQUE ("+ MovieContract.TrailerEntry.COL_TRAILER_ID+","+
                        MovieContract.TrailerEntry.COL_TRAILER_NAME+"));";

        final String CREATE_TABLE_REVIEW=
                "CREATE TABLE "+ MovieContract.ReviewEntry.TABLE_NAME+" ("+
                        MovieContract.ReviewEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        MovieContract.ReviewEntry.COL_REVIEW_ID+" STRING NOT NULL, "+
                        MovieContract.ReviewEntry.COL_REVIEW_AUTHOR+" TEXT NOT NULL, "+
                        MovieContract.ReviewEntry.COL_REVIEW_CONTENT+" TEXT NOT NULL, "+
                        "UNIQUE ("+ MovieContract.ReviewEntry.COL_REVIEW_ID+","+
                        MovieContract.ReviewEntry.COL_REVIEW_AUTHOR+"));";


        db.execSQL(CREATE_TABLE_MOVIE);
        db.execSQL(CREATE_TABLE_TRAILER);
        db.execSQL(CREATE_TABLE_REVIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(db);

    }
}
