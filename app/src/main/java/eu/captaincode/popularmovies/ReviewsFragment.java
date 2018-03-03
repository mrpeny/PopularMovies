package eu.captaincode.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.adapters.CategoryFragmentAdapter;
import eu.captaincode.popularmovies.adapters.ReviewListAdapter;
import eu.captaincode.popularmovies.databinding.FragmentReviewsBinding;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.model.Review;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.ReviewListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Holds reviews for a movie.
 */

public class ReviewsFragment extends Fragment {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private Movie mMovie;
    private FragmentReviewsBinding mFragmentReviewsBinding;
    private List<Review> mReviewList = new ArrayList<>();
    private ReviewListAdapter mReviewListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentReviewsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews,
                container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d(TAG, "No movie object passed to Fragment.");
            return null;
        }
        mMovie = bundle.getParcelable(CategoryFragmentAdapter.KEY_MOVIE);

        setupRecyclerView();
        showReviews();

        return mFragmentReviewsBinding.getRoot();
    }

    private void setupRecyclerView() {
        mReviewListAdapter = new ReviewListAdapter(getContext(), mReviewList);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mFragmentReviewsBinding.rvReviewsMovieDetail.getContext(),
                LinearLayoutManager.VERTICAL);
        mFragmentReviewsBinding.rvReviewsMovieDetail.addItemDecoration(dividerItemDecoration);
        mFragmentReviewsBinding.rvReviewsMovieDetail.setAdapter(mReviewListAdapter);
        mFragmentReviewsBinding.rvReviewsMovieDetail.setLayoutManager(linearLayoutManager);
    }

    private void showReviews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.BASE_URL_TMDB)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkUtils.TmdbServiceApi service = retrofit.create(NetworkUtils.TmdbServiceApi.class);
        Call<ReviewListResponse> call = service.getReviewsForMovie(mMovie.getId(),
                NetworkUtils.getSystemLanguageTag());

        call.enqueue(new Callback<ReviewListResponse>() {
            @Override
            public void onResponse(@NonNull final Call<ReviewListResponse> call,
                                   @NonNull final Response<ReviewListResponse> response) {
                if (response.isSuccessful()) {
                    ReviewListResponse reviewListResponse = response.body();
                    if (reviewListResponse != null) {
                        List<Review> reviewList = reviewListResponse.getmReviewList();

                        mReviewListAdapter.setData(reviewList);

                    } else {
                        Log.d(TAG, "No videos belong to this Movie");
                    }
                } else {
                    Log.d(TAG, "Failed to download movie videos");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewListResponse> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
