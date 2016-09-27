package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
            ProgramsAdapter horizontalAdapter = new ProgramsAdapter(horizontalList, subject);
            recyclerView.setAdapter(horizontalAdapter);
            recyclerView.setSubject(subject);
            Log.d("POS", "Recycled new view at pos: " + subject.getInitialPosition());
        }

        public void initialScroll() {
            ArrayList<ProgramModel> list = ((ProgramsAdapter) recyclerView.getAdapter()).getArrayList();
            final int initialPosition = Utils.getInitialPositionInList(subject.getSystemTime(), list);
            final float initialOffset = Utils.getInitialProgramOffsetPx(list.get(initialPosition).getStartTime(), subject.getSystemTime(), recyclerView.getContext());
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(initialPosition, -(int) (initialOffset + subject.getInitialPosition()));
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