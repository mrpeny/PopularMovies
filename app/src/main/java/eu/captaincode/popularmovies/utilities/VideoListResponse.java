package eu.captaincode.popularmovies.utilities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.model.Video;

/**
 * Represents a List of videos as POJO received from The Movie DB server as JSON string. Used for
 * deserialization purposes for GSON.
 */

public final class VideoListResponse {
    @SerializedName("results")
    private List<Video> mVideoList = new ArrayList<>();

    public List<Video> getVideoList() {
        return mVideoList;
    }
}
