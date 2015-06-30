package net.kechol.udacity.android.spotifystreamer;

import android.net.Uri;

/**
 * Created by ksuzuki on 6/28/15.
 */
public class SpotifyContract {

    public static final String CONTENT_AUTHORITY = "net.kechol.udacity.android.spotifystreamer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTIST = "artist";
    public static final String PATH_TRACK  = "track";


    public static final class ArtistEntry {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
        public static final String COLUMN_NAME = "name";
    }

    public static final class TrackEntry {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_COUNTRY = "country";

        public static final String VALUE_COUNTRY_DEFAULT = "US";
    }
}
