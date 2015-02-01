package gavinnicol.zapposweather;

import android.graphics.drawable.Drawable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 */
public class FutureConditionsDayData {
    private Drawable icon;
    private double minTemp;
    private double maxTemp;
    private int dayOffset;
    private String dayName;

    public FutureConditionsDayData(String iconID, double minTemp, double maxTemp, int dayOffset) {
        getIcon(iconID);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.dayOffset = dayOffset;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, dayOffset);
        SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEEEEEEEEE");
        dayName = dayNameFormatter.format(c.getTime());
    }

    private void getIcon(String iconID) {
        String requestURI = getIconRequestURI(iconID);
        HttpUriRequest httpRequest = new HttpGet(requestURI);
        HttpResponse httpResponse = getHttpResponse(httpRequest);
        Drawable icon = null;
        try {
            icon = new HttpFileResponseExtractor().execute(httpResponse).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.icon = icon;
    }

    /**
     * Retrieves response from OpenWeatherMap API
     *
     * @param request Request to execute
     *
     * @return Response from OpenWeatherMap
     */
    private HttpResponse getHttpResponse(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            response = new HttpRequestExecutor().execute(request).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getIconRequestURI(String iconID) {
        return "http://openweathermap.org/img/w/" + iconID + ".png";
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

    public int getDayOffset() {
        return dayOffset;
    }

    public String getDayName() {
        return dayName;
    }
}
