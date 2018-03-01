package eu.captaincode.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.captaincode.popularmovies.R;
import eu.captaincode.popularmovies.model.Review;

/**
 * Exposes a list of Videos from a list of videos to a
 * {@link android.support.v7.widget.RecyclerView}.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private Context mContext;
    private List<Review> mReviewList;

    public ReviewListAdapter(Context context, List<Review> reviewList) {
        this.mContext = context;
        this.mReviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder viewHolder, int position) {
        viewHolder.authorTextView.setText(mReviewList.get(position).getAuthor());
        String decodedContent = mReviewList.get(position).getContent();
        viewHolder.contentTextView.setText(decodedContent);
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public void setData(List<Review> reviewList) {
        if (reviewList == null) {
            return;
        }
        mReviewList.clear();
        mReviewList.addAll(reviewList);
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView authorTextView;
        private TextView contentTextView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.tv_author_review_list_item);
            contentTextView = itemView.findViewById(R.id.tv_content_review_list_item);
        }
    }
}
