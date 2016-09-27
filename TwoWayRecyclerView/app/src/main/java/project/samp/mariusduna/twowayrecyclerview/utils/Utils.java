package project.samp.mariusduna.twowayrecyclerview.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import project.samp.mariusduna.twowayrecyclerview.R;
import project.samp.mariusduna.twowayrecyclerview.model.ProgramModel;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public class Utils {
    private static final DateFormat hoursFormat = new SimpleDateFormat("hh");
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

    public static int getInitialPositionInList(double currentTime, ArrayList<ProgramModel> arrayList) {
        ProgramModel programModel = new ProgramModel();
        programModel.setStartTime((long) currentTime);
        int pos = Collections.binarySearch(arrayList, programModel, comparator);
        if (pos < 0) pos = Math.abs(pos) - 2;
        return pos;
    }

    private static Comparator<ProgramModel> comparator = new Comparator<ProgramModel>() {
        public int compare(ProgramModel u1, ProgramModel u2) {
            return (int) (u1.getStartTime() - u2.getStartTime());
        }
    };

    public static int getInitialPositionInTimelineList(double currentTime, ArrayList<Long> arrayList) {
        int pos = Collections.binarySearch(arrayList, (long) currentTime);
        if (pos < 0) pos = Math.abs(pos) - 2;
        return pos;
    }

    public static int getTimelineOffset(double nowTime, Context ctx) {
        Date date = new Date((long) nowTime);
        long minutes = Integer.parseInt(minutesFormat.format(date));
        if (minutes < 30) {
            return (int) convertMillisecondsToPx(TimeUnit.MINUTES.toMillis(minutes), ctx);
        } else {
            return (int) convertMillisecondsToPx(TimeUnit.MINUTES.toMillis(minutes - 30), ctx);
        }
    }

    public static int getInitialProgramOffsetPx(double programStartTime, double systemTime, Context context) {
        double offsetTime = systemTime - programStartTime;
        return (int) convertMillisecondsToPx((float) offsetTime, context);
    }
}
