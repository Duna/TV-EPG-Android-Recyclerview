package project.samp.mariusduna.twowayrecyclerview.observable;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

import project.samp.mariusduna.twowayrecyclerview.adapter.GenericProgramsAdapter;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public class ObservableRecyclerView extends RecyclerView implements IObservable {
    protected Subject subject;

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
    }

    public ObservableRecyclerView(Context context) {
        super(context);
    }

    public ObservableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_MOVE == ev.getAction()){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void update() {
        scrollBy(subject.getState(), 0);
    }

    @Override
    public void reset() {
        ArrayList<ProgramModel> list = ((GenericProgramsAdapter) getAdapter()).getArrayList();
        final int initialPosition = Utils.getInitialPositionInList(subject.getSystemTime(), list);
        final float initialOffset = Utils.getInitialProgramOffsetPx(list.get(initialPosition).getStartTime(), subject.getSystemTime(), getContext());
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(initialPosition, -(int) (initialOffset + subject.getInitialPosition()));
    }
}
