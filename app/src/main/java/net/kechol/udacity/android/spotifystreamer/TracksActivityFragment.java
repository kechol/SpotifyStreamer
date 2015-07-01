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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Track>> {

    private static final int SEARCH_TOP_TRACKS_TASK_LOADER_ID = 0;
    public static final String TRACK_URI = "TRACK_URI";

    private static final String STATE_TRACKS_LIST = "mTracksList";
    private static final String STATE_ARTIST_ID = "mArtistId";

    private Uri mUri;
    private String mArtistId;
    private List<Track> mTracksList;
    private TracksAdapter mTracksAdapter;

    public TracksActivityFragment() {
    }

    public interface Callback {
        public void onItemSelected(Uri contentUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_TRACKS_LIST, (ArrayList) mTracksList);
        outState.putString(STATE_ARTIST_ID, mArtistId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(TracksActivityFragment.TRACK_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        mTracksAdapter = new TracksAdapter(getActivity(), R.layout.list_item_track);

        ListView listView = (ListView) rootView.findViewById(R.id.list_tracks);
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

        if (savedInstanceState != null) {
            mTracksList = savedInstanceState.getParcelableArrayList(STATE_TRACKS_LIST);
            mArtistId = savedInstanceState.getString(STATE_ARTIST_ID);
            mTracksAdapter.addAll(mTracksList);
        } else {
            mArtistId = mUri.getQueryParameter(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID);
            Bundle loaderArgs = new Bundle();
            loaderArgs.putString(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID, mArtistId);
            getLoaderManager().initLoader(SEARCH_TOP_TRACKS_TASK_LOADER_ID, loaderArgs, TracksActivityFragment.this).forceLoad();
        }

        return rootView;
    }

    @Override
    public Loader<List<Track>> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            mTracksAdapter.clear();
            String artistId = args.getString(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID);
            Log.d("TracksActivityFragment", "onCreateLoader: " + artistId);
            return new SearchTopTracksTaskLoader(getActivity(), artistId);
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<List<Track>> loader) {
        mTracksAdapter.clear();
    }

    @Override
    public void onLoadFinished(Loader<List<Track>> loader, List<Track> data) {
        if (data == null || data.size() == 0) {
            Toast.makeText(getActivity(), "No Results.", Toast.LENGTH_SHORT).show();
            return;
        }

        mTracksList = data;
        mTracksAdapter.addAll(mTracksList);
    }

    static private class SearchTopTracksTaskLoader extends AsyncTaskLoader<List<Track>> {

        private SpotifyService mService;
        private String mArtistId;

        public SearchTopTracksTaskLoader(Context context, String artistId) {
            super(context);
            mService = new SpotifyApi().getService();
            mArtistId = artistId;
        }

        @Override
        public List<Track> loadInBackground() {
            List<Track> result = new ArrayList<Track>();
            try {
                Map<String, Object> options = new HashMap<String, Object>();
                options.put(SpotifyContract.TrackEntry.COLUMN_COUNTRY, SpotifyContract.TrackEntry.VALUE_COUNTRY_DEFAULT);
                Tracks tracks = mService.getArtistTopTrack(mArtistId, options);
                for (kaaes.spotify.webapi.android.models.Track track: tracks.tracks) {
                    result.add(new Track(track));
                }
                return result;
            } catch (RetrofitError error) {
                Log.e("SearchTopTracksTaskLoader", "RetrofitError: " + error.getMessage());
            }
            return result;
        }
    }
}
