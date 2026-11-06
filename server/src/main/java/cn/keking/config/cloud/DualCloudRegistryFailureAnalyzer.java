package cn.keking.config.cloud;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Maps {@link DualCloudRegistryGuard} startup failures to an actionable Boot analysis.
 */
public class DualCloudRegistryFailureAnalyzer extends AbstractFailureAnalyzer<IllegalStateException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, IllegalStateException cause) {
        if (cause.getMessage() == null || !cause.getMessage().contains("Nacos discovery and Huawei CSE discovery")) {
            return null;
        }
        return new FailureAnalysis(
                cause.getMessage(),
                "Activate exactly one cloud profile: --spring.profiles.active=nacos "
                        + "or --spring.profiles.active=cse. Do not combine them, and do not set both "
                        + "spring.cloud.nacos.discovery.enabled and spring.cloud.servicecomb.discovery.enabled to true.",
                cause);
    }
}
