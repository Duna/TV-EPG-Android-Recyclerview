package project.samp.mariusduna.twowayrecyclerview.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.concurrent.TimeUnit;

import project.samp.mariusduna.twowayrecyclerview.R;

/**
 * Created by Marius Duna on 9/15/2016.
 */
public class Utils {
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

}
