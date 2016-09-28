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
import project.samp.mariusduna.twowayrecyclerview.observable.Subject;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;

/**
 * Created by Marius Duna on 9/12/2016.
 */
public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ProgramsViewHolder> {
    private Subject subject;
    private ArrayList<ProgramModel> horizontalList;

    public class ProgramsViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;

        public ProgramsViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.program_title);
            description = (TextView) view.findViewById(R.id.program_description);
        }
    }

    public ProgramsAdapter(ArrayList<ProgramModel> horizontalList, Subject subject) {
        this.horizontalList = horizontalList;
        this.subject = subject;
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
        holder.title.setText(day);

        //holder.title.setText(programModel.getTitle());
        holder.description.setText(programModel.getDescription());
        holder.title.setBackgroundColor(programModel.getColorTitle());
        if (programModel.getStartTime() < subject.getSystemTime() && subject.getSystemTime() < programModel.getEndTime()) {
            holder.description.setBackgroundColor(ContextCompat.getColor(holder.title.getContext(), android.R.color.black));
        } else {
            holder.description.setBackgroundColor(programModel.getColorDescription());
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        int px = (int) Utils.convertMillisecondsToPx(programModel.getEndTime() - programModel.getStartTime(), holder.title.getContext());
        layoutParams.width = px;
        //layoutParams.width = (int) Utils.convertMinutesToDp(180, holder.title.getContext());//(int) Utils.convertMinutesToDp(programModel.getEndTime() - programModel.getStartTime(), holder.title.getContext());
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
