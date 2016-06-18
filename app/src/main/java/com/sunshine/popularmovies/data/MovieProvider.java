package com.sunshine.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    MovieDbHelper mMovieDbHelper;
    static final int MOVIE = 100;
    static final int MOVIE_WITH_MOVIE_ID = 101;


    public MovieProvider() {
        super();
    }



    //SQLite Query Builder
    private static final SQLiteQueryBuilder mSQLiteQueryBuilder;

    static
    {
        mSQLiteQueryBuilder = new SQLiteQueryBuilder();
    }

    //movie.movie_id=?
    final String movieWithMovieId = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";

    public Cursor getMovieWithMovieId(Uri uri,String projection[],String sortOrder)
    {
        int movieId= MovieContract.MovieEntry.getMovieId(uri);
        String selection= movieWithMovieId;
        String selectionArgs[]=new String[]{String.valueOf(movieId)};

        return mSQLiteQueryBuilder.query(mMovieDbHelper.getWritableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_MOVIE_ID);
        return matcher;

    }




    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        final int match= sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE:
                cursor= mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_MOVIE_ID:
                cursor= getMovieWithMovieId(uri,projection,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues values) {

        SQLiteDatabase db= mMovieDbHelper.getWritableDatabase();
        final int match= sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case MOVIE:
                long _id= db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if (_id>0)
                    returnUri= MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Unable to insert into "+uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
