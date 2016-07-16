package me.maxdev.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieReview implements Parcelable {

    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    @SerializedName("id")
    private String reviewId;

    @SerializedName("author")
    private String author;

    @SerializedName("url")
    private String reviewUrl;

    @SerializedName("content")
    private String content;

    public MovieReview(String reviewId) {
        this.reviewId = reviewId;
    }

    protected MovieReview(Parcel in) {
        this.reviewId = in.readString();
        this.author = in.readString();
        this.reviewUrl = in.readString();
        this.content = in.readString();
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //CHECKSTYLE:OFF
    @Override
    @SuppressWarnings("PMD")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieReview review = (MovieReview) o;

        if (reviewId != null ? !reviewId.equals(review.reviewId) : review.reviewId != null) return false;
        if (author != null ? !author.equals(review.author) : review.author != null) return false;
        if (reviewUrl != null ? !reviewUrl.equals(review.reviewUrl) : review.reviewUrl != null) return false;
        return content != null ? content.equals(review.content) : review.content == null;

    }

    @Override
    @SuppressWarnings("PMD")
    public int hashCode() {
        int result = reviewId != null ? reviewId.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (reviewUrl != null ? reviewUrl.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
    //CHECKSTYLE:ON

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reviewId);
        dest.writeString(this.author);
        dest.writeString(this.reviewUrl);
        dest.writeString(this.content);
    }
}
