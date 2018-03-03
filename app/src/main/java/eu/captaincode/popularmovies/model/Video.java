package eu.captaincode.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Video as POJO received from The Movie DB server as JSON string. Used for
 * deserialization purposes for GSON.
 */

public final class Video {

    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private String key;

    private String site;

    private String name;

    private String type;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
