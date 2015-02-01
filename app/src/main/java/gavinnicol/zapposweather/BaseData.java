package gavinnicol.zapposweather;

import android.graphics.drawable.Drawable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * All UI data requires an Icon.  This base class contains methods required to get this icon.
 */
public class BaseData {
    protected Drawable icon;

    protected void setIcon(String iconID) {
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

    private String getIconRequestURI(String iconID) {
        return "http://openweathermap.org/img/w/" + iconID + ".png";
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
}
