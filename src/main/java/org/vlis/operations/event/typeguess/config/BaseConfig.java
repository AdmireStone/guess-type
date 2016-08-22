package org.vlis.operations.event.typeguess.config;

import java.util.Map;

public class BaseConfig {
    public static Object getProperty(Map<String, Object> props, String key, Object defaultVal) {
        Object propVal = props.get(key);
        if(propVal == null) {
            return defaultVal;
        }
        
        if(propVal instanceof String) {
            return ((String)propVal).trim();
        }
        return propVal;
    }

    public static String getStringProperty(Map<String, Object> props, String key, String defaultVal) {
        String propVal = (String)props.get(key);
        if(propVal == null) {
            return defaultVal;
        }
        
        return propVal.trim();
    }
    
    public static int getIntProperty(Map<String, Object> props, String key, int defaultVal) {
        Number val = (Number) props.get(key);
        if (val == null) {
            return defaultVal;
        }

        return val.intValue();
    }
    
    public static boolean getBooleanProperty(Map<String, Object> props, String key, Boolean defaultVal) {
        Boolean val = (Boolean) props.get(key);
        if (val == null) {
            return defaultVal;
        }

        return val.booleanValue();
    }
}
