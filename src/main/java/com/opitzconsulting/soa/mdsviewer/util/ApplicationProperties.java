package com.opitzconsulting.soa.mdsviewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    public static final String VSHTTP_IN_DB_PROPERTIES = "mds_db.properties";

    public static Properties getProperties() {
        InputStream in = null;
        try {
            Properties properties = new Properties();
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(VSHTTP_IN_DB_PROPERTIES);
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String getProperty(String key) {
        return getProperties().getProperty(key);
    }
}
