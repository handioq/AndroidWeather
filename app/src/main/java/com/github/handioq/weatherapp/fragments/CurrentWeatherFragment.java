package com.github.handioq.weatherapp.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.handioq.weatherapp.client.AppWeatherClient;
import com.github.handioq.weatherapp.R;
import com.github.handioq.weatherapp.utils.IconUtils;
import com.github.handioq.weatherapp.utils.MeasurementUnitsConverter;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

public class CurrentWeatherFragment extends Fragment {

    AppWeatherClient appWeatherClient = AppWeatherClient.getInstance();
    private City selectedCity;

    private TextView tempTextView;
    private TextView condDescrTextView;
    private TextView condTextView;
    private TextView windSpeedTextView;
    private TextView pressureTextView;
    private TextView humidityTextView;
    private WeatherIconView weatherIcon;

    private SharedPreferences sharedPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.current_weather_layout, container, false);

        selectedCity = appWeatherClient.getSelectedCity();

        getActivity().setTitle(selectedCity.getName());

        tempTextView = (TextView) V.findViewById(R.id.tempTextView);
        condDescrTextView = (TextView) V.findViewById(R.id.condDescrTextView);
        condTextView = (TextView) V.findViewById(R.id.condTextView);
        windSpeedTextView = (TextView) V.findViewById(R.id.windSpeedTextView);
        humidityTextView = (TextView) V.findViewById(R.id.humidityTextView);
        pressureTextView = (TextView) V.findViewById(R.id.pressureTextView);
        weatherIcon = (WeatherIconView) V.findViewById(R.id.mainWeatherIcon);

        appWeatherClient.getClient().getCurrentCondition(new WeatherRequest(selectedCity.getId()), new WeatherClient.WeatherEventListener() {
            @Override public void onWeatherRetrieved(CurrentWeather currentWeather) {
                float currentTemp = currentWeather.weather.temperature.getTemp();
                Log.d("CWF", "City ["+currentWeather.weather.location.getCity()+"] Current temp ["+currentTemp+"]");

                Resources res = getActivity().getResources();
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String unit = sharedPrefs.getString("units_list", res.getString(R.string.degree_celsius));

                int degreeTemp;
                if (unit.equals(res.getString(R.string.degree_celsius))) {
                    degreeTemp = Math.round(currentTemp);
                }
                else {
                    degreeTemp = Math.round(MeasurementUnitsConverter.celsiusToFahrenheit(currentTemp));
                }

                String temp = degreeTemp + unit;

                String condition = String.format(res.getString(R.string.condition), currentWeather.weather.currentCondition.getCondition());
                String conditionDescr = String.format(res.getString(R.string.condition_description), currentWeather.weather.currentCondition.getDescr());
                String wind = String.format(res.getString(R.string.wind), Float.toString(currentWeather.weather.wind.getSpeed()))
                        + getActivity().getResources().getString(R.string.meters_per_second);
                String pressure = String.format(res.getString(R.string.pressure), Float.toString(currentWeather.weather.currentCondition.getHumidity()))
                        +  getActivity().getResources().getString(R.string.percent);
                String humidity = String.format(res.getString(R.string.humidity), Float.toString(Math.round(MeasurementUnitsConverter.hpaToMmHg(currentWeather.weather.currentCondition.getPressure()))))
                        + getActivity().getResources().getString(R.string.mm_hg);

                tempTextView.setText(temp);
                windSpeedTextView.setText(wind);
                pressureTextView.setText(pressure);
                humidityTextView.setText(humidity);

                condDescrTextView.setText(condition);
                condTextView.setText(conditionDescr);

                weatherIcon.setIconResource(IconUtils.getIconResource(getActivity(), currentWeather.weather.currentCondition.getWeatherId(), currentWeather.weather.currentCondition.getIcon()));
                //weatherIcon.setIconResource(getString(R.string.wi_day_sunny_overcast));
                //weatherIcon.setIconSize(100);
                //weatherIcon.setIconColor(Color.BLACK);
            }

            @Override public void onWeatherError(WeatherLibException e) {
                Log.d("WL", "Weather Error - parsing data");
                e.printStackTrace();
            }

            @Override public void onConnectionError(Throwable throwable) {
                Log.d("WL", "Connection error");
                throwable.printStackTrace();
            }
        });

        return V;
    }

}