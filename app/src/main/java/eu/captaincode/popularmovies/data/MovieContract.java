package eu.captaincode.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the table and column names for the PopularMovies database.
 */

public class MovieContract {

    /**
     * Name for the entire content provider that uniquely identifies the {@link MovieContract}.
     */
    public static final String CONTENT_AUTHORITY = "eu.captaincode.popularmovies";

    /**
     * Base of all URI's which apps will use to connect to the content provider for PopularMovie.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path segment for Movies for storing in the PopularMovie database.
     */
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        /**
         * Content URI for Movie entries in the PopularMovies database.
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID_TMDB = "tmdb_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_DATE = "date";
    }

}
