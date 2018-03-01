package eu.captaincode.popularmovies.utilities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import eu.captaincode.popularmovies.model.Review;

/**
 * Represents a List of Reviews as POJO received from The Movie DB server as JSON string. Used for
 * deserialization purposes for GSON.
 */

public class ReviewListResponse {
    @SerializedName("results")
    private List<Review> mReviewList = new ArrayList<>();

    public List<Review> getmReviewList() {
        return mReviewList;
    }
}
