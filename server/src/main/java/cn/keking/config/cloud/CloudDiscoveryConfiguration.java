package cn.keking.config.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * Turns on Spring Cloud discovery only when {@code spring.cloud.discovery.enabled=true}.
 *
 * <p>Default start activates profile {@code nacos} (Nacos config + Nacos discovery).
 * Override with {@code --spring.profiles.active=cse} (CSE discovery; Nacos remains
 * the config center) or {@code none} (no registry). {@code nacos} and {@code cse}
 * are mutually exclusive for discovery: combining them fail-fasts because Spring
 * Cloud allows only one {@code ServiceRegistry}.
 *
 * <p>Preview/business keys are not in local {@code application.yml}; they come from
 * Nacos {@code kkfileview.yml} when Nacos config is on, otherwise from
 * {@code ConfigConstants} defaults.
 *
 * <p>Flipping an env var such as {@code NACOS_DISCOVERY_ENABLED=true} without the
 * profile is not supported: Nacos {@code spring.config.import} lives in
 * {@code application-nacos.yml} / {@code application-cse.yml}, and
 * {@code register-enabled} defaults to false outside the {@code nacos} profile.
 */
@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(name = "spring.cloud.discovery.enabled", havingValue = "true")
public class CloudDiscoveryConfiguration {
}
