package epg.duna.twowayrecyclerview;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.TimeZone;
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
import genericepg.duna.project.listener.RecyclerItemClickListener;
import genericepg.duna.project.model.BaseChannelModel;
import genericepg.duna.project.model.BaseTimelineModel;
import genericepg.duna.project.observable.Subject;
import genericepg.duna.project.utils.Utils;
import genericepg.duna.project.view.EPGView;

public class MainActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {
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

    private int pxHour;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calculateOffset();
        setContentView(R.layout.activity_main);

        epgView = findViewById(R.id.epg);
        createDummyData();

        epgAdapter = new GenericEpgAdapter(verticalList, this) {
            @Override
            public GenericProgramsAdapter programsCreator(ArrayList programList, final Subject subject) {
                return new GenericProgramsAdapter(programList) {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.program_custom_item, parent, false);
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
                String day = minutesFormat.format(date.getTime());
                myHolder.timeView.setText(day);

                //Important to sync with programs width
                ViewGroup.LayoutParams layoutParams = myHolder.itemView.getLayoutParams();
                layoutParams.width = pxHour;
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

    private void calculateOffset() {
/*        IntervalModel im = PreferenceUtils.getInstance().getReportInterval();
        offsetStart = getTimeOffset(im.start);
        offsetEnd = getTimeOffset(im.end);*/
        long hour = TimeUnit.HOURS.toMillis(1);
        pxHour = (int) Utils.convertMillisecondsToPx(hour / 2, this);
    }

    public static int getTimeOffset(long time) {
        TimeZone tz = TimeZone.getDefault();
        return tz.getOffset(time);
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

        Date date = new Date(startTime);
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE);
        int diff = 60 - minutes;
        //have only precise time from half and half an hour
        startTime = startTime + diff * 60000;

        verticalList = new ArrayList<>();
        for (int j = 0; j <= 100; j++) {
            horizontalList = new ArrayList<>();
            for (long i = startTime; i <= endTime; ) {
                long x = i;
                Random random = new Random();
                i = i + halfHour + random.nextInt((int) hour);
                ProgramModel programModel = new ProgramModel(x, i);
                programModel.setTitle("Title");
                programModel.setDescription("Description");
                programModel.setColorTitle(ContextCompat.getColor(this, R.color.colorPrimary));
                programModel.setColorDescription(ContextCompat.getColor(this, R.color.colorAccent));
                horizontalList.add(programModel);
            }
            //in our case gaps are only at the end
            verticalList.add(fillGaps(horizontalList, startTime, endTime));
        }

        timelineList = new ArrayList<>();

        for (long i = startTime; i <= endTime; ) {
            TimelineModel timelineModel = new TimelineModel();
            timelineModel.setTime(i);
            timelineList.add(timelineModel);
            i = i + halfHour;
        }

        headerChannelsList = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            ChannelModel channelModel = new ChannelModel();
            channelModel.setUri(getUriToResource(getApplicationContext(), i % 3 == 0 ? R.drawable.ic_protv : i % 2 == 0 ? R.drawable.ic_fox : R.drawable.ic_bbc));
            headerChannelsList.add(channelModel);
        }
    }

    private ArrayList<ProgramModel> fillGaps(ArrayList<ProgramModel> horizontalList, long startTime, long endTime) {
        ArrayList<ProgramModel> resultList = new ArrayList<>();
        if (horizontalList == null || horizontalList.size() == 0) {
            //add 2 empty programs
            long third = (endTime - startTime) / 3L;
            ProgramModel p1 = new ProgramModel(startTime, startTime + third);
            ProgramModel p2 = new ProgramModel(startTime + third, startTime + (2L * third));
            ProgramModel p3 = new ProgramModel(startTime + (2L * third), endTime);
            resultList.add(p1);
            resultList.add(p2);
            resultList.add(p3);
        } else if (horizontalList.size() == 1) {
            //add start and end
            ProgramModel current = horizontalList.get(0);
            //current.setDescription("Gap");
            ProgramModel p = new ProgramModel(startTime, current.getStartTime());
            ProgramModel p1 = new ProgramModel(current.getEndTime(), endTime);
            resultList.add(p);
            resultList.add(current);
            resultList.add(p1);
        } else {
            //add start
            ProgramModel p = new ProgramModel(startTime, horizontalList.get(0).getStartTime());
            resultList.add(p);
            //add middle gaps
            for (int i = 0; i < horizontalList.size() - 1; i++) {
                ProgramModel current = horizontalList.get(i);
                ProgramModel next = horizontalList.get(i + 1);
                //current.setDescription("Gap");
                resultList.add(current);
                if (current.getEndTime() < next.getStartTime()) {
                    ProgramModel p0 = new ProgramModel(current.getEndTime(), next.getStartTime());
                    resultList.add(p0);
                }
                if (i == horizontalList.size() - 2) {
                    //next.setDescription("Gap");
                    resultList.add(next);
                }
            }
            //add end
            ProgramModel p1 = new ProgramModel(resultList.get(resultList.size() - 1).getEndTime(), endTime);
            resultList.add(p1);
        }
        for (ProgramModel programModel : resultList) {
            if (programModel.getStartTime() > programModel.getEndTime()) {
                //Toast.makeText(this, "Great", Toast.LENGTH_LONG).show();
            }
        }
        return resultList;
    }

    @Override
    public void onItemClick(View view, int position) {
        TextView title = view.findViewById(R.id.program_title);
        Toast.makeText(this, "Item with position clicked: " + position + " and description " + title.getText(), Toast.LENGTH_SHORT).show();
    }
}
