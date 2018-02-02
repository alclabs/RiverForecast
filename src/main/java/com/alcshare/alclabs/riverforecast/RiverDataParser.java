package com.alcshare.alclabs.riverforecast;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

class RiverDataParser extends DefaultHandler {
    private final List<RiverData> riverData = new ArrayList<>();

    @NotNull
    public List<RiverData> getRiverData() {
        return Collections.unmodifiableList(riverData);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(RiverData rd : riverData) {
            stringBuilder.append(rd.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equalsIgnoreCase("riverfcst")) {
            String stationName = attributes.getValue("stationName");
            String riverName = attributes.getValue("riverName");
            if(stationName != null && riverName != null) {
                FlowLevel forecast = toFlowLevel(
                        attributes.getValue("fcstDate"),
                        attributes.getValue("fcstFeetFlow") );
                FlowLevel observed = toFlowLevel(
                        null, // present (retrieval) time of last observation
                        attributes.getValue("obsFeetFlow") );
                CriticalLevels criticalLevels = toCriticalFlowLevels(
                        attributes.getValue("printCrit") );
                riverData.add(new RiverData(stationName, riverName, forecast, observed, criticalLevels));
            }
        }
    }
    private FlowLevel toFlowLevel(String date, String feetFlow) {
        Calendar calendar = toCalendar(date);
        // feetFlow is in a form like:
        // "8.0 Ft / 9 cfs"
        Float level = null;
        Float flow = null;
        if(feetFlow != null) {
            String[] strings = feetFlow.split(" ");
            for (String s : strings) {
                try {
                    Float n = Float.valueOf(s);
                    if (level == null) level = n;
                    else {
                        flow = n;
                        break;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return new FlowLevel(calendar, level, flow);
    }
    private Calendar toCalendar(String date) {
        // Date is in a form like:
        // "06/23 at 1409 UTC"
        Integer month = null;
        Integer day = null;
        Integer time = null;
        if(date != null) {
            String[] strings = date.split("[/ ]");
            for (String s : strings) {
                try {
                    Integer n = Integer.valueOf(s);
                    if (month == null) month = (n > 0) ? n - 1 : 0;
                    else if (day == null) day = n;
                    else {
                        time = n;
                        break;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        Calendar calendar = Calendar.getInstance();
        // If the string parsed correctly, apply those values.
        // TODO: Fix this for the year transition.
        // TODO: Fix for time zone.
        if(month != null && day != null && time != null) {
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, time / 100);
            calendar.set(Calendar.MINUTE, time % 100);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar;
    }
    private CriticalLevels toCriticalFlowLevels(String critical) {
        // String is in the form:
        // "Danger: 59.0 Ft/ Flood: 38.0 Ft/ Monitor: 30.0 Ft"
        Float danger = null;
        Float flood = null;
        Float monitor = null;
        if(critical != null) {
            String[] strings = critical.split("/");
            for (String s : strings) {
                String[] ss = s.split(" ");
                if(ss.length >= 2) {
                    try {
                        Float n = Float.valueOf(ss[1]);
                        if(ss[0].equalsIgnoreCase("danger:")) danger = n;
                        if(ss[0].equalsIgnoreCase("flood:")) flood = n;
                        if(ss[0].equalsIgnoreCase("monitor:")) monitor = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return new CriticalLevels(danger, flood, monitor);
    }
}
