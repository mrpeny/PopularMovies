package eu.captaincode.popularmovies;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.adapters.MovieListAdapter;
import eu.captaincode.popularmovies.data.MovieContract;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.MovieObjectRelationMapper;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.TmdbJsonUtils;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieListAdapter.OnMovieClickListener {
    public static final String EXTRA_KEY_MOVIE = "movie";

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LIST_WEB_LOADER_ID = 42;
    private static final int MOVIE_LIST_CURSOR_LOADER_ID = 28;
    private static int sPosition = RecyclerView.NO_POSITION;

    RecyclerView mRecyclerView;
    private List<Movie> mMovieList = new ArrayList<>();
    private MovieListAdapter mMovieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_main_movie_list);
        mMovieListAdapter = new MovieListAdapter(this, mMovieList, this);
        mRecyclerView.setAdapter(mMovieListAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.grid_span_count_main));
        mRecyclerView.setLayoutManager(layoutManager);

        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        initLoaderByPreference();
    }

    private void initLoaderByPreference() {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.preference_key_sort_by),
                getString(R.string.preference_option_sort_by_popular_value));

        if (sortBy.equals(getString(R.string.preference_option_sort_by_favorites_value))) {
            getSupportLoaderManager().initLoader(MOVIE_LIST_CURSOR_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().initLoader(MOVIE_LIST_WEB_LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();

        int id = item.getItemId();
        if (id == R.id.menu_sort_popular) {
            sharedPreferencesEditor.putString(getString(R.string.preference_key_sort_by),
                    getString(R.string.preference_option_sort_by_popular_value));
            sharedPreferencesEditor.apply();
            restartLoaderByPreference();
            return true;
        } else if (id == R.id.menu_sort_top_rated) {
            sharedPreferencesEditor.putString(getString(R.string.preference_key_sort_by),
                    getString(R.string.preference_option_sort_by_top_rated_value));
            sharedPreferencesEditor.apply();
            restartLoaderByPreference();
            return true;
        } else if (id == R.id.menu_sort_favorite) {
            sharedPreferencesEditor.putString(getString(R.string.preference_key_sort_by),
                    getString(R.string.preference_option_sort_by_favorites_value));
            sharedPreferencesEditor.apply();
            restartLoaderByPreference();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void restartLoaderByPreference() {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.preference_key_sort_by),
                getString(R.string.preference_option_sort_by_popular_value));

        if (sortBy.equals(getString(R.string.preference_option_sort_by_favorites_value))) {
            getSupportLoaderManager().restartLoader(MOVIE_LIST_CURSOR_LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(MOVIE_LIST_WEB_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case MOVIE_LIST_WEB_LOADER_ID:
                return new MovieWebLoader(this);
            case MOVIE_LIST_CURSOR_LOADER_ID:
                return new MovieCursorLoader(this);
            default:
                throw new RuntimeException("Loader not Implemented with id: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieList) {
        if (movieList == null || movieList.isEmpty()) {
            findViewById(R.id.tv_empty_view).setVisibility(View.VISIBLE);
            findViewById(R.id.rv_main_movie_list).setVisibility(View.GONE);
            return;
        }
        mMovieList = movieList;
        updateUi();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieListAdapter.setData(null);
    }

    private void updateUi() {
        findViewById(R.id.tv_empty_view).setVisibility(View.GONE);
        findViewById(R.id.rv_main_movie_list).setVisibility(View.VISIBLE);
        mMovieListAdapter.setData(mMovieList);
        if (sPosition == RecyclerView.NO_POSITION) sPosition = 0;
        mRecyclerView.scrollToPosition(sPosition);
    }

    @Override
    public void onMovieClick(int position) {
        sPosition = position;
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(EXTRA_KEY_MOVIE, mMovieList.get(position));
        movieDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_movie_detail, movieDetailFragment)
                .commit();
    }

    private static class MovieWebLoader extends AsyncTaskLoader<List<Movie>> {
        MovieWebLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public List<Movie> loadInBackground() {
            URL url = NetworkUtils.getMovieListQueryUrl(getContext());
            String jsonString = null;
            try {
                jsonString = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            if (jsonString == null) {
                return null;
            }

            List<Movie> movieList = null;
            try {
                movieList = TmdbJsonUtils.parseMovieListFrom(jsonString);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            if (movieList == null) {
                return null;
            }
            return movieList;
        }
    }

    private static class MovieCursorLoader extends AsyncTaskLoader<List<Movie>> {
        MovieCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public List<Movie> loadInBackground() {
            Cursor cursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null) {
                return null;
            }

            List<Movie> movieList = MovieObjectRelationMapper.toMovieList(cursor);
            cursor.close();
            return movieList;
        }
    }
}
