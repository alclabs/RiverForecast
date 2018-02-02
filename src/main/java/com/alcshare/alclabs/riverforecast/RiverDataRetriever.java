package com.alcshare.alclabs.riverforecast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RiverDataRetriever
{
    public List<RiverData> retrieveFrom(String url) throws RiverDataException {
        try {
            RiverDataParser parser = new RiverDataParser();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); // This may not be strictly required as DTDs shouldn't be allowed at all, per previous line.
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            reader.setContentHandler(parser);
            reader.parse(new InputSource(new URL(url).openStream()));
            return parser.getRiverData();
        } catch (SAXException|IOException e) {
            throw new RiverDataException("Error retrieving data from remote host", e);
        }
    }
}
