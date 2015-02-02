package gavinnicol.zapposweather;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * All UI data requires an Icon.  This base class contains methods required to get this icon.
 */
public class BaseData {
    protected Drawable icon;

    protected void setIcon(String iconID, Context context) {
        Resources resources = context.getResources();
        int resourceID = resources.getIdentifier("i" + iconID, "drawable", context.getPackageName());
        icon = resources.getDrawable(resourceID);
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
