package net.kechol.udacity.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistsAdapter extends ArrayAdapter<Artist> {

    public ArtistsAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Artist artist = getItem(position);
        if (artist == null) return convertView;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.list_item_artist_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_artist_image);

        nameView.setText(artist.name);
        if (artist.images.size() > 0) {
            Picasso.with(getContext()).load(artist.images.get(0).url).into(imageView);
        }

        return convertView;
    }
}