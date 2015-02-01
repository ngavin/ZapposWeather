package gavinnicol.zapposweather;

import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Contains data required by the UI to display a day of the weekly forecast
 */
public class FutureConditionsDayData extends BaseData {
    private double minTemp;
    private double maxTemp;
    private String dayName;

    public FutureConditionsDayData(String iconID, double minTemp, double maxTemp, int dayOffset) {
        setIcon(iconID);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, dayOffset);
        dayName = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public Drawable getIcon() {
        return icon;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getDayName() {
        return dayName;
    }
}
