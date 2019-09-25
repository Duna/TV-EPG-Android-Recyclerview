package genericepg.duna.project.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import genericepg.duna.project.R;
import genericepg.duna.project.listener.RecyclerItemClickListener;
import genericepg.duna.project.model.BaseProgramModel;
import genericepg.duna.project.observable.ObservableRecyclerView;
import genericepg.duna.project.observable.Subject;
import genericepg.duna.project.utils.Utils;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public abstract class GenericEpgAdapter<T extends BaseProgramModel> extends RecyclerView.Adapter<GenericEpgAdapter.EpgViewHolder> {
    private RecyclerView.RecycledViewPool recycledViewPool;
    private ArrayList<ArrayList<T>> channelsList;
    private Subject subject;

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public class EpgViewHolder<T extends BaseProgramModel> extends RecyclerView.ViewHolder {
        private ObservableRecyclerView recyclerView;

        public EpgViewHolder(View view) {
            super(view);
            recyclerView = (ObservableRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
            recyclerView.setSubject(subject);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(recyclerView.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                   // Toast.makeText(recyclerView.getContext(), "Position: " + position, Toast.LENGTH_SHORT).show();
                }
            }));
        }

        public void setList(ArrayList<T> horizontalList) {
            GenericProgramsAdapter horizontalAdapter = programsCreator(horizontalList, subject);
            recyclerView.setAdapter(horizontalAdapter);
            recyclerView.setSubject(subject);
            Log.d("POS", "Recycled new view at pos: " + subject.getInitialPosition());
        }

        public void initialScroll() {
            ArrayList<BaseProgramModel> list = ((GenericProgramsAdapter) recyclerView.getAdapter()).getArrayList();
            final int initialPosition = Utils.getInitialPositionInList(subject.getCurrentTime(), list);
            final float initialOffset = Utils.getInitialProgramOffsetPx(list.get(initialPosition).getStartTime(), subject.getSystemTime(), recyclerView.getContext());
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(initialPosition, -(int) (initialOffset + subject.getInitialPosition()));
        }
    }

    public GenericEpgAdapter(ArrayList<ArrayList<T>> verticalList) {
        this.channelsList = verticalList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(R.layout.vrecycler_view_item, 20);
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
    public void onBindViewHolder(GenericEpgAdapter.EpgViewHolder holder, int position) {
        holder.setList(channelsList.get(position));
        holder.initialScroll();
    }

    @Override
    public int getItemCount() {
        return channelsList.size();
    }

    public abstract <T extends BaseProgramModel> GenericProgramsAdapter programsCreator(ArrayList<T> programList, Subject subject);
}