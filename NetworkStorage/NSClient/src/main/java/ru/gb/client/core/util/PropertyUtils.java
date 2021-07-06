package ru.gb.client.core.util;

import domain.allertsandexeption.ExceptionMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    private static final Properties properties = new Properties();

    public static String getProperties (String propertyName) {
        try (InputStream input = PropertyUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
            return properties.getProperty(propertyName);
        } catch (IOException e) {
            throw new IllegalArgumentException(ExceptionMessage.PROPFILE_ERROR);
        }
    }
}
