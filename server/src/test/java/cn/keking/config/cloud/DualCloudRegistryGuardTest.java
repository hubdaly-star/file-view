package cn.keking.config.cloud;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DualCloudRegistryGuardTest {

    @Test
    void defaultBothOffDoesNotFail() {
        MockEnvironment environment = new MockEnvironment();
        assertDoesNotThrow(() -> DualCloudRegistryGuard.assertSingleRegistry(environment));
    }

    @Test
    void nacosOnlyDoesNotFail() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.discovery.enabled", "true");
        environment.setProperty("spring.cloud.servicecomb.discovery.enabled", "false");
        assertDoesNotThrow(() -> DualCloudRegistryGuard.assertSingleRegistry(environment));
    }

    @Test
    void cseOnlyDoesNotFail() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.discovery.enabled", "false");
        environment.setProperty("spring.cloud.servicecomb.discovery.enabled", "true");
        assertDoesNotThrow(() -> DualCloudRegistryGuard.assertSingleRegistry(environment));
    }

    @Test
    void bothEnabledFailsFast() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.discovery.enabled", "true");
        environment.setProperty("spring.cloud.servicecomb.discovery.enabled", "true");
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> DualCloudRegistryGuard.assertSingleRegistry(environment));
        assertTrue(ex.getMessage().contains("cannot both be enabled"));
        assertTrue(ex.getMessage().contains("nacos"));
        assertTrue(ex.getMessage().contains("cse"));
    }
}
