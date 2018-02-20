package eu.captaincode.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import eu.captaincode.popularmovies.BuildConfig;

/**
 * Makes network calls to The Movie DB web api.
 */

public class NetworkUtils {
    /**
     * Secure base image URL for accessing TMDB images
     */
    private static final String BASE_IMAGE_URL_TMDB = "https://image.tmdb.org/t/p";
    /**
     * Path for image size specification to append
     */
    private static final String IMAGE_SIZE_PATH_TMDB = "w185";

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
        boolean popularSorted = false;
        if (popularSorted) {
            String moviePopularEndpoint = Uri.decode(MOVIE_POPULAR_ENDPOINT_TMDB);
            tmdbUri = tmdbUri.buildUpon().appendEncodedPath(moviePopularEndpoint).build();
        } else {
            String movieTopRatedEndpoint = Uri.decode(MOVIE_TOP_RATED_ENDPOINT_TMDB);
            tmdbUri = tmdbUri.buildUpon().appendEncodedPath(movieTopRatedEndpoint).build();
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
     * Creates a valid The Movie DB Uri pointing to a poster image.
     *
     * @param imagePath the last path segment containing the image filename and extension
     *                  eg: "/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg"
     * @return the formatted Uri pointing to the image specified in the argument
     */
    public static Uri getImageUriFor(String imagePath) throws MalformedURLException {
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.replaceFirst("/", "");
        }
        return Uri.parse(BASE_IMAGE_URL_TMDB).buildUpon()
                .appendEncodedPath(IMAGE_SIZE_PATH_TMDB)
                .appendEncodedPath(imagePath).build();
    }

    /* Helper methods for Uri and URL builds */
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
