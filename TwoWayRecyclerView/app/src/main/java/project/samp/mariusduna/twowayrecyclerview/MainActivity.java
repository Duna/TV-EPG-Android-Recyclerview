package project.samp.mariusduna.twowayrecyclerview;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import project.samp.mariusduna.twowayrecyclerview.adapter.EpgAdapter;
import project.samp.mariusduna.twowayrecyclerview.adapter.HeaderChannelsAdapter;
import project.samp.mariusduna.twowayrecyclerview.adapter.TimelineAdapter;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;
import project.samp.mariusduna.twowayrecyclerview.observable.Subject;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private RecyclerView epgRecyclerView;
    private RecyclerView timelineRecyclerView;
    private RecyclerView channelsRecyclerView;
    private ArrayList<ProgramModel> horizontalList;
    private ArrayList<Long> timelineList;
    private ArrayList<ArrayList<ProgramModel>> verticalList;
    private EpgAdapter epgAdapter;
    private TimelineAdapter timelineAdapter;

    private ArrayList<Uri> headerChannelsList;
    private HeaderChannelsAdapter channelsAdapter;
    private Subject subject = new Subject();

    private LinearLayoutManager horizontalLayoutManagaer;
    private TextView currentTimeTextView;
    private View nowVerticalLineView;
    private TextView nowTextView;

    private float mDownX;
    private float mDownY;
    private double nowTime;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentTimeTextView = (TextView) findViewById(R.id.current_time);
        epgRecyclerView = (RecyclerView) findViewById(R.id.epg_recycler_view);
        timelineRecyclerView = (RecyclerView) findViewById(R.id.timeline_recycler_view);
        channelsRecyclerView = (RecyclerView) findViewById(R.id.channels_recycler_view);
        nowVerticalLineView = findViewById(R.id.epg_now_line);
        nowTextView = (TextView) findViewById(R.id.epg_now_indicator);

        nowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long offsetStartMillis = Utils.getTimelineOffset(nowTime, getApplicationContext());
                subject.setState((int) Utils.convertMillisecondsToPx(offsetStartMillis, getApplicationContext()));
                subject.setCurrentTime(nowTime);
                subject.resetAllObservers();
                int timelineCurrentPos = Utils.getInitialPositionInTimelineList(nowTime, timelineList);
                horizontalLayoutManagaer.scrollToPositionWithOffset(timelineCurrentPos, -Utils.getInitialProgramOffsetPx(timelineList.get(timelineCurrentPos), nowTime, getApplicationContext()));
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

        int hour = 3600000;
        int halfHour = hour / 2;
        int fifteenMin = hour / 4;
        int day = hour * 24;
        int week = day * 7;

        Calendar c = Calendar.getInstance();
        nowTime = c.getTimeInMillis();
        Random rand = new Random();
        double nowwTime = nowTime + (long) rand.nextInt(hour * 2 + 1);
        long startTimew = (long) nowwTime - 2 * week;
        long endTimew = (long) nowwTime + 2 * week;

        long startTime = (long) nowTime - 2 * week;
        long endTime = (long) nowTime + 2 * week;
        subject.setSystemTime(nowTime);

        verticalList = new ArrayList<>();
        for (int j = 0; j <= 30; j++) {
            horizontalList = new ArrayList<>();
            for (long i = startTimew; i <= endTimew; ) {
                ProgramModel programModel = new ProgramModel();
                programModel.setStartTime(i);
                Random randw = new Random();
                i = i + fifteenMin + randw.nextInt(hour);
                programModel.setEndTime(i);
                programModel.setTitle("Title");
                programModel.setDescription("Description");
                programModel.setColorTitle(getResources().getColor(R.color.colorPrimary));
                programModel.setColorDescription(getResources().getColor(R.color.colorAccent));
                horizontalList.add(programModel);
            }
            verticalList.add(horizontalList);
        }

        timelineList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(startTime);
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE);
        int diff = 60 - minutes;
        //have only precise time from half and half an hour
        startTime = startTime + diff * 60000;

        for (long i = startTime; i <= endTime; ) {
            timelineList.add(i);
            i = i + halfHour;
        }

        headerChannelsList = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            headerChannelsList.add(getUriToResource(getApplicationContext(), R.drawable.ic_protv));
        }

        epgAdapter = new EpgAdapter(verticalList);
        epgAdapter.setSubject(subject);

        timelineAdapter = new TimelineAdapter(timelineList);
        timelineRecyclerView.setAdapter(timelineAdapter);

        channelsAdapter = new HeaderChannelsAdapter(headerChannelsList);
        channelsRecyclerView.setAdapter(channelsAdapter);


        final LinearLayoutManager epgLayoutmanager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager channelsLayoutmanager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        horizontalLayoutManagaer = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
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
                double millisPx = Utils.convertPxToMilliseconds(dx > 0 ? dx : -dx, getApplicationContext());
                double finalValue = dx > 0 ? currentTime + millisPx : currentTime - millisPx;
                subject.setCurrentTime(finalValue);
                updateCurrentTime((long) finalValue);
                updateNowIndicator();
            }

            private void updateNowIndicator() {
                float positionOnScreen = Utils.convertMillisecondsToPx((float) (nowTime - subject.getCurrentTime()), getApplicationContext());
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

            private void updateCurrentTime(long currentTime) {
                Date date = new Date(currentTime);
                DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM hh:mm:ss");
                currentTimeTextView.setText(dateFormat.format(date));
            }
        });
        subject.setCurrentTime(nowTime);
        int timelineCurrentPos = Utils.getInitialPositionInTimelineList(nowTime, timelineList);
        horizontalLayoutManagaer.scrollToPositionWithOffset(timelineCurrentPos, -Utils.getInitialProgramOffsetPx(timelineList.get(timelineCurrentPos), nowTime, getApplicationContext()));

        epgRecyclerView.setAdapter(epgAdapter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getY() < 350) {
            super.dispatchTouchEvent(ev);
            return false;
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = ev.getY();
                    mDownX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = ev.getY();
                    float yDelta = y - mDownY;
                    mDownY = y;

                    float x = ev.getX();
                    float xDelta = x - mDownX;
                    mDownX = x;
                    //force orthogonal movements
                    if (Math.abs(yDelta) > Math.abs(xDelta)) {
                        epgRecyclerView.scrollBy(0, -(int) yDelta);
                        channelsRecyclerView.scrollBy(0, -(int) yDelta);
                    } else {
                        timelineRecyclerView.scrollBy(-(int) xDelta, 0);
                    }
                    break;
            }
            return true;
        }
    }

    public static final Uri getUriToResource(@NonNull Context context, @AnyRes int resId) throws Resources.NotFoundException {
        Resources res = context.getResources();
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
        return resUri;
    }
}
