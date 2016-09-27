package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import project.samp.mariusduna.twowayrecyclerview.R;

/**
 * Created by Marius Duna on 9/12/2016.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
    private List<Long> horizontalList;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    public class TimelineViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;

        public TimelineViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.text_timeline);
        }
    }

    public TimelineAdapter(ArrayList<Long> horizontalList) {
        this.horizontalList = horizontalList;
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
        return new TimelineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TimelineViewHolder holder, final int position) {
        Date date = new Date(horizontalList.get(position));
        calendar.setTime(date);
        String str = simpleDateFormat.format(date.getTime());

        DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM");
        String day = minutesFormat.format(date.getTime());
        holder.txtView.setText(str + "/" + day);
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}