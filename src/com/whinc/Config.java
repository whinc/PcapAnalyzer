package com.whinc;

import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2016/3/4.
 */
public class Config {
    public static final String CONFIG_FILE = "config.xml";
//    public static Locale DEFAULT_LOCALE = Locale.CHINESE;
    public static Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final long DEFAULT_TIMESTAMP = 0L;
    private static long timestamp = DEFAULT_TIMESTAMP;      // microsecond

    private Config(){}

    public static void setTimestamp(long v) {
        timestamp = v;
    }

    public static long getTimestamp() {
        return timestamp;
    }

    public static FXMLLoader createFXMLLoader(String fxml) {
        return new FXMLLoader(getResource(fxml), getStringResource());
    }

    public static ResourceBundle getStringResource() {
        return getStringResource(DEFAULT_LOCALE);
    }

    public static ResourceBundle getStringResource(Locale locale) {
        return ResourceBundle.getBundle("values/strings", locale);
    }

    public static String getString(String key) {
        return getString(key, DEFAULT_LOCALE);
    }

    public static String getString(String key, Locale locale) {
        return ResourceBundle.getBundle("values/strings", locale).getString(key);
    }

    public static URL getResource(String name) {
        return Config.class.getClassLoader().getResource(name);
    }
}
