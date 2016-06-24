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

    //Constants defined for Movie Table
    static final int MOVIE = 100;
    static final int MOVIE_WITH_MOVIE_ID = 101;

    //Constants defined for Trailer Table
    static final int TRAILER=102;
    static final int TRAILER_WITH_ID=103;

    //Constants defined for Review Table
    static final int REVIEW=104;
    static final int REVIEW_WITH_ID=105;



    public MovieProvider() {
        super();
    }


    //SQLite Query Builder
    private static final SQLiteQueryBuilder mSQLiteQueryBuilder;

    static {
        mSQLiteQueryBuilder = new SQLiteQueryBuilder();
        mSQLiteQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
        mSQLiteQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
        mSQLiteQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME);
    }

    //movie.movie_id=?
    final String movieWithMovieId = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";

    //review.movie_id=?
    final String reviewWithId= MovieContract.ReviewEntry.COL_REVIEW_ID+"=?";

    //trailer.movie_id=?
    final String trailerWithId= MovieContract.TrailerEntry.COL_TRAILER_ID+"=?";

    public Cursor getTrailerWithId(Uri uri,String projection[],String sortOrder)
    {
        int id = MovieContract.TrailerEntry.getTrailerMovieId(uri);
        String selection = trailerWithId;
        String selectionArgs[]= new String[]{String.valueOf(id)};

        return mSQLiteQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public Cursor getReviewWithId(Uri uri,String projection[],String sortOrder)
    {
        int id = MovieContract.ReviewEntry.getReviewMovieId(uri);
        String selection = reviewWithId;
        String selectionArgs[]= new String[]{String.valueOf(id)};

        return mSQLiteQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }



    public Cursor getMovieWithMovieId(Uri uri, String projection[],String sortOrder) {
        int id = MovieContract.MovieEntry.getMovieId(uri);
        String selection = movieWithMovieId;
        String selectionArgs[] = new String[]{String.valueOf(id)};

        return mSQLiteQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
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

        matcher.addURI(authority,MovieContract.PATH_TRAILER,TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER+"/#",TRAILER_WITH_ID);

        matcher.addURI(authority,MovieContract.PATH_REVIEW,REVIEW);
        matcher.addURI(authority,MovieContract.PATH_REVIEW+"/#",REVIEW_WITH_ID);

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

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                cursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_MOVIE_ID:
                cursor = getMovieWithMovieId(uri, projection,sortOrder);
                break;

            case TRAILER:
                cursor= mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRAILER_WITH_ID:
                cursor= getTrailerWithId(uri,projection,sortOrder);
                break;

            case REVIEW:
                cursor= mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW_WITH_ID:
                cursor=getReviewWithId(uri,projection,sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

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

            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_ID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Unable to insert into " + uri);
                break;

            case TRAILER:
                long idTrailer= db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (idTrailer > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(idTrailer);
                else
                    throw new SQLException("Unable to insert into " + uri);
                break;

            case REVIEW:
                long idReview= db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (idReview > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(idReview);
                else
                    throw new SQLException("Unable to insert into " + uri);
                break;


            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long rowDeleted;
        if (selection == null) selection = "1";
        switch (match) {
            case MOVIE:
                rowDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TRAILER:
                rowDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEW:
                rowDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unable to delete: " + uri);

        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return (int) rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long rowUpdated = 0;

        switch (match) {
            case MOVIE:
                rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TRAILER:
                rowUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case REVIEW:
                rowUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return (int) rowUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues contentValues : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                        if (_id != 0)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;


            case TRAILER:
                db.beginTransaction();
                int returnCountTrailer = 0;
                try {
                    for (ContentValues contentValues : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, contentValues);
                        if (_id != 0)
                            returnCountTrailer++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCountTrailer;

            case REVIEW:
                db.beginTransaction();
                int returnCountReview = 0;
                try {
                    for (ContentValues contentValues : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                        if (_id != 0)
                            returnCountReview++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCountReview;

            default:
                return super.bulkInsert(uri, values);


        }
    }
}
