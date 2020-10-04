package me.mrcreepton.cycledlife.utils;

import me.mrcreepton.cycledlife.models.TimeData;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static TimeData getTimeFromSeconds(int seconds)
    {

        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long secs = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        return new TimeData(hours, minutes, secs);
    }

}
