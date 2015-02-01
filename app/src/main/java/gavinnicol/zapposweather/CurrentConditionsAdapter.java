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
 * Created by Gavin on 1/31/2015.
 */

public class CurrentConditionsAdapter extends ArrayAdapter<CurrentConditionsData> {

    Context context;
    int layoutResourceId;
    List<CurrentConditionsData> data;

    public CurrentConditionsAdapter(Context context, int layoutResourceId, List<CurrentConditionsData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) { /*Only inflate we if cannot recycle an old view*/
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        /*Sets data to view*/
        TextView locationNameView = (TextView) row.findViewById(R.id.locationName);
        TextView currentTempView = (TextView) row.findViewById(R.id.currentTemp);
        ImageView iconView = (ImageView) row.findViewById(R.id.currentConditionImage);
        locationNameView.setText(data.get(position).getLocationName());
        currentTempView.setText("Current Temp:  " + data.get(position).getCurrentTemp());
        iconView.setImageDrawable(data.get(position).getIcon());

        return row;
    }
}