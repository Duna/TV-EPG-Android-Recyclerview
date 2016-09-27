package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import project.samp.mariusduna.twowayrecyclerview.R;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.HeaderChannelViewHolder> {
    private List<Uri> horizontalList;

    public class HeaderChannelViewHolder extends RecyclerView.ViewHolder {
        public ImageView channelLogo;

        public HeaderChannelViewHolder(View view) {
            super(view);
            channelLogo = (ImageView) view.findViewById(R.id.channel_logo);
        }
    }

    public ChannelsAdapter(ArrayList<Uri> horizontalList) {
        this.horizontalList = horizontalList;
    }

    @Override
    public HeaderChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_header_item, parent, false);
        return new HeaderChannelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HeaderChannelViewHolder holder, final int position) {
        holder.channelLogo.setImageURI(horizontalList.get(position));
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}