package genericepg.duna.project.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import genericepg.duna.project.R;
import genericepg.duna.project.adapter.GenericEpgAdapter;
import genericepg.duna.project.adapter.GenericChannelsAdapter;
import genericepg.duna.project.adapter.GenericTimelineAdapter;
import genericepg.duna.project.model.BaseTimelineModel;
import genericepg.duna.project.observable.Subject;
import genericepg.duna.project.utils.Utils;


/**
 * Created by Marius Duna on 10/3/2016.
 */

public class EPGView extends RelativeLayout {
    private RecyclerView epgRecyclerView;
    private RecyclerView timelineRecyclerView;
    private RecyclerView channelsRecyclerView;
    private TextView nowTextView;
    private TextView currentTimeTextView;
    private View nowVerticalLineView;
    private Handler handler;

    private LinearLayoutManager horizontalLayoutManagaer;

    private double nowTime;
    private int screenWidth;

    private Subject subject = new Subject();
    private Calendar calendar = Calendar.getInstance();

    private int location[] = new int[2];

    public EPGView(Context context) {
        super(context);
        init();
    }

    public EPGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
    }

    private void init() {
        inflate(getContext(), R.layout.epg_layout, this);
        handler = new Handler();
        currentTimeTextView = (TextView) findViewById(R.id.current_time);
        epgRecyclerView = (RecyclerView) findViewById(R.id.epg_recycler_view);
        timelineRecyclerView = (RecyclerView) findViewById(R.id.timeline_recycler_view);
        channelsRecyclerView = (RecyclerView) findViewById(R.id.channels_recycler_view);
        nowVerticalLineView = findViewById(R.id.epg_now_line);
        nowTextView = (TextView) findViewById(R.id.epg_now_indicator);

        nowTime = calendar.getTimeInMillis();
        subject.setSystemTime(nowTime);

        nowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.setCurrentTime(nowTime);
                subject.resetAllObservers();
                ArrayList<BaseTimelineModel> timelineList = (ArrayList<BaseTimelineModel>) ((GenericTimelineAdapter) timelineRecyclerView.getAdapter()).getList();
                int timelineCurrentPos = Utils.getInitialPositionInTimelineList(nowTime, timelineList);
                horizontalLayoutManagaer.scrollToPositionWithOffset(timelineCurrentPos,
                        -Utils.getInitialProgramOffsetPx(timelineList.get(timelineCurrentPos).getTime(), nowTime, getContext()));
            }
        });

        LinearLayoutManager epgLayoutmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager channelsLayoutmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        timelineRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        epgRecyclerView.setLayoutManager(epgLayoutmanager);
        channelsRecyclerView.setLayoutManager(channelsLayoutmanager);

        timelineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                subject.setState(dx); //we should store the time
                double currentTime = subject.getCurrentTime();
                double millisPx = Utils.convertPxToMilliseconds(dx > 0 ? dx : -dx, getContext());
                double finalValue = dx > 0 ? currentTime + millisPx : currentTime - millisPx;
                subject.setCurrentTime(finalValue);
                updateCurrentTime((long) finalValue);
                updateNowIndicator();
            }

            private void updateCurrentTime(long currentTime) {
                Date date = new Date(currentTime);
                DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM hh:mm:ss");
                currentTimeTextView.setText(dateFormat.format(date));
            }
        });
        subject.setCurrentTime(nowTime);
    }

    private void updateNowIndicator() {
        float positionOnScreen = Utils.convertMillisecondsToPx((float) (subject.getSystemTime() - subject.getCurrentTime()), getContext());
        if (positionOnScreen + channelsRecyclerView.getWidth() < channelsRecyclerView.getWidth()) {
            nowVerticalLineView.setVisibility(View.INVISIBLE);
            nowTextView.setBackgroundResource(R.drawable.ic_now_left);
            nowTextView.setX(channelsRecyclerView.getWidth());
        }
        if (positionOnScreen + channelsRecyclerView.getWidth() >= channelsRecyclerView.getWidth() && positionOnScreen < screenWidth) {
            if (nowVerticalLineView.getVisibility() == View.INVISIBLE) {
                nowVerticalLineView.setVisibility(View.VISIBLE);
                nowTextView.setBackgroundResource(R.drawable.ic_now_center);
            }
            nowVerticalLineView.setX(positionOnScreen + channelsRecyclerView.getWidth());
            nowTextView.setX(positionOnScreen - nowTextView.getWidth() / 2 + channelsRecyclerView.getWidth());
        }
        if (positionOnScreen + channelsRecyclerView.getWidth() >= screenWidth) {
            nowVerticalLineView.setVisibility(View.INVISIBLE);
            nowTextView.setX(screenWidth - nowTextView.getWidth());
            nowTextView.setBackgroundResource(R.drawable.ic_now_right);
        }
    }

    //TODO fix the crash when tapping with more than one finger
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //manual dispatch events to specific view hierarchy
        dispatchEventForView(ev, timelineRecyclerView);
        dispatchEventForView(ev, epgRecyclerView);
        dispatchEventForView(ev, channelsRecyclerView);

        nowTextView.getLocationInWindow(location);
        Rect editTextRect = new Rect();
        nowTextView.getHitRect(editTextRect);
        if (editTextRect.contains((int) ev.getX(), (int) ev.getY())) {
            nowTextView.dispatchTouchEvent(ev);
        }
        return true;
    }

    private <T extends View> void dispatchEventForView(MotionEvent ev, T genericView) {
        genericView.getLocationInWindow(location);
        MotionEvent motionEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), ev.getX() - location[0], ev.getY() - location[1], ev.getMetaState());
        genericView.dispatchTouchEvent(motionEvent);
    }

    public void setEpgAdapter(GenericEpgAdapter epgAdapter) {
        epgRecyclerView.setAdapter(epgAdapter);
        epgAdapter.setSubject(subject);
    }

    public void setChannelsAdapter(GenericChannelsAdapter channelsAdapter) {
        channelsRecyclerView.setAdapter(channelsAdapter);
    }

    public void setTimelineAdapter(GenericTimelineAdapter timelineAdapter) {
        timelineRecyclerView.setAdapter(timelineAdapter);
        ArrayList<BaseTimelineModel> timelineList = (ArrayList<BaseTimelineModel>) ((GenericTimelineAdapter) timelineRecyclerView.getAdapter()).getList();
        int timelineCurrentPos = Utils.getInitialPositionInTimelineList(nowTime, timelineList);
        horizontalLayoutManagaer.scrollToPositionWithOffset(timelineCurrentPos,
                -Utils.getInitialProgramOffsetPx(timelineList.get(timelineCurrentPos).getTime(), nowTime, getContext()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateNowIndicator();
            }
        }, 50);
    }
}
