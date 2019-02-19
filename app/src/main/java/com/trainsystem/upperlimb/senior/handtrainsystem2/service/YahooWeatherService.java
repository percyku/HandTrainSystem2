package com.trainsystem.upperlimb.senior.handtrainsystem2.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.trainsystem.upperlimb.senior.handtrainsystem2.data.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by percyku on 2017/5/7.
 */

public class YahooWeatherService {

    private WeatherServiceCallback callback;
    private String location;

    private Exception error;

    public YahooWeatherService(WeatherServiceCallback callback) {
        this.callback = callback;
    }

    public String getLocation() {
        return location;
    }

    public void refreshWeather(String l) {
        this.location = l;

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                String unit ="c";

//                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")", location);
                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='" + unit + "'", location);
// String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text="taipei,tw")");
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));
//                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20u=%22c%22%20and%20woeid=%202306179&format=json");
                try {
                    URL url = new URL(endpoint);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                    Log.e("Json",""+result.toString());

                    return result.toString();
                } catch (Exception e) {
                    error = e;
                }


                return null;
            }


            @Override
            protected void onPostExecute(String s) {

                if (s == null && error != null) {
                    callback.serviceFailure(error);
                    return;
                }

                try {
                    JSONObject data = new JSONObject(s);

                    JSONObject queryResults = data.optJSONObject("query");
                    int count = queryResults.optInt("count");

                    if (count == 0) {

                        callback.serviceFailure(new LocationWeatherrException("No weather information found for" + location));

                        return;
                    }


                    Channel channel = new Channel();
                    channel.populator(queryResults.optJSONObject("results").optJSONObject("channel"));

                    callback.serviceSuccess(channel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(location);
    }


    public class LocationWeatherrException extends Exception {

        public LocationWeatherrException(String detailMessage) {
            super(detailMessage);
        }

    }
}
