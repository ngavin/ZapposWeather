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
public class ForecastAdapter extends ArrayAdapter<FutureConditionsDayData> {
    Context context;
    int layoutResourceId;
    List<FutureConditionsDayData> data;

    public ForecastAdapter(Context context, int layoutResourceId, List<FutureConditionsDayData> data) {
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
        TextView dayView = (TextView) row.findViewById(R.id.day);
        TextView minTempView = (TextView) row.findViewById(R.id.minTemp);
        TextView maxTempView = (TextView) row.findViewById(R.id.maxTemp);
        ImageView iconView = (ImageView) row.findViewById(R.id.currentConditionImage);
        dayView.setText(data.get(position).getDayName());
        minTempView.setText("Min Temp:  " + data.get(position).getMinTemp());
        maxTempView.setText("Max Temp:  " + data.get(position).getMaxTemp());
        iconView.setImageDrawable(data.get(position).getIcon());

        return row;
    }
}
