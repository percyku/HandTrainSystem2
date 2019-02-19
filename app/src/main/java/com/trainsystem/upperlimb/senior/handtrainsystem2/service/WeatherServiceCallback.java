package com.trainsystem.upperlimb.senior.handtrainsystem2.service;

import com.trainsystem.upperlimb.senior.handtrainsystem2.data.Channel;

/**
 * Created by percyku on 2017/5/7.
 */

public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
