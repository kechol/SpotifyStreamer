package net.kechol.udacity.android.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArtistsPager> {

    private static final int SEARCH_ARTISTS_TASK_LOADER_ID = 0;
    private ArtistsAdapter mArtistsAdapter;

    public ArtistsActivityFragment() {
    }

    public interface Callback {
        public void onItemSelected(Uri contentUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        final EditText searchText = (EditText) rootView.findViewById(R.id.search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("OnEditorActionListener", "onEditorAction: " + String.valueOf(actionId));
                if (actionId == EditorInfo.IME_ACTION_SEARCH && v.getText().length() > 0) {
                    Bundle args = new Bundle();
                    args.putString("query", v.getText().toString());
                    // need to call forceReload() for backward compatibility
                    // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground
                    getLoaderManager().initLoader(SEARCH_ARTISTS_TASK_LOADER_ID, args, ArtistsActivityFragment.this).forceLoad();
                    return true;
                }
                return false;
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_artists);

        mArtistsAdapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artist);
        listView.setAdapter(mArtistsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = (Artist) parent.getItemAtPosition(position);
                if (artist != null) {
                    ((Callback) getActivity()).onItemSelected(
                            SpotifyContract.TrackEntry.CONTENT_URI.buildUpon()
                                    .appendQueryParameter(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID, artist.id).build()
                    );
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<ArtistsPager> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            mArtistsAdapter.clear();
            String query = args.getString("query");
            Log.d("ArtistsActivityFragment", "onCreateLoader: " + query);
            return new SearchArtistsTaskLoader(getActivity(), query);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArtistsPager> loader, ArtistsPager data) {
        Log.d("ArtistsActivityFragment", "onLoadFinished: " + data.artists.items.toString());

        if (data.artists == null || data.artists.items.size() == 0) {
            Toast.makeText(getActivity(), "No Results. Search Again!", Toast.LENGTH_SHORT).show();
            return;
        }

        for(Artist artist: data.artists.items) {
            mArtistsAdapter.add(artist);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArtistsPager> loader) {
        mArtistsAdapter.clear();
    }

    static private class SearchArtistsTaskLoader extends AsyncTaskLoader<ArtistsPager> {

        private SpotifyService mService;
        private String mQuery;

        public SearchArtistsTaskLoader(Context context, String query) {
            super(context);
            mService = new SpotifyApi().getService();
            mQuery = query;
        }

        @Override
        public ArtistsPager loadInBackground() {
            ArtistsPager pager = mService.searchArtists(mQuery);
            Log.d("SearchArtistsTaskLoader", "loadInBackground: " + mQuery);
            // TODO: Error Handling for Retrofit
            return pager;
        }
    }
}
