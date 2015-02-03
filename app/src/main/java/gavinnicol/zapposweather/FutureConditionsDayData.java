package gavinnicol.zapposweather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Contains data required by the UI to display a day of the weekly forecast
 */
public class FutureConditionsDayData extends BaseData {
    private int minTemp;
    private int maxTemp;
    private String dayName;

    public FutureConditionsDayData(String iconID, int minTemp, int maxTemp, int dayOffset, Context context) {
        setIcon(iconID, context);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, dayOffset);
        dayName = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public Drawable getIcon() {
        return icon;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public String getDayName() {
        return dayName;
    }
}
