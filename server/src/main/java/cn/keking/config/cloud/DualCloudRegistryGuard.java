package cn.keking.config.cloud;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Fails fast before bean creation when Nacos discovery and Huawei CSE discovery
 * are both enabled. Spring Cloud allows only one {@code ServiceRegistry}.
 *
 * <p>Supported enablement is profile-based and mutually exclusive:
 * {@code --spring.profiles.active=nacos} or {@code --spring.profiles.active=cse}.
 */
public class DualCloudRegistryGuard implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static final String NACOS_DISCOVERY_ENABLED_KEY = "spring.cloud.nacos.discovery.enabled";
    static final String CSE_DISCOVERY_ENABLED_KEY = "spring.cloud.servicecomb.discovery.enabled";

    public static final String FAILURE_MESSAGE =
            "Nacos discovery and Huawei CSE discovery cannot both be enabled. "
                    + "Spring Cloud allows only one ServiceRegistry. "
                    + "Use --spring.profiles.active=nacos or --spring.profiles.active=cse (mutually exclusive).";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        assertSingleRegistry(applicationContext.getEnvironment());
    }

    public static void assertSingleRegistry(Environment environment) {
        boolean nacos = isEnabled(environment, NACOS_DISCOVERY_ENABLED_KEY);
        boolean cse = isEnabled(environment, CSE_DISCOVERY_ENABLED_KEY);
        if (nacos && cse) {
            throw new IllegalStateException(FAILURE_MESSAGE);
        }
    }

    static boolean isEnabled(Environment environment, String key) {
        return Boolean.TRUE.equals(environment.getProperty(key, Boolean.class, Boolean.FALSE));
    }
}
