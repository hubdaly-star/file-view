package cn.keking.utils;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConfigUtilsRestorePropertiesTest {

    @Test
    void placeholderWithoutColonDoesNotThrowWhenEnvMissing() {
        Properties properties = new Properties();
        properties.setProperty("ref", "${MISSING_KK_CLOUD_ENV}");
        assertDoesNotThrow(() -> ConfigUtils.restorePropertiesFromEnvFormat(properties));
        assertEquals("${MISSING_KK_CLOUD_ENV}", properties.getProperty("ref"));
    }

    @Test
    void usesDefaultWhenEnvMissing() {
        Properties properties = new Properties();
        properties.setProperty("addr", "${MISSING_KK_CLOUD_ENV:127.0.0.1:8848}");
        ConfigUtils.restorePropertiesFromEnvFormat(properties);
        assertEquals("127.0.0.1:8848", properties.getProperty("addr"));
    }

    @Test
    void emptyDefaultIsAllowed() {
        Properties properties = new Properties();
        properties.setProperty("user", "${MISSING_KK_CLOUD_ENV:}");
        ConfigUtils.restorePropertiesFromEnvFormat(properties);
        assertEquals("", properties.getProperty("user"));
    }
}
