package eu.captaincode.popularmovies.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import eu.captaincode.popularmovies.OverviewFragment;
import eu.captaincode.popularmovies.R;
import eu.captaincode.popularmovies.ReviewsFragment;
import eu.captaincode.popularmovies.VideosFragment;
import eu.captaincode.popularmovies.model.Movie;

/**
 * Handles changing pages within the ViewPager and constructs Appropriate Fragments to show.
 */

public class CategoryFragmentAdapter extends FragmentPagerAdapter {
    public static final String KEY_MOVIE = "movie";

    private static final int NUM_PAGES = 3;
    private String[] pageTitles = new String[NUM_PAGES];
    private Movie mMovie;

    public CategoryFragmentAdapter(FragmentManager fm, Context context, Movie movie) {
        super(fm);
        this.mMovie = movie;
        pageTitles[0] = context.getString(R.string.movie_detail_overview_page_title);
        pageTitles[1] = context.getString(R.string.movie_detail_trailers_page_title);
        pageTitles[2] = context.getString(R.string.movie_detail_reviews_page_title);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_MOVIE, mMovie);

        switch (position) {
            case 0:
                OverviewFragment overviewFragment = new OverviewFragment();
                overviewFragment.setArguments(args);
                return overviewFragment;
            case 1:
                VideosFragment videosFragment = new VideosFragment();
                videosFragment.setArguments(args);
                return videosFragment;
            case 2:
                ReviewsFragment reviewsFragment = new ReviewsFragment();
                reviewsFragment.setArguments(args);
                return reviewsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
