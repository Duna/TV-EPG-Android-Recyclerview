package epg.duna.twowayrecyclerview;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import epg.duna.twowayrecyclerview.model.ChannelModel;
import epg.duna.twowayrecyclerview.model.ProgramModel;
import epg.duna.twowayrecyclerview.model.TimelineModel;
import epg.duna.twowayrecyclerview.view.HeaderChannelViewHolder;
import epg.duna.twowayrecyclerview.view.ProgramsViewHolder;
import epg.duna.twowayrecyclerview.view.TimelineViewHolder;
import genericepg.duna.project.adapter.GenericChannelsAdapter;
import genericepg.duna.project.adapter.GenericEpgAdapter;
import genericepg.duna.project.adapter.GenericProgramsAdapter;
import genericepg.duna.project.adapter.GenericTimelineAdapter;
import genericepg.duna.project.model.BaseChannelModel;
import genericepg.duna.project.model.BaseTimelineModel;
import genericepg.duna.project.observable.Subject;
import genericepg.duna.project.utils.Utils;
import genericepg.duna.project.view.EPGView;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ProgramModel> horizontalList;
    private ArrayList<ChannelModel> headerChannelsList;
    private ArrayList<TimelineModel> timelineList;
    private ArrayList<ArrayList<ProgramModel>> verticalList;

    private GenericEpgAdapter epgAdapter;
    private GenericTimelineAdapter genericTimelineAdapter;
    private GenericChannelsAdapter channelsAdapter;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM hh:mm", Locale.US);

    private EPGView epgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        epgView = findViewById(R.id.epg);
        createDummyData();

        epgAdapter = new GenericEpgAdapter(verticalList) {
            @Override
            public GenericProgramsAdapter programsCreator(ArrayList programList, final Subject subject) {
                return new GenericProgramsAdapter(programList) {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_item, parent, false);
                        return new ProgramsViewHolder(itemView);
                    }

                    @Override
                    public void onBindData(RecyclerView.ViewHolder holder, final int position) {
                        ProgramModel programModel = (ProgramModel) getItem(position);

                        String day = minutesFormat.format(programModel.getStartTime());
                        ProgramsViewHolder myHolder = (ProgramsViewHolder) holder;
                        myHolder.title.setText(day);

                        myHolder.description.setText(programModel.getDescription());
                        myHolder.title.setBackgroundColor(programModel.getColorTitle());
                        if (programModel.getStartTime() < subject.getSystemTime() && subject.getSystemTime() < programModel.getEndTime()) {
                            myHolder.description.setBackgroundColor(ContextCompat.getColor(myHolder.title.getContext(), android.R.color.black));
                        } else {
                            myHolder.description.setBackgroundColor(programModel.getColorDescription());
                        }
                        ViewGroup.LayoutParams layoutParams = myHolder.itemView.getLayoutParams();
                        int px = (int) Utils.convertMillisecondsToPx(programModel.getEndTime() - programModel.getStartTime(), myHolder.title.getContext());
                        layoutParams.width = px;
                    }
                };
            }
        };

        genericTimelineAdapter = new GenericTimelineAdapter(timelineList) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
                return new TimelineViewHolder(itemView);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, int position) {
                BaseTimelineModel timelineModel = getItem(position);
                TimelineViewHolder myHolder = (TimelineViewHolder) holder;

                Date date = new Date(timelineModel.getTime());
                calendar.setTime(date);
                String str = simpleDateFormat.format(date.getTime());

                String day = minutesFormat.format(date.getTime());
                myHolder.timeView.setText(String.format("%s/%s", str, day));
            }
        };

        channelsAdapter = new GenericChannelsAdapter(headerChannelsList) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_header_item, parent, false);
                return new HeaderChannelViewHolder(itemView);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, int position) {
                BaseChannelModel programModel = (BaseChannelModel) getItem(position);
                HeaderChannelViewHolder myHolder = (HeaderChannelViewHolder) holder;
                myHolder.channelLogo.setImageURI(programModel.getUri());
            }
        };

        epgView.setTimelineAdapter(genericTimelineAdapter);
        epgView.setChannelsAdapter(channelsAdapter);
        epgView.setEpgAdapter(epgAdapter);
    }

    private Uri getUriToResource(@NonNull Context context, @AnyRes int resId) throws Resources.NotFoundException {
        Resources res = context.getResources();
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
        return resUri;
    }

    private void createDummyData() {
        long hour = TimeUnit.HOURS.toMillis(1);
        long halfHour = hour / 2;
        long fifteenMin = hour / 4;
        long day = hour * 24;
        long week = day * 7;

        double nowTime = calendar.getTimeInMillis();

        //generate EPG for -2 weeks and +2 weeks
        long startTime = (long) nowTime - 2 * week;
        long endTime = (long) nowTime + 2 * week;

        verticalList = new ArrayList<>();
        for (int j = 0; j <= 100; j++) {
            horizontalList = new ArrayList<>();
            for (long i = startTime; i <= endTime; ) {
                ProgramModel programModel = new ProgramModel();
                programModel.setStartTime(i);
                Random random = new Random();
                i = i + fifteenMin + random.nextInt((int) hour);
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
        Date date = new Date(startTime);
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE);
        int diff = 60 - minutes;
        //have only precise time from half and half an hour
        startTime = startTime + diff * 60000;

        for (long i = startTime; i <= endTime; ) {
            TimelineModel timelineModel = new TimelineModel();
            timelineModel.setTime(i);
            timelineList.add(timelineModel);
            i = i + halfHour;
        }

        headerChannelsList = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            ChannelModel channelModel = new ChannelModel();
            channelModel.setUri(getUriToResource(getApplicationContext(), i % 2 == 0 ? R.drawable.ic_protv : R.drawable.ic_bbc));
            headerChannelsList.add(channelModel);
        }
    }
}
