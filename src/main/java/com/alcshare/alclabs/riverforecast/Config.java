package com.alcshare.alclabs.riverforecast;

import com.controlj.green.addonsupport.access.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Properties;

public final class Config {
    private static final String STORE_NAME = "riverforecast";
    private static final String PROP_URL = "url";
    private static final String PROP_MAPPINGS = "mappings";
    private static final String DEFAULT_URL = "http://www.cnrfc.noaa.gov/data/kml/riverFcst.xml";
    private static final Config instance = Config.load();

    private final Properties props;

    private Config(Properties props) { this.props = props; }

    @NotNull
    public static Config getConfig() { return instance; }

    @NotNull
    private static Config load()
    {
        SystemConnection connection = DirectAccess.getDirectAccess().getRootSystemConnection();
        Properties props = null;
        try
        {
            props = connection.runReadAction(new ReadActionResult<Properties>()
            {
                public Properties execute(@NotNull SystemAccess access) throws Exception
                {
                    DataStore store = access.getSystemDataStore(STORE_NAME);
                    Properties props = new Properties(getDefaultProperties());
                    try {
                        props.load(store.getReader());
                    } catch (IOException ignored) {}
                    return props;
                }
            });
        } catch (Exception e) {
            System.out.println("RiverForecast: Error loading configuration.");
        }
        return new Config(props);
    }

    private void save() throws IOException
    {
        SystemConnection connection = DirectAccess.getDirectAccess().getRootSystemConnection();
        try
        {
            connection.runWriteAction("Saving River Forecast configuration", new WriteAction() {
                public void execute(@NotNull WritableSystemAccess access) throws Exception
                {
                    DataStore store = access.getSystemDataStore(STORE_NAME);
                    props.store(store.getWriter(), null);
                }
            });
        } catch (Exception e)  {
            System.out.println("RiverForecast: Error saving configuration.");
            throw new IOException("Error saving configuration", e);
        }
    }

    @NotNull
    public String getUrl() {
        synchronized (props)
        {
            return props.getProperty(PROP_URL);
        }
    }
    public void setUrl(@NotNull String url) throws IOException {
        synchronized (props)
        {
            props.setProperty(PROP_URL, url);
            save();
        }
    }

    @NotNull
    public JSONArray getMappings() throws JSONException {
        synchronized (props)
        {
            String mappings = props.getProperty(PROP_MAPPINGS);
            return new JSONArray(mappings);
        }
    }
    public void setMappings(@NotNull JSONArray mappings) throws IOException {
        synchronized (props)
        {
            props.setProperty(PROP_MAPPINGS, mappings.toString());
            save();
        }
    }

    private static Properties getDefaultProperties() {
        Properties result = new Properties();
        result.setProperty(PROP_URL, DEFAULT_URL);
        result.setProperty(PROP_MAPPINGS, "[]"); // empty jsonarray
        return result;
    }
}
