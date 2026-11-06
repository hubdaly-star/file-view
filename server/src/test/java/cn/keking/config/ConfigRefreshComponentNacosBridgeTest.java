package cn.keking.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigRefreshComponentNacosBridgeTest {

    @Test
    void nacosConfigOffUsesLocalFilePath() {
        MockEnvironment environment = new MockEnvironment();
        ConfigRefreshComponent component = new ConfigRefreshComponent(environment);
        assertFalse(component.isNacosConfigEnabled());
    }

    @Test
    void nacosConfigOnReadsFromEnvironment() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.config.enabled", "true");
        environment.setProperty("cache.enabled", "false");
        environment.setProperty("watermark.txt", "from-nacos");
        ConfigRefreshComponent component = new ConfigRefreshComponent(environment);
        assertTrue(component.isNacosConfigEnabled());

        ConfigRefreshComponent.EnvironmentBackedProperties properties =
                new ConfigRefreshComponent.EnvironmentBackedProperties(environment);
        assertEquals("false", properties.getProperty("cache.enabled"));
        assertEquals("from-nacos", properties.getProperty("watermark.txt"));
        assertEquals("fallback", properties.getProperty("missing.key", "fallback"));
    }
}
