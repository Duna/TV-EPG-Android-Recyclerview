package genericepg.duna.project.observable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import genericepg.duna.project.adapter.GenericProgramsAdapter;
import genericepg.duna.project.model.BaseProgramModel;
import genericepg.duna.project.utils.Utils;

/**
 * Created by Marius Duna on 9/9/2016.
 */
public class ObservableRecyclerView<T extends BaseProgramModel> extends RecyclerView implements IObservable {
    protected Subject subject;

    public ObservableRecyclerView(Context context) {
        super(context);
    }

    public ObservableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
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
        ArrayList<T> list = ((GenericProgramsAdapter) getAdapter()).getArrayList();
        final int initialPosition = Utils.getInitialPositionInList(subject.getSystemTime(), (ArrayList<BaseProgramModel>) list);
        final float initialOffset = Utils.getInitialProgramOffsetPx(list.get(initialPosition).getStartTime(), subject.getSystemTime(), getContext());
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(initialPosition, -(int) (initialOffset + subject.getInitialPosition()));
    }
}
