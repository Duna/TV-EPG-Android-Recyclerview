package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Marius Duna on 9/30/2016.
 */

public abstract class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public CustomViewHolder(View itemView) {
        super(itemView);
    }
}
