package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.data.Channel;
import com.trainsystem.upperlimb.senior.handtrainsystem2.data.Item;
import com.trainsystem.upperlimb.senior.handtrainsystem2.service.WeatherServiceCallback;
import com.trainsystem.upperlimb.senior.handtrainsystem2.service.YahooWeatherService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements WeatherServiceCallback {
    private TextToSpeech textToSpeech;
    PercentRelativeLayout p;

    public static int GET_WEATHER_FROM_CURRENT_LOCATION = 0x00001;

    private TextView time, day, temperature, c;
    private ImageView imageWeather;


    private YahooWeatherService service;

    private ProgressDialog dialog;

//https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20u%3D%22c%22%20and%20woeid%3D%202306179&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainv4);
        initial();

        service = new YahooWeatherService(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("載入中...");
        dialog.show();
        service.refreshWeather("taipei,tw");


    }

    private void initial() {

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });


        p = (PercentRelativeLayout) this.findViewById(R.id.secretAcross);
        p.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                startActivity(new Intent(getApplicationContext(), DatabaseActivity.class));
                return false;
            }
        });

        time = (TextView) this.findViewById(R.id.maincontext_title);
        day = (TextView) this.findViewById(R.id.maintext_day);
        temperature = (TextView) this.findViewById(R.id.maintext_temperature);
        imageWeather = (ImageView) this.findViewById(R.id.maincontext_Weather);

        time.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AdobeFanHeitiStd-Bold.otf"));
        day.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AdobeFanHeitiStd-Bold.otf"));
        temperature.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AdobeFanHeitiStd-Bold.otf"));


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf6 = new SimpleDateFormat("a");
        SimpleDateFormat sdf5 = new SimpleDateFormat("E");
        SimpleDateFormat sdf4 = new SimpleDateFormat("MM-dd");

        Date dt = new Date();
        String dts = sdf.format(dt);
        String dts2 = sdf6.format(dt);
        if (dts2.equals("上午"))
            time.setText(dts);
        else
            time.setText(dts);
        Log.e("dts", dts);


        day.setText(sdf5.format(dt) + " " + sdf4.format(dt));

    }


    public void OnGame1(View view) {
        ttsTest("歡迎進入" + getText(R.string.game1) + "遊戲");
        startActivity(new Intent(getApplicationContext(), Game.class));
    }

    public void OnGame2(View view) {
        ttsTest("歡迎進入" + getText(R.string.game2) + "遊戲");

        startActivity(new Intent(getApplicationContext(), Game2.class));

    }

    public void OnGame3(View view) {
        ttsTest("歡迎進入" + getText(R.string.game3) + "遊戲");

        startActivity(new Intent(getApplicationContext(), Game3.class));

    }

    public void OnRecord(View view) {
        ttsTest("歡迎觀看" + getText(R.string.record));

        startActivity(new Intent(getApplicationContext(), RecordActivity.class));

    }

    private void ttsTest(String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();

        Item item = channel.getItem();

        int resource = getResources().getIdentifier("drawable/icon_" + item.getCondition().getCode(), null, getPackageName());

        @SuppressWarnings("deprecation")
        Drawable weatherIcon = getResources().getDrawable(resource);


        imageWeather.setImageDrawable(weatherIcon);

//        String temF=item.getCondition().getTemperature()+ "\u00B0"+channel.getUnits().getTemperature().substring(0,2);
//
//        Double c=(Integer.parseInt(temF)+40)/1.8-40;
//        temperature.setText(""+c);
//        temperature.setText(item.getCondition().getTemperature()+ "\u00B0"+channel.getUnits().getTemperature().split("°C"));
        String str = item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature();
        String tem[] = str.split("°");
        temperature.setText(tem[0]);
//        Log.e("1", item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature().split("°C"));
//        Log.e("2", "\u00B0" + channel.getUnits().getTemperature().split("°C"));
//          temperature.setText(item.getCondition().getTemperature());

    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();

        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}
