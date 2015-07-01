package net.kechol.udacity.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ksuzuki on 7/1/15.
 */
public class Artist implements Parcelable {
    public String id;
    public String name;
    public String image_url;

    public Artist() {
        super();
    }

    public Artist(kaaes.spotify.webapi.android.models.Artist sArtist) {
        this.id = sArtist.id;
        this.name = sArtist.name;
        if (sArtist.images != null && sArtist.images.size() > 0) {
            this.image_url = sArtist.images.get(0).url;
        }
    }

    public Artist(Parcel src) {
        id = src.readString();
        name = src.readString();
        image_url = src.readString();
    }

    @Override
    public String toString() {
        return String.format("Artist: %s (%s)", name, id);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(image_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
