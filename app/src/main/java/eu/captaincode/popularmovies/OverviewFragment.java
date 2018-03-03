package eu.captaincode.popularmovies;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import eu.captaincode.popularmovies.adapters.CategoryFragmentAdapter;
import eu.captaincode.popularmovies.databinding.FragmentOverviewBinding;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.TmdbJsonUtils;

/**
 * Holds general information and overview about a movie.
 */

public class OverviewFragment extends Fragment {
    private static final int PERCENT_MULTIPLIER_VOTE_AVERAGE = 10;
    private static final String TAG = OverviewFragment.class.getSimpleName();

    private FragmentOverviewBinding fragmentOverviewBinding;

    private Movie mMovie;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentOverviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview,
                container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d(TAG, "No movie object passed to Fragment.");
            return null;
        }
        mMovie = bundle.getParcelable(CategoryFragmentAdapter.KEY_MOVIE);

        showMovieDetails();

        return fragmentOverviewBinding.getRoot();
    }

    private void showMovieDetails() {
        fragmentOverviewBinding.setMovie(mMovie);
        showPosterImage();
        showReleaseDate();
        fragmentOverviewBinding.tvVoteAverageNumberMovieDetail.setText(String.valueOf(mMovie
                .getVoteAverage()));
        fragmentOverviewBinding.circularProgressbar.setProgress((int) (mMovie.getVoteAverage() *
                PERCENT_MULTIPLIER_VOTE_AVERAGE));
    }

    private void showPosterImage() {
        Uri posterUri = NetworkUtils.getPosterImageUriFor(getContext(), mMovie.getPosterPath());
        Picasso.with(getContext()).load(posterUri).into(fragmentOverviewBinding.ivPosterImageMovieDetail);
        fragmentOverviewBinding.ivPosterImageMovieDetail.setContentDescription(getString(R.string
                .main_poster_iv_content_description, mMovie.getTitle()));
    }

    private void showReleaseDate() {
        String releaseDateString = mMovie.getDate();
        try {
            Date releaseDate = new SimpleDateFormat(TmdbJsonUtils.DATE_FORMAT_TMDB, Locale
                    .getDefault()).parse(releaseDateString);
            String localeReleaseDate = DateFormat.getDateInstance(DateFormat.LONG)
                    .format(releaseDate);
            String labeledReleaseDate = getString(R.string.release_date_movie_detail,
                    localeReleaseDate);
            fragmentOverviewBinding.tvReleaseDateMovieDetail.setText(labeledReleaseDate);
        } catch (ParseException e) {
            String labeledReleaseDate = getString(R.string.release_date_movie_detail,
                    releaseDateString);
            fragmentOverviewBinding.tvReleaseDateMovieDetail.setText(labeledReleaseDate);
            e.printStackTrace();
        }
    }
}
