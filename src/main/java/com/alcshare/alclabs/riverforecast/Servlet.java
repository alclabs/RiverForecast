package com.alcshare.alclabs.riverforecast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Servlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = getRequestURI(req);
        switch(requestURI) {
            case "/config":
                RequestDispatcher dispatcher = req.getRequestDispatcher("html/index.html");
                dispatcher.forward(req, resp);
                break;
            case "/config/getMappings":
                getMappings(req, resp);
                break;
            case "/config/getRivers":
                getRivers(req, resp);
                break;
            case "/config/getStations":
                getStations(req, resp);
                break;
            case "/config/getValueTypes":
                getValueTypes(req, resp);
                break;
            default:
                super.doGet(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = getRequestURI(req);
        switch(requestURI)
        {
            case "/config/addMapping":
                updateMapping(req, resp, true);
                break;
            case "/config/removeMapping":
                updateMapping(req, resp, false);
                break;
            default:
                super.doPost(req, resp);
        }
    }

    private String getRequestURI(HttpServletRequest req) {
        String contextPath = getServletContext().getContextPath();
        String requestURI = req.getRequestURI();

        if (requestURI.startsWith(contextPath))
            return requestURI.substring(contextPath.length());

        return requestURI;
    }

    private void getRivers(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        RiverDataCache cache = getRiverDataCache(req.getServletContext());
        JSONArray riverNames = cache.getRiverNames();
        reply(resp, riverNames);
    }

    private void getStations(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String riverName = req.getParameter("riverName");
        RiverDataCache cache = getRiverDataCache(req.getServletContext());
        JSONArray stationNames = cache.getStationNames(riverName);
        reply(resp, stationNames);
    }

    @NotNull
    private RiverDataCache getRiverDataCache(ServletContext context) throws ServletException {
        Object attribute = context.getAttribute(RiverDataCache.class.getName());
        if(attribute instanceof RiverDataCache) {
            return (RiverDataCache) attribute;
        }
        throw new ServletException("Could not find RiverDataCache in servlet context");
    }

    private void getValueTypes(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try
        {
            JSONArray valueTypes = ValueType.valuesAsJsonArray();
            reply(resp, valueTypes);
        } catch (JSONException e)
        {
            throw new ServletException("Could not retrieve value types.", e);
        }
    }

    private void getMappings(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Config config = getConfig(req.getServletContext());
        JSONArray mappings;
        try {
            mappings = config.getMappings();
        } catch (JSONException e) {
            throw new ServletException("Error getting mappings", e);
        }
        reply(resp, mappings);
    }

    private void updateMapping(HttpServletRequest req, HttpServletResponse resp, boolean add) throws IOException, ServletException {
        boolean success = false;
        String message = null;
        String riverName = req.getParameter("riverName");
        String stationName = req.getParameter("stationName");
        String valueType = req.getParameter("valueType");
        String mbPath = req.getParameter("mbPath");

        if(riverName == null || stationName == null || valueType == null || mbPath == null)
        {
            message = "Mapping update request contained incomplete parameters.";
        }
        else
        {
            Config config = getConfig(req.getServletContext());
            try
            {
                JsonHelper jsonHelper = new JsonHelper();
                List<Mapping> mappings = jsonHelper.toMappings(config.getMappings());

                Mapping mapping = new Mapping(riverName, stationName, valueType, mbPath);
                if(add) mappings.add(mapping);
                else    mappings.remove(mapping);
                config.setMappings(jsonHelper.toJson(mappings));
                success = true;
            } catch (JSONException e)
            {
                message = "Error updating mapping: " + e.getMessage();
            }
        }
        reply(resp, success, message);
    }

    @NotNull
    private Config getConfig(ServletContext context) throws ServletException {
        Object attribute = context.getAttribute(Config.class.getName());
        if(attribute instanceof Config) {
            return (Config) attribute;
        }
        throw new ServletException("Could not find Config in servlet context");
    }

    private void reply(HttpServletResponse resp, JSONArray reply) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.print(reply);
        writer.flush();
    }

    private void reply(HttpServletResponse resp, boolean success, @Nullable String message) throws IOException, ServletException {
        JSONObject ack = new JSONObject();
        try
        {
            ack.put("success", success);
            if(message != null)
                ack.put("message", message);
        } catch (JSONException e)
        {
            throw new ServletException("Could not formulate response to post request: " + e.getMessage());
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.print(ack);
        writer.flush();
    }
}
