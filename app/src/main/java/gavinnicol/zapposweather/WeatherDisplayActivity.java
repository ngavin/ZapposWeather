package gavinnicol.zapposweather;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gavinnicol.zapposweather.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class WeatherDisplayActivity extends Activity {
    private final String Tag = "ZapposWeather";
    private final String openWeatherMapAPIKey = "76b40e52ad7d4cd161158f7b43ee2fd1";
    private String units = "imperial";

    List<CurrentConditionsData> currentConditions;

    List<FutureConditionsDayData> forecast;


    private Location userLocation;

    private Handler mHideHandler = new Handler();

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /** The instance of the {@link SystemUiHider} for this activity. */
    private SystemUiHider mSystemUiHider;

    /**
     * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS}
     * milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;
    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_weather_display);
        enableFullscreenSystemCalls();

        enableGPS();

    }

    /**
     * These are auto-generated by IntelliJ
     */
    private void enableFullscreenSystemCalls() {
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi (Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // NOTE: delete dummy_button
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Enables the GPS and requests location updates
     */
    private void enableGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context
                .LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                userLocation = location;

                getWeatherData();

                enableCurrentConditionsAdapter();
                enableForecastConditionsAdapter();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0,
                locationListener);
    }

    private void enableForecastConditionsAdapter() {
        if (!forecast.isEmpty()) {
            ForecastAdapter forecastAdapter = new ForecastAdapter(getApplicationContext(), R.layout.forecast_row_layout, forecast);

            ListView forecastView = (ListView) findViewById(R.id.forecast_list);
            forecastView.setAdapter(forecastAdapter);

            forecastAdapter.notifyDataSetChanged();
        }
    }

    private void enableCurrentConditionsAdapter() {
        if (!currentConditions.isEmpty()) {

            CurrentConditionsAdapter currentConditionsAdapter = new CurrentConditionsAdapter(getApplicationContext(), R.layout.current_conditions_layout, currentConditions);

            ListView currentConditionsView = (ListView) findViewById(R.id.current_conditions_list);
            currentConditionsView.setAdapter(currentConditionsAdapter);

            currentConditionsAdapter.notifyDataSetChanged();
        }
    }

    private void getWeatherData() {
        currentConditions = getCurrentConditions();
        forecast = getForecast();
    }

    private List<FutureConditionsDayData> getForecast() {
        String requestURI = getForecastURI();
        JSONObject response = getJSONData(new HttpGet(requestURI));
        List<FutureConditionsDayData> forecast = new ArrayList<>();
        if (response != null) forecast = createForecastData(response);
        else Log.w(Tag, "Response for forecast is null");
        return forecast;
    }

    private List<CurrentConditionsData> getCurrentConditions() {
        String requestURI = getCurrentConditonsURI();
        JSONObject response = getJSONData(new HttpGet(requestURI));
        List<CurrentConditionsData> currentCondition = new ArrayList<>();
        if (response != null) currentCondition.add(createCurrentConditionsData(response));
        return currentCondition;
    }

    private List<FutureConditionsDayData> createForecastData(JSONObject response) {
        String iconID;
        double minTemp;
        double maxTemp;
        List<FutureConditionsDayData> weekForecastData = new ArrayList<>();
        try {
            JSONArray days = response.getJSONArray("list");
            for (int i = 0; i < days.length(); ++i) {
                JSONObject currentDay = (JSONObject) days.get(i);
                iconID = getIconID(currentDay);
                minTemp = getMinTemp(currentDay);
                maxTemp = getMaxTemp(currentDay);
                FutureConditionsDayData dayData = new FutureConditionsDayData(iconID, minTemp, maxTemp, i, getApplicationContext());
                weekForecastData.add(dayData);
            }
        } catch (Exception e) {
            Log.e(Tag, "Accessing data in JSON Object failed");
            e.printStackTrace();
        }
        return weekForecastData;
    }

    private double getMaxTemp(JSONObject currentDay) throws JSONException {
        JSONObject temperatureObject = (JSONObject) currentDay.getJSONObject("temp");
        return temperatureObject.getDouble("max");
    }

    private double getMinTemp(JSONObject currentDay) throws JSONException {
        JSONObject temperatureObject = (JSONObject) currentDay.getJSONObject("temp");
        return temperatureObject.getDouble("min");
    }

    private CurrentConditionsData createCurrentConditionsData(JSONObject response) {
        String locationName;
        String iconID;
        double currentTemp;
        CurrentConditionsData currentConditionsData = null;
        try {
            locationName = response.getString("name");
            iconID = getIconID(response);
            currentTemp = getCurrentTemp(response);
            currentConditionsData = new CurrentConditionsData(iconID, currentTemp, locationName, getApplicationContext());
        } catch (Exception e) {
            Log.e(Tag, "Accessing data in JSON Object failed");
        }
        return currentConditionsData;
    }

    private double getCurrentTemp(JSONObject response) throws JSONException {
        JSONObject currentTempObject = (JSONObject) response.get("main");
        return currentTempObject.getDouble("temp");
    }

    /**
     * Finds the icon ID of the current weather icon
     *
     * @param response "Weather" array must be at the top level
     *
     * @return String of the icon ID
     *
     * @throws JSONException
     */
    private String getIconID(JSONObject response) throws JSONException {
        JSONArray weatherArray = (JSONArray) response.get("weather");
        JSONObject weatherObject = (JSONObject) weatherArray.get(0);
        return weatherObject.getString("icon");
    }

    /**
     * Executes network request, parses response into JSON, and returns
     *
     * @param request API Request to execute
     *
     * @return JSON parsing of API response
     */
    private JSONObject getJSONData(HttpUriRequest request) {
        HttpResponse httpResponse = getHttpResponse(request);
        String stringResponse = extractJSONResponse(httpResponse);
        return parseStringToJSON(stringResponse);
    }

    /**
     * Gets the URI for the Current Conditions API call
     *
     * @return String containing the API call
     */
    private String getCurrentConditonsURI() {
        return "http://api.openweathermap.org/data/2.5/weather?lat=" +
                userLocation.getLatitude() +
                "&lon=" +
                userLocation.getLongitude() +
                "&mode=json&units=" +
                units;
    }

    /**
     * Gets the URI for the Forecast API call
     *
     * @return String containing API call
     */
    private String getForecastURI() {
        return "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" +
                userLocation.getLatitude() +
                "&lon=" +
                userLocation.getLongitude() +
                "&cnt=5&mode=json&units=" +
                units;
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

    /**
     * Gets Json string from the stream contained in response
     *
     * @param response Http response containing the stream
     *
     * @return String representation of the Json
     */
    private String extractJSONResponse(HttpResponse response) {
        String jsonResponse = null;
        try {
            jsonResponse = new HttpJSONResponseExtractor().execute(response).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    /**
     * Parses JSONString into a JSONObject
     *
     * @param JSONString String representation of a JSON Object
     *
     * @return JSONObject of JSONString
     */
    private JSONObject parseStringToJSON(String JSONString) {
        JSONObject JSONObject = null;
        try {
            JSONObject = new JSONObject(JSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSONObject;
    }
}