package net.kechol.udacity.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TracksAdapter extends ArrayAdapter<Track> {

    public TracksAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = getItem(position);
        if (track == null) return convertView;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.list_item_track_name);
        TextView albumView = (TextView) convertView.findViewById(R.id.list_item_track_album);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_track_image);

        nameView.setText(track.name);
        albumView.setText(track.album_name);

        if (track.image_url != null) {
            Picasso.with(getContext()).load(track.image_url).into(imageView);
        }

        return convertView;
    }

    public void addAll(List<Track> tracksList) {
        for (Track track: tracksList) {
            add(track);
        }
    }
}