package genericepg.duna.project.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import genericepg.duna.project.R;
import genericepg.duna.project.adapter.GenericChannelsAdapter;
import genericepg.duna.project.adapter.GenericEpgAdapter;
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

    public double getNowTime() {
        return subject.getCurrentTime();
    }

    public static int getTimeOffset(long time) {
        TimeZone tz = TimeZone.getDefault();
        return tz.getOffset(time);
    }

    private void init() {
        inflate(getContext(), R.layout.epg_layout, this);
        handler = new Handler();
        currentTimeTextView = findViewById(R.id.current_time);
        epgRecyclerView = findViewById(R.id.epg_recycler_view);
        timelineRecyclerView = findViewById(R.id.timeline_recycler_view);
        channelsRecyclerView = findViewById(R.id.channels_recycler_view);
        nowVerticalLineView = findViewById(R.id.epg_now_line);
        nowTextView = findViewById(R.id.epg_now_indicator);

        long millis = calendar.getTimeInMillis();
        nowTime = millis;// + getTimeOffset(millis);
        subject.setSystemTime(nowTime);

    /*    DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM HH:mm");
        String dayy = minutesFormat.format(nowTime);
        Toast.makeText(getContext(), "Start Time is: " + dayy, Toast.LENGTH_LONG).show();*/

      /*  Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<BaseTimelineModel> timelineList = (ArrayList<BaseTimelineModel>) ((GenericTimelineAdapter) timelineRecyclerView.getAdapter()).getList();
                int timelineCurrentPos = Utils.getInitialPositionInTimelineList(nowTime, timelineList);

                DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM HH:mm");
                String dayy = minutesFormat.format(timelineList.get(0).getTime());

                Toast.makeText(getContext(), "Time start:" + dayy, Toast.LENGTH_LONG).show();
            }
        }, 1000);*/


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
                DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM HH:mm", Locale.US);
                currentTimeTextView.setText(dateFormat.format(date));
            }
        });
        subject.setCurrentTime(nowTime);
    }

    private void updateNowIndicator() {
        double positionOnScreen = Utils.convertMillisecondsToPx((subject.getSystemTime() - subject.getCurrentTime()), getContext());
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
            nowVerticalLineView.setX((float) (positionOnScreen + channelsRecyclerView.getWidth()));
            nowTextView.setX((float) (positionOnScreen - nowTextView.getWidth() / 2 + channelsRecyclerView.getWidth()));
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
        //genericView.getLocationInWindow(location);
        int height = genericView.getHeight();
        int width = genericView.getWidth();
        MotionEvent motionEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), ev.getX() - width/*- location[0]*/, ev.getY() - height /*- location[1]*/, ev.getMetaState());
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
        showNowIndicator();
        timelineRecyclerView.setAdapter(timelineAdapter);
        ArrayList<BaseTimelineModel> timelineList = (ArrayList<BaseTimelineModel>) ((GenericTimelineAdapter) timelineRecyclerView.getAdapter()).getList();
        int timelineCurrentPos = Utils.getInitialPositionInTimelineList(nowTime, timelineList);

        try {
            horizontalLayoutManagaer.scrollToPositionWithOffset(timelineCurrentPos,
                    -Utils.getInitialProgramOffsetPx(timelineList.get(timelineCurrentPos).getTime(), nowTime, getContext()));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateNowIndicator();
                }
            }, 50);
        } catch (Exception e) {
            hideNowIndicator();
        }
    }

    private void hideNowIndicator() {
        nowVerticalLineView.setVisibility(View.GONE);
        nowTextView.setVisibility(View.GONE);
    }

    private void showNowIndicator() {
        nowVerticalLineView.setVisibility(View.VISIBLE);
        nowTextView.setVisibility(View.VISIBLE);
    }
}
