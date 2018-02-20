package eu.captaincode.popularmovies;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import eu.captaincode.popularmovies.utilities.NetworkUtils;

/**
 * Makes testing on app components in this development phase (MrPeny 2018.02.20.)
 */
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.main_iv);

        getSupportLoaderManager().initLoader(42, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonString) {
        if (jsonString == null) {
            Toast.makeText(this, "Network Error has occured", Toast.LENGTH_SHORT).show();
        }
        tv.setText(jsonString);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

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
