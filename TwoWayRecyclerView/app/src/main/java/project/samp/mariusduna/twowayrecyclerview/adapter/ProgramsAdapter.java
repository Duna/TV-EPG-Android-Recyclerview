package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import project.samp.mariusduna.twowayrecyclerview.R;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;

/**
 * Created by Marius Duna on 9/12/2016.
 */
public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ProgramsViewHolder> {

    private ArrayList<ProgramModel> horizontalList;

    public class ProgramsViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle;
        public TextView textDescription;

        public ProgramsViewHolder(View view) {
            super(view);
            textTitle = (TextView) view.findViewById(R.id.program_title);
            textDescription = (TextView) view.findViewById(R.id.program_description);
        }
    }

    public ProgramsAdapter(ArrayList<ProgramModel> horizontalList) {
        this.horizontalList = horizontalList;
    }

    @Override
    public ProgramsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_item, parent, false);
        return new ProgramsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProgramsViewHolder holder, final int position) {
        ProgramModel programModel = horizontalList.get(position);

        DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM hh:mm");
        String day = minutesFormat.format(programModel.getStartTime());
        holder.textTitle.setText(day);

        //holder.textTitle.setText(programModel.getTitle());
        holder.textDescription.setText(programModel.getDescription());
        holder.textTitle.setBackgroundColor(programModel.getColorTitle());
        if (programModel.getStartTime() < System.currentTimeMillis() && System.currentTimeMillis() < programModel.getEndTime()) {
            holder.textDescription.setBackgroundColor(ContextCompat.getColor(holder.textTitle.getContext(), android.R.color.black));
        } else {
            holder.textDescription.setBackgroundColor(programModel.getColorDescription());
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        int px = (int) Utils.convertMillisecondsToPx(programModel.getEndTime() - programModel.getStartTime(), holder.textTitle.getContext());
        layoutParams.width = px;
        //layoutParams.width = (int) Utils.convertMinutesToDp(180, holder.textTitle.getContext());//(int) Utils.convertMinutesToDp(programModel.getEndTime() - programModel.getStartTime(), holder.textTitle.getContext());
        // holder.itemView.setLayoutParams(layoutParams);
       /* holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public ArrayList<ProgramModel> getArrayList() {
        return horizontalList;
    }
}
