package net.kechol.udacity.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ksuzuki on 7/1/15.
 */
public class Track implements Parcelable {
    public String id;
    public String name;
    public String artist_id;
    public String album_name;
    public String image_url;
    public String preview_url;

    public Track() {
        super();
    }

    public Track(kaaes.spotify.webapi.android.models.Track sTrack) {
        this.id = sTrack.id;
        this.name = sTrack.name;
        this.album_name = sTrack.album.name;
        this.preview_url = sTrack.preview_url;

        if (sTrack.album.images != null && sTrack.album.images.size() > 0) {
            this.image_url = sTrack.album.images.get(0).url;
        }

        if (sTrack.artists != null && sTrack.artists.size() > 0) {
            this.artist_id = sTrack.artists.get(0).id;
        }
    }

    public Track(Parcel src) {
        id = src.readString();
        name = src.readString();
        artist_id = src.readString();
        album_name = src.readString();
        image_url = src.readString();
        preview_url = src.readString();
    }

    @Override
    public String toString() {
        return String.format("Track: %s (%s)", name, id);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(artist_id);
        dest.writeString(album_name);
        dest.writeString(image_url);
        dest.writeString(preview_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
