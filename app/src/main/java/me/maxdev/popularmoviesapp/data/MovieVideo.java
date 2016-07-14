package me.maxdev.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

@SuppressWarnings("PMD.GodClass")
public class MovieVideo implements Parcelable {

    public static final Parcelable.Creator<MovieVideo> CREATOR = new Parcelable.Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel source) {
            return new MovieVideo(source);
        }

        @Override
        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };

    private static final String SITE_YOUTUBE = "YouTube";

    @SerializedName("id")
    private String videoId;
    @SerializedName("iso_639_1")
    private String languageCode;
    @SerializedName("iso_3166_1")
    private String countryCode;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;
    @SerializedName("size")
    private int size;
    @SerializedName("type")
    private String type;

    public MovieVideo(String videoId) {
        this.videoId = videoId;
    }

    protected MovieVideo(Parcel in) {
        this.videoId = in.readString();
        this.languageCode = in.readString();
        this.countryCode = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readInt();
        this.type = in.readString();
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isYoutubeVideo() {
        return site.toLowerCase(Locale.US).equals(SITE_YOUTUBE.toLowerCase(Locale.US));
    }

    //CHECKSTYLE:OFF
    @Override
    @SuppressWarnings("PMD")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieVideo video = (MovieVideo) o;

        if (size != video.size) return false;
        if (videoId != null ? !videoId.equals(video.videoId) : video.videoId != null) return false;
        if (languageCode != null ? !languageCode.equals(video.languageCode) : video.languageCode != null) return false;
        if (countryCode != null ? !countryCode.equals(video.countryCode) : video.countryCode != null) return false;
        if (key != null ? !key.equals(video.key) : video.key != null) return false;
        if (name != null ? !name.equals(video.name) : video.name != null) return false;
        if (site != null ? !site.equals(video.site) : video.site != null) return false;
        return type != null ? type.equals(video.type) : video.type == null;

    }

    @Override
    @SuppressWarnings("PMD")
    public int hashCode() {
        int result = videoId != null ? videoId.hashCode() : 0;
        result = 31 * result + (languageCode != null ? languageCode.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (site != null ? site.hashCode() : 0);
        result = 31 * result + size;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
    //CHECKSTYLE:ON

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videoId);
        dest.writeString(this.languageCode);
        dest.writeString(this.countryCode);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeInt(this.size);
        dest.writeString(this.type);
    }

}
