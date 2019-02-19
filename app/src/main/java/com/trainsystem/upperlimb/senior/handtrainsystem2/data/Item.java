package com.trainsystem.upperlimb.senior.handtrainsystem2.data;

import org.json.JSONObject;

/**
 * Created by percyku on 2017/5/7.
 */

public class Item implements JSONPopulator {


    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void populator(JSONObject data) {
        condition=new Condition();
        condition.populator(data.optJSONObject("condition"));

    }
}
