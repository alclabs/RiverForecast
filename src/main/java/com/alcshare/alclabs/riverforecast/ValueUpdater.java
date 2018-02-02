package com.alcshare.alclabs.riverforecast;

import com.controlj.green.addonsupport.access.*;
import com.controlj.green.addonsupport.access.aspect.PresentValue;
import com.controlj.green.addonsupport.access.value.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ValueUpdater implements Runnable
{
    private static final long INTERVAL_IN_MINUTES = 60;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final AtomicReference<Future> futureRef = new AtomicReference<>();
    private final Config config;
    private final RiverDataPoller poller;

    public ValueUpdater(Config config, RiverDataPoller poller)
    {
        this.config = config;
        this.poller = poller;
    }

    @Override
    public void run()
    {
        try
        {
            RiverDataCache cache = poller.poll();
            updateMappedPoints(cache.getRiverData());
        } catch (RiverDataException e)
        {
            System.out.println("RiverForecast: Value updater attempt failed. " + e.getMessage());
        }
    }

    private void updateMappedPoints(@NotNull List<RiverData> riverDataList) throws RiverDataException {
        List<Mapping> mappings;
        try
        {
            mappings = new JsonHelper().toMappings(config.getMappings());
        } catch (JSONException e)
        {
            throw new RiverDataException("Could not retrieve mappings.");
        }
        for(Mapping mapping : mappings) {
            Float valueToWrite = getValueToWrite(mapping.getStationName(), mapping.getValueType(), riverDataList);
            if(valueToWrite != null)
                writeMappedPoint(mapping.getMbPath(), valueToWrite);
        }
    }

    @Nullable
    private Float getValueToWrite(@NotNull String stationName, @NotNull String valueTypeName, @NotNull List<RiverData> riverDataList) {
        ValueType valueType;
        try
        {
            valueType = ValueType.valueOf(valueTypeName);
        } catch (IllegalArgumentException e)
        {
            System.out.println("RiverForecast: Could not identify value to write '" + valueTypeName + "'.");
            return null;
        }
        for(RiverData data : riverDataList) {
            if(data.getStationName().equalsIgnoreCase(stationName)) {
                switch(valueType)
                {
                    case FORECAST_FLOW:   return data.getForecast().getFlow();
                    case FORECAST_LEVEL:  return data.getForecast().getLevel();
                    case OBSERVED_FLOW:   return data.getObserved().getFlow();
                    case OBSERVED_LEVEL:  return data.getObserved().getLevel();
                    case FLOOD_LEVEL:     return data.getCriticalLevels().getFloodLevel();
                    case DANGER_LEVEL:    return data.getCriticalLevels().getDangerLevel();
                    case MONITOR_LEVEL:   return data.getCriticalLevels().getMonitorLevel();
                    default:              return null;
                }
            }
        }
        return null;
    }

    private void writeMappedPoint(@NotNull String mbPath, @NotNull Float valueToWrite) {
        SystemConnection connection = DirectAccess.getDirectAccess().getRootSystemConnection();
        try
        {
            connection.runWriteAction(FieldAccessFactory.newFieldAccess(), "River Forecast write",  (systemAccess) -> {
                Tree geoTree = systemAccess.getTree(SystemTree.Geographic);
                Location location = systemAccess.getGeoRoot().toNode().evalToNode(mbPath).resolveToLocation(geoTree);
                PresentValue presentValue = location.getAspect(PresentValue.class);
                Value value = presentValue.getValue();
                setValue(value, valueToWrite);
            });
        } catch (ActionExecutionException|SystemException|WriteAbortedException e)
        {
            System.out.println("RiverForecast: Could not update '" + mbPath + "'. " + e.getMessage());;
        }
    }

    private void setValue(Value value, Float update) throws InvalidValueException
    {
        if (value instanceof FloatValue) {
            FloatValue floatValue = (FloatValue) value;
            floatValue.set(update);
        } else if (value instanceof IntValue) {
            IntValue intValue = (IntValue) value;
            intValue.set(Math.round(update));
        } else if (value instanceof BoolValue) {
            BoolValue boolValue = (BoolValue) value;
            boolValue.set((update > 0.0) || (update < 0.0));
        }
    }


    public void start()
    {
        futureRef.set(executor.scheduleAtFixedRate(this, 0, INTERVAL_IN_MINUTES, TimeUnit.MINUTES));
    }

    public void stop()
    {
        try {
            Future future = futureRef.get();
            if (future != null) {
                future.cancel(true);
            }
            executor.shutdownNow();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

}
