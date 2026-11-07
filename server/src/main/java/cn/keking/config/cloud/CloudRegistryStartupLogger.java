package cn.keking.config.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Logs which cloud registry integrations are active after a successful start.
 * Dual Nacos + CSE discovery is rejected earlier by {@link DualCloudRegistryGuard}.
 *
 * <p>Default profile is {@code nacos}. Override with {@code --spring.profiles.active=cse}
 * (mutually exclusive discovery) or {@code none} to turn Nacos off.
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

    @Value("${spring.cloud.nacos.discovery.register-enabled:false}")
    private boolean nacosRegisterEnabled;

    @Value("${spring.cloud.servicecomb.discovery.enabled:false}")
    private boolean cseDiscoveryEnabled;

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info(
                "Cloud integrations: discovery.enabled={}, nacos.config={}, nacos.discovery={}, "
                        + "nacos.register-enabled={}, cse.discovery={}",
                discoveryEnabled, nacosConfigEnabled, nacosDiscoveryEnabled,
                nacosRegisterEnabled, cseDiscoveryEnabled);
        if (nacosDiscoveryEnabled) {
            LOGGER.info("Nacos discovery is on (profile nacos). CSE discovery stays off.");
        } else if (cseDiscoveryEnabled) {
            LOGGER.info("Huawei CSE discovery is on (profile cse). Nacos discovery stays off.");
        }
    }
}
