package com.trainsystem.upperlimb.senior.handtrainsystem2.data;

import org.json.JSONObject;

/**
 * Created by percyku on 2017/5/7.
 */

public class Channel implements JSONPopulator {

    private Units units;
    private Item item;


    public Units getUnits(){
        return  units;
    }

    public  Item getItem(){
        return  item;
    }
    @Override
    public void populator(JSONObject data) {

        units=new Units();
        units.populator(data.optJSONObject("units"));

        item=new Item();
        item.populator(data.optJSONObject("item"));
    }
}
