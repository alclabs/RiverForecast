package com.alcshare.alclabs.riverforecast;

import org.jetbrains.annotations.NotNull;

public class Mapping {
    @NotNull private final String riverName;
    @NotNull private final String stationName;
    @NotNull private final String valueType;
    @NotNull private final String mbPath;

    public Mapping(@NotNull String riverName, @NotNull String stationName, @NotNull String valueType, @NotNull String mbPath) {
        this.riverName = riverName;
        this.stationName = stationName;
        this.valueType = valueType;
        this.mbPath = mbPath;
    }

    @NotNull
    public String getRiverName()
    {
        return riverName;
    }

    @NotNull
    public String getStationName() {
        return stationName;
    }

    @NotNull
    public String getValueType()
    {
        return valueType;
    }

    @NotNull
    public String getMbPath() {
        return mbPath;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Mapping)) return false;

        Mapping mapping = (Mapping) o;

        if (!riverName.equals(mapping.riverName)) return false;
        if (!stationName.equals(mapping.stationName)) return false;
        if (!valueType.equals(mapping.valueType)) return false;
        return mbPath.equals(mapping.mbPath);
    }

    @Override
    public int hashCode()
    {
        int result = riverName.hashCode();
        result = 31 * result + stationName.hashCode();
        result = 31 * result + valueType.hashCode();
        result = 31 * result + mbPath.hashCode();
        return result;
    }
}
