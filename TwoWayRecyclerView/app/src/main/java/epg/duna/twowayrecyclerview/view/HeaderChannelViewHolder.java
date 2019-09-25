package epg.duna.twowayrecyclerview.view;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import epg.duna.twowayrecyclerview.R;

/**
 * Created by Marius Duna on 10/3/2016.
 */

public class HeaderChannelViewHolder extends RecyclerView.ViewHolder {
    public ImageView channelLogo;

    public HeaderChannelViewHolder(View view) {
        super(view);
        channelLogo = view.findViewById(R.id.channel_logo);
    }
}
