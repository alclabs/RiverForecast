package com.alcshare.alclabs.riverforecast;

public class RiverData {
    private final String stationName;
    private final String riverName;
    private final FlowLevel forecast;
    private final FlowLevel observed;
    private final CriticalLevels criticalLevels;

    public RiverData(String stationName, String riverName, FlowLevel forecast, FlowLevel observed, CriticalLevels criticalLevels) {
        this.stationName = stationName;
        this.riverName = riverName;
        this.forecast = forecast;
        this.observed = observed;
        this.criticalLevels = criticalLevels;
    }

    public String getStationName() {
        return stationName;
    }

    public String getRiverName() {
        return riverName;
    }

    public FlowLevel getForecast() {
        return forecast;
    }

    public FlowLevel getObserved() {
        return observed;
    }

    public CriticalLevels getCriticalLevels() {
        return criticalLevels;
    }

    @Override
    public String toString() {
        return "RiverData{" +
                "stationName='" + stationName + '\'' +
                "riverName='" + riverName + '\'' +
                ", forecast=" + forecast +
                ", observed=" + observed +
                ", criticalFlowLevels=" + criticalLevels +
                '}';
    }
}
