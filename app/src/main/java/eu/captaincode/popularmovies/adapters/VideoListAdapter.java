package eu.captaincode.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.captaincode.popularmovies.R;
import eu.captaincode.popularmovies.model.Video;
import eu.captaincode.popularmovies.utilities.NetworkUtils;

/**
 * VideoListAdapter exposes a list of Videos from a list of videos to a
 * {@link android.support.v7.widget.RecyclerView}.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {
    private Context mContext;
    private List<Video> mVideoList;

    public VideoListAdapter(Context mContext, List<Video> mVideoList) {
        this.mContext = mContext;
        this.mVideoList = mVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, final int position) {
        final String videoId = mVideoList.get(position).getKey();
        holder.setVideoId(videoId);
        Uri videoThumbnailUri = NetworkUtils.getYouTubeThumbnailImageFor(videoId);
        Picasso.with(mContext).load(videoThumbnailUri).into(holder.thumbNailImageView);

        holder.nameTextView.setText(mVideoList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public void setData(List<Video> videoList) {
        if (videoList == null) {
            return;
        }
        mVideoList.clear();
        mVideoList.addAll(videoList);
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        static final String VND_YOUTUBE = "vnd.youtube:";
        private ImageView thumbNailImageView;
        private String mVideoId;
        private TextView nameTextView;

        VideoViewHolder(View itemView) {
            super(itemView);
            thumbNailImageView = itemView.findViewById(R.id.iv_video_list_item_thumbnail);
            nameTextView = itemView.findViewById(R.id.tv_video_list_item_name);
            itemView.setOnClickListener(this);
        }

        void setVideoId(String movieId) {
            this.mVideoId = movieId;
        }

        @Override
        public void onClick(View v) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VND_YOUTUBE + mVideoId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(NetworkUtils.getYouTubeVideoUrlFor(mVideoId).toString()));
            try {
                mContext.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                mContext.startActivity(webIntent);
            }
        }
    }
}
