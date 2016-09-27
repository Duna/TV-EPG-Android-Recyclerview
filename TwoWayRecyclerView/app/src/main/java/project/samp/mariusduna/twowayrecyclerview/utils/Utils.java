package project.samp.mariusduna.twowayrecyclerview.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import project.samp.mariusduna.twowayrecyclerview.R;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public class Utils {
    private static final DateFormat minutesFormat = new SimpleDateFormat("mm");
    private static final DateFormat secondsFormat = new SimpleDateFormat("ss");

    private static float pxPerMinConstant(Context context) {
        return convertDpToPixel(context.getResources().getDimension(R.dimen.epg_width_one_min), context);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertMillisecondsToDp(long milliseconds, Context context) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        return context.getResources().getDimension(R.dimen.epg_width_one_min) * minutes;
    }

   /* public static float convertDpToMilliseconds(float dp, Context context) {
        float oneMin = context.getResources().getDimension(R.dimen.epg_width_one_min);
        return dp / oneMin;
    }*/

    public static float convertMillisecondsToPx(float milliseconds, Context context) {
        return milliseconds * pxPerMinConstant(context) / TimeUnit.MINUTES.toMillis(1);
    }

    public static float convertPxToMilliseconds(float px, Context context) {
        return TimeUnit.MINUTES.toMillis(1) * px / pxPerMinConstant(context);
    }

    //this will search only 4-5 programs and then return
    public static int getInitialPositionInList(double currentTime, ArrayList<ProgramModel> arrayList) {
        int half = arrayList.size() / 2;
        if (arrayList.get(half).getStartTime() < (long) currentTime) {
            for (int i = half; i >= 0; i--) {
                if (currentTime > arrayList.get(i).getStartTime()) {
                    return i;
                }
            }
        } else {
            for (int i = half; i < arrayList.size(); i++) {
                if (arrayList.get(i).getEndTime() > (long) currentTime) {
                    return i;
                }
            }
        }
        return half;
    }

    public static int getInitialPositionInTimelineList(double currentTime, ArrayList<Long> arrayList) {
        int pos = Collections.binarySearch(arrayList, (long) currentTime);
        if (pos < 0) pos = Math.abs(pos) - 2;
        return pos;
    }

    public static int getTimelineOffset(double nowTime, Context ctx) {
        Date date = new Date((long) nowTime);
        long minutes = Integer.parseInt(minutesFormat.format(date));
        if (minutes < 30) {
            return (int)convertMillisecondsToPx(TimeUnit.MINUTES.toMillis(minutes), ctx);
        } else {
            return (int)convertMillisecondsToPx(TimeUnit.MINUTES.toMillis(minutes-30), ctx);
        }
    }

    public static float getInitialOffset(double nowTime, ProgramModel program) {
        Date date1 = new Date((long) nowTime);
        long minutes1 = Integer.parseInt(minutesFormat.format(date1));
        long seconds1 = Integer.parseInt(secondsFormat.format(date1));
        long totalSeconds = TimeUnit.MINUTES.toSeconds(minutes1) + seconds1;
        long diffProgramStart = TimeUnit.MILLISECONDS.toSeconds(program.getEndTime() - program.getStartTime()) - totalSeconds;
        float diffProgramStartMillis = TimeUnit.SECONDS.toMillis(diffProgramStart);
        return diffProgramStartMillis;
    }
}
