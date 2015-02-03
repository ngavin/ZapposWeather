package gavinnicol.zapposweather;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Displays weather for your current location
 */
public class WeatherDisplayActivity extends Activity {
    private static final int FAHRENHEIT = 0;
    private static final int CELSIUS = 1;
    private final String Tag = "ZapposWeather";
    private int units = FAHRENHEIT;
    private Location userLocation;
    private boolean first;
    private List<BaseData> allData;
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        first = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather_display);

        Button unitSwitcher = (Button) findViewById(R.id.unit_switcher);
        unitSwitcher.setOnClickListener(new UnitSwitcherListener());

        /*settingsDrawerView = (ListView) findViewById(R.id.left_drawer);
        settingsDrawerView.setAdapter(new SettingsDrawerAdapter(getApplicationContext(), R.id.drawer_layout));
        settingsDrawerView.setOnItemClickListener(new SettingsDrawerListener());*/
    }

    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        enableGPS();
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
                enableWeatherAdapter();
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

    private void enableWeatherAdapter() {
        weatherAdapter = new WeatherAdapter(getApplicationContext(), R.layout.current_conditions_layout, R.layout.forecast_row_layout, units, allData);

        ListView weatherView = (ListView) findViewById(R.id.weather_list);
        weatherView.setAdapter(weatherAdapter);

        weatherAdapter.notifyDataSetChanged();
    }

    private void getWeatherData() {
        List<CurrentConditionsData> currentConditions;
        List<FutureConditionsDayData> forecast;

        currentConditions = getCurrentConditions();
        forecast = getForecast();

        /*This is here because sometimes the JSON response from the forecast is incomplete*/
        if (first || !forecast.isEmpty()) {
            first = false;
            allData = new ArrayList<>();
            allData.addAll(currentConditions);
            allData.addAll(forecast);
        }
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
        int minTemp;
        int maxTemp;
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

    private int getMaxTemp(JSONObject currentDay) throws JSONException {
        JSONObject temperatureObject = currentDay.getJSONObject("temp");
        return temperatureObject.getInt("max");
    }

    private int getMinTemp(JSONObject currentDay) throws JSONException {
        JSONObject temperatureObject = currentDay.getJSONObject("temp");
        return temperatureObject.getInt("min");
    }

    private CurrentConditionsData createCurrentConditionsData(JSONObject response) {
        String locationName;
        String iconID;
        int currentTemp;
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

    private int getCurrentTemp(JSONObject response) throws JSONException {
        JSONObject currentTempObject = (JSONObject) response.get("main");
        return currentTempObject.getInt("temp");
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
                "&mode=json&units=imperial";
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
                "&cnt=5&mode=json&units=imperial";
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

    private class UnitSwitcherListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (units == FAHRENHEIT) units = CELSIUS;
            else units = FAHRENHEIT;
            weatherAdapter.switchUnits();
            weatherAdapter.notifyDataSetChanged();
        }
    }
}