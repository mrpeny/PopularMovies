package eu.captaincode.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import eu.captaincode.popularmovies.databinding.ActivityMovieDetailBinding;
import eu.captaincode.popularmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailBinding movieDetailBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_movie_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            mMovie = startingIntent.getParcelableExtra(MainActivity.EXTRA_KEY_MOVIE);
        }

        movieDetailBinding.setMovie(mMovie);
    }
}
