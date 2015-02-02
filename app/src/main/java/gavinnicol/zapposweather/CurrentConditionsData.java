package gavinnicol.zapposweather;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Contains data required by the UI to display the current conditions
 */
public class CurrentConditionsData extends BaseData {
    private String locationName;
    private double currentTemp;

    public CurrentConditionsData(String iconID, double currentTemp, String locationName, Context context) {
        setIcon(iconID, context);
        this.currentTemp = currentTemp;
        this.locationName = locationName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getCurrentTemp() {
        return Double.toString(currentTemp);
    }
}
