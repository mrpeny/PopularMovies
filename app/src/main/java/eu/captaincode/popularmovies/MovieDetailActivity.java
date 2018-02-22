package eu.captaincode.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import eu.captaincode.popularmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView titleTextView = findViewById(R.id.tv_movie_title_movie_detail);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            // Temporary testing
            mMovie = startingIntent.getParcelableExtra(MainActivity.EXTRA_KEY_MOVIE);
        }

        titleTextView.setText(mMovie.getTitle());
    }
}
