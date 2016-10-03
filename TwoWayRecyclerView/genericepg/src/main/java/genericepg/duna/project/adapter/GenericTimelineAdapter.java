package genericepg.duna.project.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import genericepg.duna.project.model.BaseTimelineModel;

/**
 * Created by Marius Duna on 9/12/2016.
 * This is duplicate of GenericPrograms Adapter, specially added in case further modifications only for timeline view
 */
public abstract class GenericTimelineAdapter<O extends BaseTimelineModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<O> timeList;

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent);

    public abstract void onBindData(RecyclerView.ViewHolder holder, int position);

    public GenericTimelineAdapter(ArrayList<O> timeList) {
        this.timeList = timeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = setViewHolder(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }

    public O getItem(int position) {
        return timeList.get(position);
    }

    public List<O> getList(){
        return timeList;
    }
}