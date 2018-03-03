package eu.captaincode.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.captaincode.popularmovies.adapters.CategoryFragmentAdapter;
import eu.captaincode.popularmovies.adapters.VideoListAdapter;
import eu.captaincode.popularmovies.databinding.FragmentTrailersBinding;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.model.Video;
import eu.captaincode.popularmovies.utilities.NetworkUtils;
import eu.captaincode.popularmovies.utilities.VideoListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Holds videos for a movie.
 */

public class VideosFragment extends Fragment {
    private static final String TAG = OverviewFragment.class.getSimpleName();
    private static final String VIDEO_TYPE_TRAILER = "Trailer";
    Movie mMovie;
    FragmentTrailersBinding mFragmentTrailersBinding;
    private List<Video> mVideoList = new ArrayList<>();
    private VideoListAdapter mVideoListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentTrailersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trailers,
                container, false);

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d(TAG, "No movie object passed to Fragment.");
            return null;
        }
        mMovie = bundle.getParcelable(CategoryFragmentAdapter.KEY_MOVIE);

        setupRecyclerView();
        loadVideos();

        return mFragmentTrailersBinding.getRoot();
    }

    private void setupRecyclerView() {
        mVideoListAdapter = new VideoListAdapter(getContext(), mVideoList);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mFragmentTrailersBinding.rvVideosMovieDetail.setAdapter(mVideoListAdapter);
        mFragmentTrailersBinding.rvVideosMovieDetail.setLayoutManager(linearLayoutManager);
    }

    private void loadVideos() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.BASE_URL_TMDB)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkUtils.TmdbServiceApi service = retrofit.create(NetworkUtils.TmdbServiceApi.class);
        Call<VideoListResponse> call = service.getVideosForMovie(mMovie.getId(),
                NetworkUtils.getSystemLanguageTag());

        call.enqueue(new Callback<VideoListResponse>() {
            @Override
            public void onResponse(@NonNull final Call<VideoListResponse> call,
                                   @NonNull final Response<VideoListResponse> response) {
                if (response.isSuccessful()) {
                    VideoListResponse videoListResponse = response.body();
                    if (videoListResponse != null) {
                        List<Video> videoList = videoListResponse.getVideoList();
                        videoList = selectTrailers(videoList);
                        if (videoList.isEmpty()) {
                            showEmptyView();
                        } else {
                            showVideoList();
                            mVideoListAdapter.setData(videoList);
                        }
                    } else {
                        showEmptyView();
                        Log.d(TAG, "No videos belong to this Movie");
                    }
                } else {
                    showEmptyView();
                    Log.d(TAG, "Failed to download movie videos");
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoListResponse> call, @NonNull Throwable t) {
                showEmptyView();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private List<Video> selectTrailers(List<Video> videoList) {
        for (Iterator<Video> iterator = videoList.iterator(); iterator.hasNext(); ) {
            Video video = iterator.next();
            if (!video.getType().equals(VIDEO_TYPE_TRAILER)) {
                iterator.remove();
            }
        }
        return videoList;
    }

    private void showVideoList() {
        mFragmentTrailersBinding.tvEmptyViewTrailersFragment.setVisibility(View.
                GONE);
        mFragmentTrailersBinding.rvVideosMovieDetail.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        mFragmentTrailersBinding.tvEmptyViewTrailersFragment.setVisibility(View.
                VISIBLE);
        mFragmentTrailersBinding.rvVideosMovieDetail.setVisibility(View.
                GONE);
    }
}
