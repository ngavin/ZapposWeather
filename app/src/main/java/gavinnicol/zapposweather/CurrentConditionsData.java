package gavinnicol.zapposweather;

import android.graphics.drawable.Drawable;

/**
 * Contains data required by the UI to display the current conditions
 */
public class CurrentConditionsData extends BaseData {
    private String locationName;
    private double currentTemp;

    public CurrentConditionsData(String iconID, double currentTemp, String locationName) {
        setIcon(iconID);
        this.currentTemp = currentTemp;
        this.locationName = locationName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }
}
