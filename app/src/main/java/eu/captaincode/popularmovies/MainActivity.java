package eu.captaincode.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.adapters.MovieListAdapter;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.TmdbJsonUtils;

/**
 * Makes testing on app components in this development phase (MrPeny 2018.02.20.)
 */
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, MovieListAdapter.OnMovieClickListener {
    public static final String EXTRA_KEY_MOVIE = "movie-name";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LIST_LOADER_ID = 42;

    private List<Movie> mMovieList = new ArrayList<>();
    private MovieListAdapter mMovieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.rv_main_movie_list);
        mMovieListAdapter = new MovieListAdapter(this, mMovieList, this);
        mRecyclerView.setAdapter(mMovieListAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.main_grid_span_count));
        mRecyclerView.setLayoutManager(layoutManager);

        getSupportLoaderManager().initLoader(MOVIE_LIST_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MovieListSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonString) {
        if (jsonString == null) {
            Toast.makeText(this, R.string.main_internet_error_toast_message,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mMovieList = TmdbJsonUtils.parseMovieListFrom(jsonString);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        if (mMovieList != null) {
            updateUi();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        mMovieListAdapter.setData(null);
    }

    private void updateUi() {
        mMovieListAdapter.setData(mMovieList);
    }

    @Override
    public void onMovieClick(int position) {
        Intent startMovieDetailIntent = new Intent(this, MovieDetailActivity.class);
        startMovieDetailIntent.putExtra(EXTRA_KEY_MOVIE, mMovieList.get(position));
        startActivity(startMovieDetailIntent);
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
            URL url = NetworkUtils.getMovieListQueryUrl(getContext());
            String jsonString = null;
            try {
                jsonString = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return jsonString;
        }
    }
}
