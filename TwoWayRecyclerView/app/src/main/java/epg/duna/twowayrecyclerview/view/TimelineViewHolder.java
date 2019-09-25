package epg.duna.twowayrecyclerview.view;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import epg.duna.twowayrecyclerview.R;

/**
 * Created by Marius Duna on 10/3/2016.
 */

public class TimelineViewHolder extends RecyclerView.ViewHolder {
    public TextView timeView;

    public TimelineViewHolder(View view) {
        super(view);
        timeView = view.findViewById(R.id.text_timeline);
    }
}