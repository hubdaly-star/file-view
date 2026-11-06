package cn.keking.config.cloud;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

/**
 * Drops Huawei governance / router auto-configuration so kkFileView preview HTTP
 * is not rate-limited or rewritten.
 *
 * <p>The discovery starter transitively brings {@code spring-cloud-huawei-governance}
 * and {@code spring-cloud-huawei-router}. Maven-excluding those jars is unsafe:
 * {@code DiscoveryAutoConfiguration} beans implement router
 * {@code CanaryFilterAdapter} / {@code ZoneAwareFilterAdapter} and governance
 * {@code AuthenticationAdapter}. Filtering auto-config keeps CSE
 * discovery/registration working while skipping servlet filters and LB rewrites.
 */
public class HuaweiGovernanceImportFilter implements AutoConfigurationImportFilter {

    static boolean shouldKeep(String className) {
        if (className == null || className.isEmpty()) {
            return true;
        }
        return !className.startsWith("com.huaweicloud.governance.")
                && !className.startsWith("com.huaweicloud.router.");
    }

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            matches[i] = shouldKeep(autoConfigurationClasses[i]);
        }
        return matches;
    }
}
