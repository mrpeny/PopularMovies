package eu.captaincode.popularmovies.utilities;

import android.content.ContentValues;

import eu.captaincode.popularmovies.data.MovieContract.MovieEntry;
import eu.captaincode.popularmovies.model.Movie;

/**
 * Handles ContentValues Movie objects conversion.
 */

public class MovieObjectRelationMapper {

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

    public static Movie toMovie(ContentValues contentValues) {
        Movie movie = new Movie();

        movie.setId(contentValues.getAsInteger(MovieEntry._ID));
        movie.setTitle(contentValues.getAsString(MovieEntry.COLUMN_TITLE));
        movie.setPosterPath(contentValues.getAsString(MovieEntry.COLUMN_POSTER_PATH));
        movie.setOverView(contentValues.getAsString(MovieEntry.COLUMN_OVERVIEW));
        movie.setVoteAverage(contentValues.getAsLong(MovieEntry.COLUMN_VOTE_AVERAGE));
        movie.setBackdropPath(contentValues.getAsString(MovieEntry.COLUMN_BACKDROP_PATH));
        movie.setDate(contentValues.getAsString(MovieEntry.COLUMN_DATE));
        movie.setFavorite(contentValues.getAsBoolean(MovieEntry.COLUMN_FAVORITE));

        return movie;
    }
}
