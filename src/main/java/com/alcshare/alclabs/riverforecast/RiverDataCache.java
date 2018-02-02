package com.alcshare.alclabs.riverforecast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RiverDataCache
{
    private final AtomicReference<List<RiverData>> riverDataRef = new AtomicReference<>();

    @NotNull
    public List<RiverData> getRiverData()
    {
        List<RiverData> riverData = riverDataRef.get();
        return (riverData != null) ? riverData : Collections.emptyList();
    }
    public void setRiverData(@NotNull List<RiverData> riverData) {
        riverDataRef.set(riverData);
    }

    @NotNull
    public JSONArray getRiverNames() {
        JSONArray jsonArray = new JSONArray();
        Set<String> names = new HashSet<>();
        List<RiverData> riverData = riverDataRef.get();
        if(riverData != null)
        {
            for (RiverData r : riverData)
            {
                String name = r.getRiverName();
                if(names.add(name)) {
                    jsonArray.put(name);
                }
            }
        }
        return jsonArray;
    }

    public JSONArray getStationNames(@Nullable String forRiver) {
        JSONArray jsonArray = new JSONArray();
        Set<String> names = new HashSet<>();
        List<RiverData> riverData = riverDataRef.get();
        if(riverData != null)
        {
            for (RiverData r : riverData)
            {
                if((forRiver == null) || r.getRiverName().equalsIgnoreCase(forRiver))
                {
                    String name = r.getStationName();
                    if (names.add(name))
                    {
                        jsonArray.put(name);
                    }
                }
            }
        }
        return jsonArray;
    }

}
