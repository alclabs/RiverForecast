package com.alcshare.alclabs.riverforecast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonHelper
{
    @NotNull
    public List<Mapping> toMappings(@NotNull JSONArray jsonArray) throws JSONException {
        List<Mapping> mappings = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String riverName = jsonObject.getString("riverName");
            String stationName = jsonObject.getString("stationName");
            String valueType = jsonObject.getString("valueType");
            String mbPath = jsonObject.getString("mbPath");
            if(riverName != null && stationName != null && valueType != null && mbPath != null) {
                mappings.add(new Mapping(riverName, stationName, valueType, mbPath));
            }
        }
        return mappings;
    }

    @NotNull
    public JSONArray toJson(@NotNull List<Mapping> mappings) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(Mapping mapping : mappings) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("riverName", mapping.getRiverName());
            jsonObject.put("stationName", mapping.getStationName());
            jsonObject.put("valueType", mapping.getValueType());
            jsonObject.put("mbPath", mapping.getMbPath());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
}
