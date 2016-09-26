package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import project.samp.mariusduna.twowayrecyclerview.R;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;
import project.samp.mariusduna.twowayrecyclerview.observable.ObservableRecyclerView;
import project.samp.mariusduna.twowayrecyclerview.observable.Subject;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public class EpgAdapter extends RecyclerView.Adapter<EpgAdapter.EpgViewHolder> {
    private RecyclerView.RecycledViewPool recycledViewPool;
    private ArrayList<ArrayList<ProgramModel>> verticalList;
    DateFormat minutesFormat = new SimpleDateFormat("mm");
    DateFormat secondsFormat = new SimpleDateFormat("ss");
    private Subject subject;

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public class EpgViewHolder extends RecyclerView.ViewHolder {
        private ObservableRecyclerView recyclerView;

        public EpgViewHolder(View view) {
            super(view);
            recyclerView = (ObservableRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
            recyclerView.setSubject(subject);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
        }

        public void setList(ArrayList<ProgramModel> horizontalList) {
            ProgramsAdapter horizontalAdapter = new ProgramsAdapter(horizontalList);
            recyclerView.setAdapter(horizontalAdapter);
            recyclerView.setSubject(subject);
            Log.d("POS", "Recycled new view at pos: " + subject.getInitialPosition());
        }

        private float getInitialOffset(ProgramModel program) {
            Date date1 = new Date((long) subject.getCurrentTime());
            long minutes1 = Integer.parseInt(minutesFormat.format(date1));
            long seconds1 = Integer.parseInt(secondsFormat.format(date1));
            long totalSeconds = TimeUnit.MINUTES.toSeconds(minutes1) + seconds1;
            long diffProgramStart = TimeUnit.MILLISECONDS.toSeconds(program.getEndTime() - program.getStartTime()) - totalSeconds;
            float diffProgramStartMillis = TimeUnit.SECONDS.toMillis(diffProgramStart);
            return diffProgramStartMillis;
        }

        //TODO optimize idea: migrate to binary search
        private int getInitialPositionTimeline(ArrayList<ProgramModel> list) {
            int i = 0;
            while (subject.getCurrentTime() > list.get(i).getStartTime()) {
                i++;
            }
            return i;
        }

        public void initialScroll() {
            ArrayList<ProgramModel> list = ((ProgramsAdapter) recyclerView.getAdapter()).getArrayList();
            final int initialPosition = getInitialPositionTimeline(list);
            final float initialOffset = getInitialOffset(list.get(initialPosition));

            subject.getHandler().post(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((LinearLayoutManager) recyclerView.getLayoutManager())
                                    .scrollToPositionWithOffset(initialPosition, (int) Utils.convertMillisecondsToPx((long) initialOffset, recyclerView.getContext()));
                        }
                    }
            );
        }

    }

    public EpgAdapter(ArrayList<ArrayList<ProgramModel>> verticalList) {
        this.verticalList = verticalList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(R.layout.vrecycler_view_item, 25);
    }

    @Override
    public EpgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ObservableRecyclerView programRow = (ObservableRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        programRow.setRecycledViewPool(recycledViewPool);
        return new EpgViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.vrecycler_view_item;
    }

    @Override
    public void onBindViewHolder(final EpgViewHolder holder, final int position) {
        holder.setList(verticalList.get(position));
       /* holder.scrollBy(-subject.getInitialPosition());*/
        holder.initialScroll();

        /*holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, holder.txtView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }
}