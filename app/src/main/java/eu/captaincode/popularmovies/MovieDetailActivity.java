package eu.captaincode.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import eu.captaincode.popularmovies.adapters.VideoListAdapter;
import eu.captaincode.popularmovies.databinding.ActivityMovieDetailBinding;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.model.Video;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.TmdbJsonUtils;
import eu.captaincode.popularmovies.utilities.VideoListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private static final int PERCENT_MULTIPLIER_VOTE_AVERAGE = 10;

    private ActivityMovieDetailBinding movieDetailBinding;
    private Movie mMovie;
    private List<Video> mVideoList = new ArrayList<>();

    VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_movie_detail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            mMovie = startingIntent.getParcelableExtra(MainActivity.EXTRA_KEY_MOVIE);
        }
        if (mMovie == null) {
            Log.d(TAG, "No movie object passed");
            return;
        }

        videoListAdapter = new VideoListAdapter(this, mVideoList);
        movieDetailBinding.rvVideosMovieDetail.setAdapter(videoListAdapter);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        movieDetailBinding.rvVideosMovieDetail.setLayoutManager(linearLayoutManager);

        showMovieDetails();
    }

    private void showMovieDetails() {
        movieDetailBinding.setMovie(mMovie);

        Uri backdropUri = NetworkUtils.getBackdropImageUriFor(this, mMovie.getBackdropPath());
        Picasso.with(this).load(backdropUri).into(movieDetailBinding.ivMovieDetailBackdrop);
        movieDetailBinding.ivMovieDetailBackdrop.setContentDescription(getString(R.string
                .main_backdrop_iv_content_description, mMovie.getTitle()));

        Uri posterUri = NetworkUtils.getPosterImageUriFor(this, mMovie.getPosterPath());
        Picasso.with(this).load(posterUri).into(movieDetailBinding.ivPosterImageMovieDetail);
        movieDetailBinding.ivPosterImageMovieDetail.setContentDescription(getString(R.string
                .main_poster_iv_content_description, mMovie.getTitle()));

        String releaseDateString = mMovie.getDate();
        try {
            Date releaseDate = new SimpleDateFormat(TmdbJsonUtils.DATE_FORMAT_TMDB, Locale
                    .getDefault()).parse(releaseDateString);
            String localeReleaseDate = DateFormat.getDateInstance(DateFormat.LONG)
                    .format(releaseDate);
            String labeledReleaseDate = getString(R.string.release_date_movie_detail,
                    localeReleaseDate);
            movieDetailBinding.tvReleaseDateMovieDetail.setText(labeledReleaseDate);
        } catch (ParseException e) {
            String labeledReleaseDate = getString(R.string.release_date_movie_detail,
                    releaseDateString);
            movieDetailBinding.tvReleaseDateMovieDetail.setText(labeledReleaseDate);
            e.printStackTrace();
        }

        movieDetailBinding.tvVoteAverageNumberMovieDetail.setText(String.valueOf(mMovie
                .getVoteAverage()));

        movieDetailBinding.circularProgressbar.setProgress((int) (mMovie.getVoteAverage() *
                PERCENT_MULTIPLIER_VOTE_AVERAGE));

        showVideos();
    }

    private void showVideos() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.BASE_URL_TMDB)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final NetworkUtils.TmdbServiceApi service = retrofit.create(NetworkUtils.TmdbServiceApi.class);
        final Call<VideoListResponse> call = service.getVideosForMovie(mMovie.getId(),
                NetworkUtils.getSystemLanguageTag());

        call.enqueue(new Callback<VideoListResponse>() {
            @Override
            public void onResponse(@NonNull final Call<VideoListResponse> call,
                                   @NonNull final Response<VideoListResponse> response) {
                if (response.isSuccessful()) {
                    VideoListResponse videoListResponse = response.body();
                    if (videoListResponse != null) {
                        List<Video> videoList = videoListResponse.getVideoList();

                        for (Iterator<Video> iterator = videoList.iterator(); iterator.hasNext(); ) {
                            Video video = iterator.next();
                            if (!video.getType().equals("Trailer")) {
                                iterator.remove();
                            }
                            // Temporary test
                            videoListAdapter.setData(videoList);
                        }
                    } else {
                        Log.d(TAG, "No videos belong to this Movie");
                    }
                } else {
                    Log.d(TAG, "Failed to download movie videos");
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoListResponse> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
