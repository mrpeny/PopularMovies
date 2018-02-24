package eu.captaincode.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import eu.captaincode.popularmovies.BuildConfig;
import eu.captaincode.popularmovies.R;

/**
 * Makes network calls to The Movie DB web api.
 */

public class NetworkUtils {
    private static final String BASE_IMAGE_URL_TMDB = "https://image.tmdb.org/t/p";

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL_TMDB = "https://api.themoviedb.org/3";
    private static final String MOVIE_POPULAR_ENDPOINT_TMDB = "movie/popular";
    private static final String MOVIE_TOP_RATED_ENDPOINT_TMDB = "movie/top_rated";
    private static final String MOVIE_DETAILS_ENDPOINT_TMDB = "movie";

    private static final String API_KEY_PARAM_TMDB = "api_key";
    private static final String LANGUAGE_PARAM_TMDB = "language";

    private static final String DEFAULT_LANGUAGE_TAG_TMDB = "en-US";

    /**
     * Retrieves the URL for The Movie DB based on the user's sorting preferences.
     *
     * @param context the application context used for accessing application wide information
     * @return the generated {@link URL} based on user's preference
     */
    public static URL getMovieListQueryUrl(Context context) {
        Uri tmdbUri = buildBaseUriWithLanguageAndApi();

        // This validation will be implemented later
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String sortBy = sharedPreferences.getString(context.getString(R.string.preference_key_sort_by), "");
        if (sortBy.equals(context.getString(R.string.preference_option_sort_by_top_rated_value))) {
            String movieTopRatedEndpoint = Uri.decode(MOVIE_TOP_RATED_ENDPOINT_TMDB);
            tmdbUri = tmdbUri.buildUpon().appendEncodedPath(movieTopRatedEndpoint).build();
        } else {
            String moviePopularEndpoint = Uri.decode(MOVIE_POPULAR_ENDPOINT_TMDB);
            tmdbUri = tmdbUri.buildUpon().appendEncodedPath(moviePopularEndpoint).build();
        }
        return buildUrlFrom(tmdbUri);
    }

    /**
     * Retrieves the URL for the details on a Movie of The Movie DB to based on the given ID.
     *
     * @param context the application context used for accessing application wide information
     * @param movieId the TMDB web API "id" of the movie to get details about
     * @return the generated {@link URL} based on user's preference
     */
    public static URL getMovieDetailsQueryURL(Context context, String movieId) {
        Uri tmdbUri = buildBaseUriWithLanguageAndApi();
        tmdbUri = tmdbUri.buildUpon().appendEncodedPath(MOVIE_DETAILS_ENDPOINT_TMDB)
                .appendEncodedPath(movieId).build();
        return buildUrlFrom(tmdbUri);
    }

    /**
     * Builds up https URLConnection for the given URL and returns the received json from the
     * server based on the url argument.
     *
     * @param url the url containing the query that will be requested from the server
     * @return the json response from the server based on the given url
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        // TODO: Create Network Security Config for https connection {@see https://developer.android.com/training/articles/security-config.html}
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Creates a valid The Movie DB Uri pointing to a poster image considering resolution
     * requirements of the screen size image.
     *
     * @param context    the application context used for resource retrieval
     * @param posterPath the last path segment containing the image filename and extension
     *                   eg: "/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg"
     * @return the formatted Uri pointing to the poster image specified in the argument
     */
    public static Uri getPosterImageUriFor(Context context, String posterPath) {
        String posterSize = context.getString(R.string.poster_size_path_tmdb);
        return getImageUriFor(posterPath, posterSize);
    }

    /**
     * Creates a valid The Movie DB Uri pointing to a backdrop image considering resolution
     * requirements of the screen size image.
     *
     * @param context      the application context used for resource retrieval
     * @param backdropPath the last path segment containing the image filename and extension
     *                     eg: "/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg"
     * @return the formatted Uri pointing to the backdrop image specified in the argument
     */
    public static Uri getBackdropImageUriFor(Context context, String backdropPath) {
        String backdropSize = context.getString(R.string.backdrop_size_path_tmdb);
        return getImageUriFor(backdropPath, backdropSize);
    }

    /* Helper methods for Uri and URL builds */
    private static Uri getImageUriFor(String imagePath, String imageSizePath) {
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.replaceFirst("/", "");
        }
        return Uri.parse(BASE_IMAGE_URL_TMDB).buildUpon()
                .appendEncodedPath(imageSizePath)
                .appendEncodedPath(imagePath).build();
    }

    private static Uri buildBaseUriWithLanguageAndApi() {
        String languageTag = DEFAULT_LANGUAGE_TAG_TMDB;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            languageTag = Locale.getDefault().toLanguageTag();
        }
        return Uri.parse(BASE_URL_TMDB).buildUpon()
                .appendQueryParameter(LANGUAGE_PARAM_TMDB, languageTag)
                .appendQueryParameter(API_KEY_PARAM_TMDB, BuildConfig.API_KEY)
                .build();
    }

    private static URL buildUrlFrom(Uri uri) {
        try {
            URL queryUrl = new URL(uri.toString());
            Log.d(TAG, queryUrl.toString());
            return queryUrl;
        } catch (MalformedURLException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }
}
