package com.github.handioq.weatherapp.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.handioq.weatherapp.R;
import com.github.handioq.weatherapp.utils.IconUtils;
import com.github.handioq.weatherapp.utils.MeasurementUnitsConverter;
import com.survivingwithandroid.weather.lib.model.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HourForecastExpListAdapter extends BaseExpandableListAdapter {

    private ArrayList<Map<String, Weather>> weatherGroups;
    private Context mContext;

    private SharedPreferences sharedPrefs;

    public HourForecastExpListAdapter(Context context, ArrayList<Map<String, Weather>> weatherGroups) {
        this.mContext = context;
        this.weatherGroups = weatherGroups;
    }

    @Override
    public int getGroupCount() {
        return weatherGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return weatherGroups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return weatherGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return weatherGroups.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hour_forecast_group_view, null);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        TextView textGroupInfo = (TextView) convertView.findViewById(R.id.textGroupInfo);
        TextView textGroupInfoCenter = (TextView) convertView.findViewById(R.id.textGroupInfoCenter);
        ImageView weatherImage = (ImageView) convertView.findViewById(R.id.weatherImage);

        List<String> timeList = new ArrayList<String>(weatherGroups.get(groupPosition).keySet());
        String time = timeList.get(0); // get time

        List<Weather> weatherList = new ArrayList<Weather>(weatherGroups.get(groupPosition).values());
        Weather currentWeather = weatherList.get(0); // get weather for this time

        Resources res = mContext.getResources();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String unit = sharedPrefs.getString("units_list", res.getString(R.string.degree_celsius));

        int degreeTemp;

        if (unit.equals(res.getString(R.string.degree_celsius))) {
            degreeTemp = Math.round(currentWeather.temperature.getTemp());
        }
        else {
            degreeTemp = Math.round(MeasurementUnitsConverter.celsiusToFahrenheit(currentWeather.temperature.getTemp()));
        }

        String temperature = degreeTemp + unit;
        String condition = currentWeather.currentCondition.getCondition();

        String groupText = String.format(res.getString(R.string.hour_group_info_title), temperature);
        String groupTextCenter = String.format(res.getString(R.string.hour_group_info_center), condition);
        textGroupInfo.setText(groupText);
        textGroupInfoCenter.setText(groupTextCenter);
        textGroup.setText(time);

        // imageLoader.displayImage(weatherHttpClient.getQueryImageURL(iconID), cityImage);
        weatherImage.setImageDrawable(IconUtils.getIconFromDrawable(mContext, currentWeather.currentCondition.getIcon()));

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hour_forecast_child_view, null);
        }

        TextView tempTextView = (TextView) convertView.findViewById(R.id.tempText);
        TextView pressureText = (TextView) convertView.findViewById(R.id.pressureText);
        TextView humidityText = (TextView) convertView.findViewById(R.id.humidityText);
        TextView condText = (TextView) convertView.findViewById(R.id.condText);
        TextView condDescrText = (TextView) convertView.findViewById(R.id.condDescrText);
        TextView windSpeedText = (TextView) convertView.findViewById(R.id.windSpeedText);

        List<Weather> weatherList = new ArrayList<Weather>(weatherGroups.get(groupPosition).values());
        Weather currentWeather = weatherList.get(0);

        Resources res = mContext.getResources();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String unit = sharedPrefs.getString("units_list", res.getString(R.string.degree_celsius));

        int degreeTemp;

        if (unit.equals(res.getString(R.string.degree_celsius))) {
            degreeTemp = Math.round(currentWeather.temperature.getTemp());
        }
        else {
            degreeTemp = Math.round(MeasurementUnitsConverter.celsiusToFahrenheit(currentWeather.temperature.getTemp()));
        }

        String temp = degreeTemp + unit;
        String condition = String.format(res.getString(R.string.condition), currentWeather.currentCondition.getCondition());
        String conditionDescr = String.format(res.getString(R.string.condition_description), currentWeather.currentCondition.getDescr());
        String wind = String.format(res.getString(R.string.wind), Float.toString(currentWeather.wind.getSpeed()))
                + mContext.getResources().getString(R.string.meters_per_second);
        String pressure = String.format(res.getString(R.string.pressure), Float.toString(currentWeather.currentCondition.getHumidity()))
                +  mContext.getResources().getString(R.string.percent);
        String humidity = String.format(res.getString(R.string.humidity), Float.toString(Math.round(MeasurementUnitsConverter.hpaToMmHg(currentWeather.currentCondition.getPressure()))))
                + mContext.getResources().getString(R.string.mm_hg);

        tempTextView.setText(temp);
        condDescrText.setText(conditionDescr);
        condText.setText(condition);
        windSpeedText.setText(wind);
        pressureText.setText(pressure);
        humidityText.setText(humidity);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}