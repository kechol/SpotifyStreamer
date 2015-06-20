package net.kechol.udacity.android.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ArtistsAdapter extends CursorAdapter {

    public ArtistsAdapter(Context context, Cursor cursor, int flags) { super(context, cursor, flags); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_artist_image);
        Picasso.with(context).load("http://placehold.it/80x80").into(imageView);

        TextView nameView = (TextView) view.findViewById(R.id.list_item_artist_name);
        nameView.setText("Famous Artist Name Here");
    }
}
