package com.alcshare.alclabs.riverforecast;

public class CriticalLevels
{
    private final Float dangerLevel;
    private final Float floodLevel;
    private final Float monitorLevel;

    public CriticalLevels(Float dangerLevel, Float floodLevel, Float monitorLevel) {
        this.dangerLevel = dangerLevel;
        this.floodLevel = floodLevel;
        this.monitorLevel = monitorLevel;
    }

    public Float getDangerLevel() {
        return dangerLevel;
    }

    public Float getFloodLevel() {
        return floodLevel;
    }

    public Float getMonitorLevel() {
        return monitorLevel;
    }

    @Override
    public String toString() {
        return "CriticalLevels{" +
                "dangerLevel=" + dangerLevel +
                ", floodLevel=" + floodLevel +
                ", monitorLevel=" + monitorLevel +
                '}';
    }
}
