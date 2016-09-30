package project.samp.mariusduna.twowayrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import project.samp.mariusduna.twowayrecyclerview.model.BaseProgramModel;

/**
 * Created by Marius Duna on 9/12/2016.
 */
public abstract class GenericProgramsAdapter<T extends BaseProgramModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<T> listItems;

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent);

    public abstract void onBindData(RecyclerView.ViewHolder holder, int position);

    public GenericProgramsAdapter(ArrayList<T> listItems) {
        this.listItems = listItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = setViewHolder(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        onBindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public ArrayList<T> getArrayList() {
        return listItems;
    }

    public void setItems(ArrayList<T> newItems) {
        listItems = newItems;
        this.notifyDataSetChanged();
    }

    public T getItem(int position) {
        return listItems.get(position);
    }
}
