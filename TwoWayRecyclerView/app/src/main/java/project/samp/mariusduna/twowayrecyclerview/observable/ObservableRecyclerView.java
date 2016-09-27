package project.samp.mariusduna.twowayrecyclerview.observable;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import project.samp.mariusduna.twowayrecyclerview.adapter.ProgramsAdapter;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public class ObservableRecyclerView extends RecyclerView implements IObservable{
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
        return true;
    }

    @Override
    public void update() {
        scrollBy(subject.getState(), 0);
        Log.d("POS", "Item scrolled at: " + subject.getState());
    }

    @Override
    public void reset() {
        int initialPositionInList = Utils.getInitialPositionInList(subject.getCurrentTime(), ((ProgramsAdapter)getAdapter()).getArrayList());
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(initialPositionInList, 0);
    }
}
