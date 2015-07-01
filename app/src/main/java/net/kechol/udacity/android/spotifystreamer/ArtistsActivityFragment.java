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

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Artist>> {

    private static final int SEARCH_ARTISTS_TASK_LOADER_ID = 0;
    private static final String STATE_ARTISTS_LIST = "mArtistsList";
    private List<Artist> mArtistsList;
    private ArtistsAdapter mArtistsAdapter;

    public ArtistsActivityFragment() {
    }

    public interface Callback {
        public void onItemSelected(Uri contentUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_ARTISTS_LIST, (ArrayList) mArtistsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);
        
        mArtistsAdapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artist);

        if (savedInstanceState != null) {
            mArtistsList = savedInstanceState.getParcelableArrayList(STATE_ARTISTS_LIST);
            mArtistsAdapter.addAll(mArtistsList);
        }

        ListView listView = (ListView) rootView.findViewById(R.id.list_artists);
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

        return rootView;
    }

    @Override
    public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            mArtistsAdapter.clear();
            String query = args.getString("query");
            Log.d("ArtistsActivityFragment", "onCreateLoader: " + query);
            return new SearchArtistsTaskLoader(getActivity(), query);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
        Log.d("ArtistsActivityFragment", "onLoadFinished: " + data.toString());

        if (data == null || data.size() == 0) {
            Toast.makeText(getActivity(), "No Results. Search Again!", Toast.LENGTH_SHORT).show();
            return;
        }

        mArtistsList = data;
        mArtistsAdapter.addAll(mArtistsList);
    }

    @Override
    public void onLoaderReset(Loader<List<Artist>> loader) {
        mArtistsAdapter.clear();
    }

    static private class SearchArtistsTaskLoader extends AsyncTaskLoader<List<Artist>> {

        private SpotifyService mService;
        private String mQuery;

        public SearchArtistsTaskLoader(Context context, String query) {
            super(context);
            mService = new SpotifyApi().getService();
            mQuery = query;
        }

        @Override
        public List<Artist> loadInBackground() {
            List<Artist> result = new ArrayList<>();

            try {
                ArtistsPager pager = mService.searchArtists(mQuery);
                Log.d("SearchArtistsTaskLoader", "loadInBackground: " + mQuery);

                if (pager == null || pager.artists == null) {
                    return result;
                }

                for(kaaes.spotify.webapi.android.models.Artist artist: pager.artists.items) {
                    result.add(new Artist(artist));
                }
            } catch (RetrofitError error) {
                Log.e("SearchArtistsTaskLoader", "RetrofitError: " + error.getMessage());
            }

            return result;
        }
    }
}
