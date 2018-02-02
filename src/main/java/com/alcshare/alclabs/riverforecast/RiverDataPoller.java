package com.alcshare.alclabs.riverforecast;

import java.util.List;

public class RiverDataPoller
{
    private final Config config;
    private final RiverDataCache cache;

    public RiverDataPoller(Config config, RiverDataCache cache) {
        this.config = config;
        this.cache = cache;
    }

    public RiverDataCache poll() throws RiverDataException
    {
        RiverDataRetriever retriever = new RiverDataRetriever();
        List<RiverData> riverData = retriever.retrieveFrom(config.getUrl());
        cache.setRiverData(riverData);
        return cache;
    }
}
