package konrad.tools.tomcat.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Stworzone przez Konrad Botor dnia 2016-07-06.
 */
public class ConfigUtil {
    private static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static Map<String, String> configuration;
    private static ConfigUtil configUtil;

    @Override
    public String toString() {
        if (configuration != null && configuration.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String key: configuration.keySet()) {
                stringBuilder.append(key);
                stringBuilder.append(": ");
                stringBuilder.append(configuration.get(key));
                stringBuilder.append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("\n"));
            return stringBuilder.toString();
        } else {
            return super.toString();
        }
    }

    private ConfigUtil() {
        logger.info("init");
        configuration = new TreeMap<>();
        configuration.putAll(System.getenv());
        Properties systemProperties = System.getProperties();
        addProperties(systemProperties);
        Properties customProperties = new Properties();
        String configFile = getProperty("config.file", "config.properties");
        try {
            FileInputStream fileInputStream = new FileInputStream(configFile);
            customProperties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);
            if (inputStream != null) {
                try {
                    customProperties.load(inputStream);
                } catch (IOException e1) {
                    logger.error("Error opening config file: {} as resource", configFile, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error opening config file: {}", configFile, e);
        }
        addProperties(customProperties);
    }

    public static ConfigUtil getInstance() {
        logger.info("getInstance");
        if (configUtil == null) {
            configUtil = new ConfigUtil();
        }
        return configUtil;
    }

    public String getProperty(String key) {
        logger.debug("getProperty({})", key);
        return configuration.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        logger.debug("getProperty({}, {})", key, defaultValue);
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private void addProperties(Properties properties) {
        for (Object property: properties.keySet()) {
            configuration.put(property.toString(), properties.getProperty(property.toString()));
        }
    }

    public Set<Map.Entry<String, String>> getProperties() {
        return configuration.entrySet();
    }

    public Set<String> getPropertyKeys() {
        return configuration.keySet();
    }

}
