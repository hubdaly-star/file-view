package cn.keking.config.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Logs which cloud registry integrations are active. Spring Cloud allows only
 * one {@code ServiceRegistry} implementation, so enabling Nacos discovery and
 * CSE discovery together is warned against.
 */
@Component
public class CloudRegistryStartupLogger implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudRegistryStartupLogger.class);

    @Value("${spring.cloud.discovery.enabled:false}")
    private boolean discoveryEnabled;

    @Value("${spring.cloud.nacos.config.enabled:false}")
    private boolean nacosConfigEnabled;

    @Value("${spring.cloud.nacos.discovery.enabled:false}")
    private boolean nacosDiscoveryEnabled;

    @Value("${spring.cloud.servicecomb.discovery.enabled:false}")
    private boolean cseDiscoveryEnabled;

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info(
                "Cloud integrations: discovery.enabled={}, nacos.config={}, nacos.discovery={}, cse.discovery={}",
                discoveryEnabled, nacosConfigEnabled, nacosDiscoveryEnabled, cseDiscoveryEnabled);
        if (nacosDiscoveryEnabled && cseDiscoveryEnabled) {
            LOGGER.warn("Both Nacos discovery and Huawei CSE discovery are enabled. "
                    + "Spring Cloud supports only one ServiceRegistry; keep a single registry for registration.");
        }
    }
}
