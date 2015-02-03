package gavinnicol.zapposweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gavin on 2/1/2015.
 */
public class WeatherAdapter extends ArrayAdapter<BaseData> {
    private static final int CURRENT_CONDITIONS = 0;
    private static final int FUTURE_CONDITIONS = 1;
    private static final int NUM_TYPES = 2;
    private Context context;
    private int currentConditionsLayoutId;
    private int futureConditionsLayoutId;

    List<BaseData> data;

    public WeatherAdapter(Context context, int currentConditionsLayoutId, int futureConditionsLayoutId, List<BaseData> data) {
        super(context, currentConditionsLayoutId, futureConditionsLayoutId, data);
        this.currentConditionsLayoutId = currentConditionsLayoutId;
        this.futureConditionsLayoutId = futureConditionsLayoutId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        int type = getItemViewType(position);

        if (row == null) { /*Only inflate we if cannot recycle an old view*/
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (type == CURRENT_CONDITIONS)
                row = inflater.inflate(currentConditionsLayoutId, parent, false);
            else row = inflater.inflate(futureConditionsLayoutId, parent, false);
        }

        if (type == CURRENT_CONDITIONS) setCurrentConditionsData(row, position);
        else setFutureConditionsData(row, position);

        return row;
    }

    private void setFutureConditionsData(View row, int position) {
        FutureConditionsDayData futureConditionsDayData = (FutureConditionsDayData) data.get(position);

        TextView dayView = (TextView) row.findViewById(R.id.day);
        TextView minTempView = (TextView) row.findViewById(R.id.minTemp);
        TextView maxTempView = (TextView) row.findViewById(R.id.maxTemp);
        ImageView iconView = (ImageView) row.findViewById(R.id.currentConditionImage);

        dayView.setText(futureConditionsDayData.getDayName());
        minTempView.setText("Min Temp:  " + futureConditionsDayData.getMinTemp());
        maxTempView.setText("Max Temp:  " + futureConditionsDayData.getMaxTemp());
        iconView.setImageDrawable(futureConditionsDayData.getIcon());
    }

    private void setCurrentConditionsData(View row, int position) {
        CurrentConditionsData currentConditionsData = (CurrentConditionsData) data.get(position);

        TextView locationNameView = (TextView) row.findViewById(R.id.locationName);
        TextView currentTempView = (TextView) row.findViewById(R.id.currentTemp);
        ImageView iconView = (ImageView) row.findViewById(R.id.currentConditionImage);

        locationNameView.setText(currentConditionsData.getLocationName());
        currentTempView.setText("Current Temp:  " + currentConditionsData.getCurrentTemp());
        iconView.setImageDrawable(currentConditionsData.getIcon());
    }

    public int getViewTypeCount() {
        return NUM_TYPES;
    }

    /**
     * Index 0 is the current conditions, every other is the forecast
     *
     * @param position Index we are asking about
     *
     * @return Int correpsonding to the correct type
     */
    public int getItemViewType(int position) {
        if (position == 0) return CURRENT_CONDITIONS;
        else return FUTURE_CONDITIONS;
    }
}
