package eu.captaincode.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.captaincode.popularmovies.data.MovieContract.MovieEntry;

/**
 * Manages local database for PopularMovie data.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_ID_TMDB + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                        MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                        MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                        MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                        MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0 );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
