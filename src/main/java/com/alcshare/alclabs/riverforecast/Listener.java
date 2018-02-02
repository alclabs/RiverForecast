package com.alcshare.alclabs.riverforecast;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Listener implements ServletContextListener
{
    private final Config config;
    private final RiverDataCache cache;
    private final RiverDataPoller poller;
    private final ValueUpdater updater;

    public Listener() {
        config = Config.getConfig();
        cache = new RiverDataCache();
        poller = new RiverDataPoller(config, cache);
        updater = new ValueUpdater(config, poller);
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute(Config.class.getName(), config);
        servletContextEvent.getServletContext().setAttribute(RiverDataCache.class.getName(), cache);
        servletContextEvent.getServletContext().setAttribute(RiverDataPoller.class.getName(), poller);
        updater.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        updater.stop();
    }
    
}
