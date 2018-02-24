package eu.captaincode.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents Movie object from The Movie Database.
 */
public class Movie implements Parcelable {
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String title;
    private String posterPath;
    private String overView;
    private double voteAverage;
    private String backdropPath;
    private String date;

    public Movie(String title, String posterPath, String overView, double voteAverage,
                 String backdropPath, String date) {
        this.title = title;
        this.posterPath = posterPath;
        this.overView = overView;
        this.voteAverage = voteAverage;
        this.backdropPath = backdropPath;
        this.date = date;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        overView = in.readString();
        voteAverage = in.readDouble();
        backdropPath = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overView);
        dest.writeDouble(voteAverage);
        dest.writeString(backdropPath);
        dest.writeString(date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
