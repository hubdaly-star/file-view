package cn.keking.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void loadYamlPropertiesFlattensNestedKeysAndResolvesPlaceholders(@TempDir Path tempDir) throws Exception {
        Path yaml = tempDir.resolve("application.yml");
        Files.writeString(yaml, """
                spring:
                  cloud:
                    nacos:
                      server-addr: "${MISSING_KK_CLOUD_ENV:127.0.0.1:8848}"
                watermark:
                  txt: "${MISSING_KK_CLOUD_ENV:}"
                """, StandardCharsets.UTF_8);

        Properties properties = ConfigUtils.loadYamlProperties(yaml.toString());
        assertEquals("127.0.0.1:8848", properties.getProperty("spring.cloud.nacos.server-addr"));
        assertEquals("", properties.getProperty("watermark.txt"));
    }
}
