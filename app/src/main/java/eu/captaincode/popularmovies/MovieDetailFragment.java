package eu.captaincode.popularmovies;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import eu.captaincode.popularmovies.adapters.CategoryFragmentAdapter;
import eu.captaincode.popularmovies.databinding.FragmentMovieDetailBinding;
import eu.captaincode.popularmovies.handler.FavoritesAsyncQueryHandler;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.NetworkUtils;

/**
 * Holds reusable methods for showing Movie details
 */

public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private Movie mMovie;
    private FavoritesAsyncQueryHandler mQueryHandler;
    private FragmentMovieDetailBinding mMovieDetailFragmentBinding;


    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMovieDetailFragmentBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_movie_detail, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovie = bundle.getParcelable(MainActivity.EXTRA_KEY_MOVIE);
        }
        if (mMovie == null) {
            Log.d(TAG, "No movie object passed, nothing to show.");
            return null;
        }

        Toolbar toolbar = mMovieDetailFragmentBinding.toolbarMovieDetail;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        CategoryFragmentAdapter categoryFragmentAdapter = new CategoryFragmentAdapter(
                getActivity().getSupportFragmentManager(), getContext(), mMovie);
        ViewPager viewPager = mMovieDetailFragmentBinding.viewPager;
        viewPager.setAdapter(categoryFragmentAdapter);
        TabLayout tabLayout = mMovieDetailFragmentBinding.tabLayout;
        tabLayout.setupWithViewPager(viewPager);

        showMovieDetails();

        return mMovieDetailFragmentBinding.getRoot();

    }

    private void showMovieDetails() {
        getActivity().setTitle(mMovie.getTitle());
        showBackdropImage();
    }

    private void showBackdropImage() {
        Uri backdropUri = NetworkUtils.getBackdropImageUriFor(getContext(), mMovie.getBackdropPath());
        Picasso.with(getContext()).load(backdropUri).placeholder(R.drawable.poster_placeholder)
                .into(mMovieDetailFragmentBinding.ivMovieDetailBackdrop);
        mMovieDetailFragmentBinding.ivMovieDetailBackdrop.setContentDescription(getString(R.string
                .main_backdrop_iv_content_description, mMovie.getTitle()));
    }

}
