package net.kechol.udacity.android.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsActivityFragment extends Fragment {

//    private SpotifyService mService;

    public ArtistsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        mService = new SpotifyApi().getService();

        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        final EditText searchText = (EditText) rootView.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchable = s.toString();
//                mService.searchArtists(searchable);
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_artists);

        ArtistsAdapter artistsAdapter = new ArtistsAdapter(getActivity(), null, 0);
        listView.setAdapter(artistsAdapter);


        return rootView;
    }
}
