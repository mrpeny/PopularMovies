package eu.captaincode.popularmovies;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.TmdbJsonUtils;

/**
 * Makes testing on app components in this development phase (MrPeny 2018.02.20.)
 */
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView tv;
    ImageView iv;

    List<Movie> movieList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.main_tv);
        iv = findViewById(R.id.main_iv);

        getSupportLoaderManager().initLoader(42, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonString) {
        if (jsonString == null) {
            Toast.makeText(this, "Cannot connect to server. Check your internet connection!",
                    Toast.LENGTH_SHORT).show();
        }

        /*try {
            movieList = TmdbJsonUtils.parseMovieListFrom(jsonString);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        if (movieList != null) {
            showUi(jsonString);
        }*/

        Movie movie = null;
        try {
            movie = TmdbJsonUtils.parseMovieFrom(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (movie != null) {
            showUi(movie);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void showUi(Movie movie) {
        tv.setText(movie.getPosterPath());
        Uri uri = null;
        try {

            uri = NetworkUtils.getImageUriFor(movie.getPosterPath());
            Log.d(TAG, uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Picasso.with(this).load(uri).into(iv);
    }

    static class MovieLoader extends AsyncTaskLoader<String> {
        MovieLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public String loadInBackground() {
            URL url = NetworkUtils.getMovieDetailsQueryURL(getContext(), "211672");
            String jsonString = null;
            try {
                jsonString = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.d("MainActivity", e.getMessage());
                e.printStackTrace();
            }
            return jsonString;
        }
    }
}
