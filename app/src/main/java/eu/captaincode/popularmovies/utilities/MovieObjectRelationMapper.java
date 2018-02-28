package eu.captaincode.popularmovies.utilities;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.data.MovieContract.MovieEntry;
import eu.captaincode.popularmovies.model.Movie;

/**
 * Handles Movie conversions represented as {@link ContentValues}, {@link Cursor}, or POJO's.
 */

public class MovieObjectRelationMapper {
    /**
     * Converts {@link Movie} POJO to {@link ContentValues}.
     *
     * @param movie the object to convert to
     * @return the converted Movie object
     */
    public static ContentValues toContentValues(Movie movie) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieEntry.COLUMN_ID_TMDB, movie.getId());
        contentValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverView());
        contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MovieEntry.COLUMN_DATE, movie.getDate());
        contentValues.put(MovieEntry.COLUMN_FAVORITE, movie.isFavorite());

        return contentValues;
    }

    /**
     * Converts {@link Cursor} to a List of {@link Movie} POJO's.
     *
     * @param cursor the cursor containing a list of Movie objects
     * @return the list of {@link Movie} objects
     */
    public static List<Movie> toMovieList(@NonNull Cursor cursor) {
        int numRows = cursor.getCount();
        if (numRows == 0) {
            return new ArrayList<>(0);
        }
        List<Movie> movieList = new ArrayList<>(numRows);

        int tmdbIdIndex = cursor.getColumnIndex(MovieEntry.COLUMN_ID_TMDB);
        int titleIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        int posterPathIndex = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
        int overviewIndex = cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
        int voteAverageIndex = cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
        int backdropPathIndex = cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP_PATH);
        int dateIndex = cursor.getColumnIndex(MovieEntry.COLUMN_DATE);
        int favoriteIndex = cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE);

        cursor.moveToFirst();
        do {
            Movie movie = new Movie();

            movie.setId(cursor.getInt(tmdbIdIndex));
            movie.setTitle(cursor.getString(titleIndex));
            movie.setPosterPath(cursor.getString(posterPathIndex));
            movie.setOverView(cursor.getString(overviewIndex));
            movie.setVoteAverage(cursor.getLong(voteAverageIndex));
            movie.setBackdropPath(cursor.getString(backdropPathIndex));
            movie.setDate(cursor.getString(dateIndex));
            movie.setFavorite(cursor.getInt(favoriteIndex) == 1);

            movieList.add(movie);
        } while (cursor.moveToNext());

        return movieList;
    }
}
