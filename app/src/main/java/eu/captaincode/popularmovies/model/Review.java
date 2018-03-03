package eu.captaincode.popularmovies.model;

/**
 * Represents a Review as POJO received from The Movie DB server as JSON string. Used for
 * deserialization purposes for GSON.
 */

public class Review {

    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}