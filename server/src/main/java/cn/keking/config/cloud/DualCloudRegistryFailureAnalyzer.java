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
                "Default start uses profile nacos. For CSE discovery use --spring.profiles.active=cse "
                        + "(it replaces nacos; Nacos stays the config center). Do not combine nacos and cse, "
                        + "and do not set both spring.cloud.nacos.discovery.enabled and "
                        + "spring.cloud.servicecomb.discovery.enabled to true. "
                        + "To turn Nacos off: --spring.profiles.active=none.",
                cause);
    }
}
