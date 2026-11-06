package cn.keking.config.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * Turns on Spring Cloud discovery only when an operator explicitly enables it.
 * Nacos and CSE auto-config stay off by default via {@code *.enabled=false}
 * so kkFileView preview still starts without a registry.
 */
@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty(name = "spring.cloud.discovery.enabled", havingValue = "true")
public class CloudDiscoveryConfiguration {
}
