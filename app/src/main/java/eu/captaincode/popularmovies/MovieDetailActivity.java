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
import android.view.View;
import android.widget.Toast;

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
import eu.captaincode.popularmovies.data.MovieContract;
import eu.captaincode.popularmovies.databinding.ActivityMovieDetailBinding;
import eu.captaincode.popularmovies.handler.FavoritesAsyncQueryHandler;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.model.Video;
import eu.captaincode.popularmovies.utilities.MovieObjectRelationMapper;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.TmdbJsonUtils;
import eu.captaincode.popularmovies.utilities.VideoListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity implements FavoritesAsyncQueryHandler.AsyncQueryListener {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final int PERCENT_MULTIPLIER_VOTE_AVERAGE = 10;

    private Movie mMovie;
    private List<Video> mVideoList = new ArrayList<>();

    private VideoListAdapter mVideoListAdapter;
    private FavoritesAsyncQueryHandler mQueryHandler;
    private ActivityMovieDetailBinding mMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

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

        mVideoListAdapter = new VideoListAdapter(this, mVideoList);
        mMovieDetailBinding.rvVideosMovieDetail.setAdapter(mVideoListAdapter);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mMovieDetailBinding.rvVideosMovieDetail.setLayoutManager(linearLayoutManager);

        mQueryHandler = new FavoritesAsyncQueryHandler(getContentResolver(), this);

        showMovieDetails();
    }

    private void showMovieDetails() {
        mMovieDetailBinding.setMovie(mMovie);

        Uri backdropUri = NetworkUtils.getBackdropImageUriFor(this, mMovie.getBackdropPath());
        Picasso.with(this).load(backdropUri).into(mMovieDetailBinding.ivMovieDetailBackdrop);
        mMovieDetailBinding.ivMovieDetailBackdrop.setContentDescription(getString(R.string
                .main_backdrop_iv_content_description, mMovie.getTitle()));

        Uri posterUri = NetworkUtils.getPosterImageUriFor(this, mMovie.getPosterPath());
        Picasso.with(this).load(posterUri).into(mMovieDetailBinding.ivPosterImageMovieDetail);
        mMovieDetailBinding.ivPosterImageMovieDetail.setContentDescription(getString(R.string
                .main_poster_iv_content_description, mMovie.getTitle()));

        String releaseDateString = mMovie.getDate();
        try {
            Date releaseDate = new SimpleDateFormat(TmdbJsonUtils.DATE_FORMAT_TMDB, Locale
                    .getDefault()).parse(releaseDateString);
            String localeReleaseDate = DateFormat.getDateInstance(DateFormat.LONG)
                    .format(releaseDate);
            String labeledReleaseDate = getString(R.string.release_date_movie_detail,
                    localeReleaseDate);
            mMovieDetailBinding.tvReleaseDateMovieDetail.setText(labeledReleaseDate);
        } catch (ParseException e) {
            String labeledReleaseDate = getString(R.string.release_date_movie_detail,
                    releaseDateString);
            mMovieDetailBinding.tvReleaseDateMovieDetail.setText(labeledReleaseDate);
            e.printStackTrace();
        }

        mMovieDetailBinding.tvVoteAverageNumberMovieDetail.setText(String.valueOf(mMovie
                .getVoteAverage()));

        mMovieDetailBinding.circularProgressbar.setProgress((int) (mMovie.getVoteAverage() *
                PERCENT_MULTIPLIER_VOTE_AVERAGE));

        // TODO: Set movie as favorite if it exist the favorite list DB table
        if (mMovie.isFavorite()) {
            mMovieDetailBinding.fab.setImageResource(R.drawable.ic_favorite_true_24dp);
        }

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
                            mVideoListAdapter.setData(videoList);
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

    public void onFabClick(View view) {
        if (!mMovie.isFavorite()) {
            mMovie.setFavorite(true);
            saveMovieToDatabase();
        } else if (mMovie.isFavorite()) {
            mMovie.setFavorite(false);
            deleteMovieFromDatabase();
        }
    }

    private void deleteMovieFromDatabase() {
        mQueryHandler.startDelete(1, null, MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_ID_TMDB + "= ?",
                new String[]{String.valueOf(mMovie.getId())});
    }

    private void saveMovieToDatabase() {
        mQueryHandler.startInsert(1, null,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieObjectRelationMapper.toContentValues(mMovie));
    }

    @Override
    public void onInsertComplete() {
        mMovieDetailBinding.fab.setImageResource(R.drawable.ic_favorite_true_24dp);
        Toast.makeText(this, mMovie.getTitle() + " is marked as favorite", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteComplete() {
        mMovieDetailBinding.fab.setImageResource(R.drawable.ic_favorite_false_24dp);
        Toast.makeText(this, mMovie.getTitle() + " is deleted from favorites", Toast.LENGTH_SHORT).show();
    }
}
