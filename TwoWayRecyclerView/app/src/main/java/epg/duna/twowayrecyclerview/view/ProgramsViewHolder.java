package epg.duna.twowayrecyclerview.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import epg.duna.twowayrecyclerview.R;

/**
 * Created by Marius Duna on 10/3/2016.
 */

public class ProgramsViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView description;

    public ProgramsViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.program_title);
        description = (TextView) itemView.findViewById(R.id.program_description);
    }
}