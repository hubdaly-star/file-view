package cn.keking.config.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * Turns on Spring Cloud discovery only when {@code spring.cloud.discovery.enabled=true}.
 *
 * <p>Supported path: {@code --spring.profiles.active=nacos} or {@code cse}
 * (mutually exclusive). Those profiles set the master switch plus the matching
 * client ({@code spring.cloud.nacos.discovery.*} or
 * {@code spring.cloud.servicecomb.discovery.*}). Default application.yml keeps
 * every flag false so preview still starts without a registry.
 *
 * <p>Flipping an env var such as {@code NACOS_DISCOVERY_ENABLED=true} without the
 * profile is not supported: Nacos {@code spring.config.import} lives only in
 * {@code application-nacos.yml}, and {@code register-enabled} defaults to false.
 */
@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(name = "spring.cloud.discovery.enabled", havingValue = "true")
public class CloudDiscoveryConfiguration {
}
