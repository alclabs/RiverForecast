package com.alcshare.alclabs.riverforecast;

import org.json.JSONArray;
import org.json.JSONException;

public enum ValueType
{
    FORECAST_FLOW(1),
    FORECAST_LEVEL(2),
    OBSERVED_FLOW(3),
    OBSERVED_LEVEL(4),
    DANGER_LEVEL(5),
    FLOOD_LEVEL(6),
    MONITOR_LEVEL(7);

    private final int num;
    ValueType(int num) {
        this.num = num;
    }

    public static JSONArray valuesAsJsonArray() throws JSONException {
        JSONArray json = new JSONArray();
        for(ValueType valueType : values()) {
            json.put(valueType.name());
        }
        return json;
    }
}
