package gavinnicol.zapposweather;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Gavin on 1/31/2015.
 */

public class CurrentConditionsAdapter extends ArrayAdapter<CurrentConditionsData> {

    Context context;
    int layoutResourceId;
    CurrentConditionsData data = null;

    public CurrentConditionsAdapter(Context context, int layoutResourceId,
                                    List<CurrentConditionsData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data.get(0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            row.setTag(data);
        }/* else {
             (WeatherHolder) row.getTag();
        }*/

        /*Weather weather = data[position];
        holder.txtTitle.setText(weather.title);
        holder.imgIcon.setImageResource(weather.icon);*/

        return row;
    }
}