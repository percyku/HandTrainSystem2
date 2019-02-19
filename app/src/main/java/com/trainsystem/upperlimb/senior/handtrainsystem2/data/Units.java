package com.trainsystem.upperlimb.senior.handtrainsystem2.data;

import org.json.JSONObject;

/**
 * Created by percyku on 2017/5/7.
 */

public class Units implements  JSONPopulator {

    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populator(JSONObject data) {
        temperature=data.optString("temperature");

    }
}
