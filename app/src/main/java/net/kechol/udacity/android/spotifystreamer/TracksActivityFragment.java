package net.kechol.udacity.android.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Tracks> {

    private static final int SEARCH_TOP_TRACKS_TASK_LOADER_ID = 0;
    public static final String TRACK_URI = "TRACK_URI";

    private Uri mUri;
    private ArrayAdapter<Track> mTracksAdapter;

    public TracksActivityFragment() {
    }

    public interface Callback {
        public void onItemSelected(Uri contentUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(TracksActivityFragment.TRACK_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_tracks);
        mTracksAdapter = new TracksAdapter(getActivity(), R.layout.list_item_track);
        listView.setAdapter(mTracksAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = (Track) parent.getItemAtPosition(position);
                if (track != null) {
                    ((Callback) getActivity()).onItemSelected(
                            SpotifyContract.ArtistEntry.CONTENT_URI.buildUpon()
                                    .appendQueryParameter(SpotifyContract.ArtistEntry.COLUMN_NAME, track.id).build()
                    );
                }
            }
        });

        String artistId = mUri.getQueryParameter(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID);
        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID, artistId);
        getLoaderManager().initLoader(SEARCH_TOP_TRACKS_TASK_LOADER_ID, loaderArgs, TracksActivityFragment.this).forceLoad();

        return rootView;
    }

    @Override
    public Loader<Tracks> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            mTracksAdapter.clear();
            String artistId = args.getString(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID);
            Log.d("TracksActivityFragment", "onCreateLoader: " + artistId);
            return new SearchTopTracksTaskLoader(getActivity(), artistId);
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Tracks> loader) {
        mTracksAdapter.clear();
    }

    @Override
    public void onLoadFinished(Loader<Tracks> loader, Tracks data) {
        if (data == null || data.tracks == null) {
            Toast.makeText(getActivity(), "Some Error Occurred. Check connection?", Toast.LENGTH_SHORT).show();
            return;
        }

        for(Track track: data.tracks) {
            mTracksAdapter.add(track);
        }
    }

    static private class SearchTopTracksTaskLoader extends AsyncTaskLoader<Tracks> {

        private SpotifyService mService;
        private String mArtistId;

        public SearchTopTracksTaskLoader(Context context, String artistId) {
            super(context);
            mService = new SpotifyApi().getService();
            mArtistId = artistId;
        }

        @Override
        public Tracks loadInBackground() {
            try {
                Map<String, Object> options = new HashMap<String, Object>();
                options.put(SpotifyContract.TrackEntry.COLUMN_COUNTRY, SpotifyContract.TrackEntry.VALUE_COUNTRY_DEFAULT);
                Tracks tracks = mService.getArtistTopTrack(mArtistId, options);
                return tracks;
            } catch (RetrofitError error) {
                Log.e("SearchTopTracksTaskLoader", "RetrofitError: " + error.getMessage());
            }
            return null;
        }
    }
}
