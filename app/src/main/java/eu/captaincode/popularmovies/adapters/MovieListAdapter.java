package eu.captaincode.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.captaincode.popularmovies.R;
import eu.captaincode.popularmovies.model.Movie;
import eu.captaincode.popularmovies.utilities.NetworkUtils;

/**
 * {@link MovieListAdapter} exposes a list of Movies
 * from a list of movies to a {@link android.support.v7.widget.RecyclerView}.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {
    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private Context mContext;
    private List<Movie> mMovieList;
    private OnMovieClickListener mOnMovieClickListener;

    public MovieListAdapter(Context context, List<Movie> movieList, OnMovieClickListener listener) {
        mContext = context;
        mMovieList = movieList;
        mOnMovieClickListener = listener;
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
        return new MovieListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder holder, int position) {
        String contentDescription = mContext.getString(R.string.main_poster_iv_content_description,
                mMovieList.get(position).getTitle());
        holder.posterImageView.setContentDescription(contentDescription);

        String posterPath = mMovieList.get(position).getPosterPath();
        Uri posterUri = NetworkUtils.getPosterImageUriFor(mContext, posterPath);
        Picasso.with(mContext).load(posterUri).into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) return 0;
        return mMovieList.size();
    }

    public void setData(List<Movie> movieList) {
        if (movieList == null) {
            return;
        }
        mMovieList.clear();
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }

    /**
     * Interface definition for a callback to be invoked when a Movie poster is clicked.
     */
    public interface OnMovieClickListener {
        void onMovieClick(int position);
    }

    class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView posterImageView;

        MovieListViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.iv_movie_list_item_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnMovieClickListener.onMovieClick(position);
        }
    }
}
