package com.alcshare.alclabs.riverforecast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FlowLevel {
    private final Calendar date;
    private final Float level;
    private final Float flow;

    public FlowLevel(Calendar date, Float level, Float flow) {
        this.date = date;
        this.level = level;
        this.flow = flow;
    }

    public Calendar getDate() {
        return date;
    }

    public Float getLevel() {
        return level;
    }

    public Float getFlow() {
        return flow;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String dateString = dateFormat.format(date.getTime());

        return "FlowLevel{" +
                "date=" + dateString +
                ", level=" + level +
                ", flow=" + flow +
                '}';
    }
}
