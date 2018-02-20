package eu.captaincode.popularmovies.utilities;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.model.Movie;

/**
 * Parses JSON strings received from The Movie DB into {@link Movie} objects and lists of them.
 */
public class TmdbJsonUtils {
    private static final String TAG = TmdbJsonUtils.class.getSimpleName();

    /* Movie List JSON property keys */
    // Type: JSONArray
    private static final String RESULTS_LIST_TMDB = "results";

    /* Movie JSON property keys */
    private static final String TITLE_TMDB = "title";
    private static final String POSTER_PATH_TMDB = "poster_path";
    private static final String OVERVIEW_TMDB = "overview";
    private static final String VOTE_AVERAGE_TMDB = "vote_average";
    private static final String RELEASE_DATE_TMDB = "release_date";

    /* TMDB JSON status property keys */
    private static final String STATUS_CODE_TMDB = "status_code";
    private static final String STATUS_MESSAGE_TMDB = "status_message";

    private static final int INITIAL_MOVIE_COUNT = 20;

    /**
     * Parses JSON string containing movies into a {@link List} of Movies object.
     *
     * @param jsonString the string containing the movies in JSON format
     * @return the list of {@link Movie} objects parsed from the given String if successful or null
     * otherwise
     */
    public static List<Movie> parseMovieListFrom(String jsonString) throws JSONException {
        JSONObject rootJson = new JSONObject(jsonString);

        if (!isQuerySuccessful(rootJson)) {
            return null;
        }

        JSONArray moviesArray = rootJson.optJSONArray(RESULTS_LIST_TMDB);
        if (moviesArray == null || moviesArray.length() == 0) {
            return null;
        }

        List<Movie> movieList = new ArrayList<>(INITIAL_MOVIE_COUNT);
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieJson = moviesArray.getJSONObject(i);
            Movie movie = parseMovieFrom(movieJson);
            movieList.add(movie);
        }
        return movieList;
    }

    /**
     * Parses JSON string containing movie details to a Movie object.
     *
     * @param movieJsonString the JSON string containing the movie details
     * @return the {@link Movie} object parsed details if successful or
     * null otherwise
     */
    public static Movie parseMovieFrom(String movieJsonString) throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonString);
        if (!isQuerySuccessful(movieJson)) {
            return null;
        }
        return parseMovieFrom(movieJson);
    }

    /* Helper methods */

    /**
     * Parses a{@link JSONObject} containing movie details to a {@link Movie} object.
     *
     * @param movieJson the {@link JSONObject} containing the movie details
     * @return the {@link Movie} object parsed details if successful
     */
    private static Movie parseMovieFrom(JSONObject movieJson) throws JSONException {
        String title = movieJson.optString(TITLE_TMDB);
        String posterPath = movieJson.optString(POSTER_PATH_TMDB);
        String overview = movieJson.optString(OVERVIEW_TMDB);
        double voteAverage = movieJson.optDouble(VOTE_AVERAGE_TMDB);
        String releaseDate = movieJson.optString(RELEASE_DATE_TMDB);

        return new Movie(title, posterPath, overview, voteAverage, releaseDate);
    }

    private static boolean isQuerySuccessful(JSONObject jsonObject) {
        if (jsonObject.has(STATUS_CODE_TMDB)) {
            String statusMessage = jsonObject.optString(STATUS_MESSAGE_TMDB);
            Log.d(TAG, statusMessage);
            return false;
        }
        return true;
    }
}
