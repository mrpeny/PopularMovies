package eu.captaincode.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;

import eu.captaincode.popularmovies.adapters.CategoryFragmentAdapter;
import eu.captaincode.popularmovies.data.MovieContract;
import eu.captaincode.popularmovies.databinding.ActivityMovieDetailBinding;
import eu.captaincode.popularmovies.handler.FavoritesAsyncQueryHandler;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.MovieObjectRelationMapper;
import eu.captaincode.popularmovies.utilities.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity implements FavoritesAsyncQueryHandler.AsyncQueryListener {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private Movie mMovie;
    private FavoritesAsyncQueryHandler mQueryHandler;
    private ActivityMovieDetailBinding mMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            mMovie = startingIntent.getParcelableExtra(MainActivity.EXTRA_KEY_MOVIE);
        }
        if (mMovie == null) {
            Log.d(TAG, "No movie object passed, nothing to show.");
            return;
        }

        Toolbar toolbar = mMovieDetailBinding.toolbarMovieDetail;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Object used to perform CRUD operations on the DB
        mQueryHandler = new FavoritesAsyncQueryHandler(getContentResolver(), this);

        CategoryFragmentAdapter categoryFragmentAdapter = new CategoryFragmentAdapter(
                getSupportFragmentManager(), this, mMovie);
        ViewPager viewPager = mMovieDetailBinding.viewPager;
        viewPager.setAdapter(categoryFragmentAdapter);
        TabLayout tabLayout = mMovieDetailBinding.tabLayout;
        tabLayout.setupWithViewPager(viewPager);

        showMovieDetails();
    }

    private void showMovieDetails() {
        setTitle(mMovie.getTitle());
        showBackdropImage();
        checkIfFavorite();
    }

    private void showBackdropImage() {
        Uri backdropUri = NetworkUtils.getBackdropImageUriFor(this, mMovie.getBackdropPath());
        Picasso.with(this).load(backdropUri).placeholder(R.drawable.poster_placeholder)
                .into(mMovieDetailBinding.ivMovieDetailBackdrop);
        mMovieDetailBinding.ivMovieDetailBackdrop.setContentDescription(getString(R.string
                .main_backdrop_iv_content_description, mMovie.getTitle()));
    }

    /*
     * Run a background query to check if mMovie is part of favorites. The result is evaluated
     * onQueryComplete(Cursor cursor) callback method below.
     */
    private void checkIfFavorite() {
        mQueryHandler.startQuery(0, null, MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_ID_TMDB + " = ?",
                new String[]{String.valueOf(mMovie.getId())},
                null);
    }

    @Override
    public void onQueryComplete(Cursor cursor) {
        if (cursor.getCount() > 0) {
            mMovie.setFavorite(true);
            mMovieDetailBinding.fab.setImageResource(R.drawable.ic_favorite_true_24dp);
        }
    }

    public void onFabClick(View view) {
        if (!mMovie.isFavorite()) {
            mMovie.setFavorite(true);
            saveMovieToDatabase();
        } else if (mMovie.isFavorite()) {
            mMovie.setFavorite(false);
            deleteMovieFromDatabase();
        }
    }

    private void saveMovieToDatabase() {
        ContentValues movieInContentValues = MovieObjectRelationMapper.toContentValues(mMovie);
        mQueryHandler.startInsert(1, null, MovieContract.MovieEntry.CONTENT_URI,
                movieInContentValues);
    }

    private void deleteMovieFromDatabase() {
        String selection = MovieContract.MovieEntry.COLUMN_ID_TMDB + "= ?";
        String[] selectionArgs = {String.valueOf(mMovie.getId())};
        mQueryHandler.startDelete(1, null, MovieContract.MovieEntry.CONTENT_URI, selection,
                selectionArgs);
    }

    // Callback methods invoked by mQueryHandler on completion of save and delete movie method
    @Override
    public void onInsertComplete() {
        mMovieDetailBinding.fab.setImageResource(R.drawable.ic_favorite_true_24dp);
        Snackbar.make(mMovieDetailBinding.clMainMovieDetail,
                R.string.movie_detail_added_to_favorites, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteComplete() {
        mMovieDetailBinding.fab.setImageResource(R.drawable.ic_favorite_false_24dp);
        Snackbar.make(mMovieDetailBinding.clMainMovieDetail,
                R.string.movie_detail_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
    }
}
