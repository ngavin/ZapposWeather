package gavinnicol.zapposweather;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Contains data required by the UI to display the current conditions
 */
public class CurrentConditionsData extends BaseData {
    private String locationName;
    private int currentTemp;

    public CurrentConditionsData(String iconID, int currentTemp, String locationName, Context context) {
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

    /**
     * Temp is in F
     *
     * @return Temp in F
     */
    public int getCurrentTemp() {
        return currentTemp;
    }
}
