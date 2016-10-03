package project.samp.mariusduna.twowayrecyclerview;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import project.samp.mariusduna.twowayrecyclerview.adapter.EpgAdapter;
import project.samp.mariusduna.twowayrecyclerview.adapter.GenericChannelsAdapter;
import project.samp.mariusduna.twowayrecyclerview.adapter.GenericProgramsAdapter;
import project.samp.mariusduna.twowayrecyclerview.adapter.GenericTimelineAdapter;
import project.samp.mariusduna.twowayrecyclerview.model.ChannelModel;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;
import project.samp.mariusduna.twowayrecyclerview.model.TimelineModel;
import project.samp.mariusduna.twowayrecyclerview.observable.Subject;
import project.samp.mariusduna.twowayrecyclerview.utils.Utils;
import project.samp.mariusduna.twowayrecyclerview.view.EPGView;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ProgramModel> horizontalList;
    private ArrayList<ChannelModel> headerChannelsList;
    private ArrayList<TimelineModel> timelineList;
    private ArrayList<ArrayList<ProgramModel>> verticalList;

    private EpgAdapter epgAdapter;
    private GenericTimelineAdapter genericTimelineAdapter;
    private GenericChannelsAdapter channelsAdapter;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    private double nowTime;

    private EPGView epgView;

    private class ProgramsViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;

        public ProgramsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.program_title);
            description = (TextView) itemView.findViewById(R.id.program_description);
        }
    }

    public class HeaderChannelViewHolder extends RecyclerView.ViewHolder {
        public ImageView channelLogo;

        public HeaderChannelViewHolder(View view) {
            super(view);
            channelLogo = (ImageView) view.findViewById(R.id.channel_logo);
        }
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder {
        public TextView timeView;

        public TimelineViewHolder(View view) {
            super(view);
            timeView = (TextView) view.findViewById(R.id.text_timeline);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        epgView  = (EPGView) findViewById(R.id.epg);

        int hour = 3600000;
        int halfHour = hour / 2;
        int fifteenMin = hour / 4;
        int day = hour * 24;
        int week = day * 7;

        nowTime = calendar.getTimeInMillis();
        Random rand = new Random();
        double nowwTime = nowTime + (long) rand.nextInt(hour * 2 + 1);
        long startTimew = (long) nowwTime - 2 * week;
        long endTimew = (long) nowwTime + 2 * week;

        long startTime = (long) nowTime - 2 * week;
        long endTime = (long) nowTime + 2 * week;

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
        for (int i = 0; i <= 30; i++) {
            ChannelModel channelModel = new ChannelModel();
            channelModel.setUri(getUriToResource(getApplicationContext(), R.drawable.ic_protv));
            headerChannelsList.add(channelModel);
        }

        epgAdapter = new EpgAdapter(verticalList) {
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

                        DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM hh:mm");
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
                TimelineModel timelineModel = (TimelineModel) getItem(position);
                TimelineViewHolder myHolder = (TimelineViewHolder) holder;

                Date date = new Date(timelineModel.getTime());
                calendar.setTime(date);
                String str = simpleDateFormat.format(date.getTime());

                DateFormat minutesFormat = new SimpleDateFormat("EEE dd MMM");
                String day = minutesFormat.format(date.getTime());
                myHolder.timeView.setText(str + "/" + day);
            }
        };
        epgView.setTimelineAdapter(genericTimelineAdapter);

        channelsAdapter = new GenericChannelsAdapter(headerChannelsList) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_header_item, parent, false);
                return new HeaderChannelViewHolder(itemView);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, int position) {
                ChannelModel programModel = (ChannelModel) getItem(position);
                HeaderChannelViewHolder myHolder = (HeaderChannelViewHolder) holder;
                myHolder.channelLogo.setImageURI(programModel.getUri());
            }
        };

        epgView.setChannelsAdapter(channelsAdapter);
        epgView.setEpgAdapter(epgAdapter);
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
