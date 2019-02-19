package com.trainsystem.upperlimb.senior.handtrainsystem2.data;

import org.json.JSONObject;

/**
 * Created by percyku on 2017/5/7.
 */

public class Condition implements JSONPopulator {

    private int code;
    private int temperature;
    private String description;


    public int getCode() {
        return code;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    @Override

    public void populator(JSONObject data) {

        code=data.optInt("code");
        temperature=data.optInt("temp");
        description=data.optString("text");
    }
}
