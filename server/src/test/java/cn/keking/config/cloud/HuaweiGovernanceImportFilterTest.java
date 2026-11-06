package cn.keking.config.cloud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuaweiGovernanceImportFilterTest {

    @Test
    void keepsCseDiscoveryAutoConfig() {
        assertTrue(HuaweiGovernanceImportFilter.shouldKeep(
                "com.huaweicloud.servicecomb.discovery.DiscoveryAutoConfiguration"));
        assertTrue(HuaweiGovernanceImportFilter.shouldKeep(
                "com.huaweicloud.servicecomb.discovery.registry.ServiceCombRegistryAutoConfiguration"));
    }

    @Test
    void dropsGovernanceAndRouterAutoConfig() {
        assertFalse(HuaweiGovernanceImportFilter.shouldKeep(
                "com.huaweicloud.governance.adapters.webmvc.WebMvcConfiguration"));
        assertFalse(HuaweiGovernanceImportFilter.shouldKeep(
                "com.huaweicloud.governance.GovernanceConfiguration"));
        assertFalse(HuaweiGovernanceImportFilter.shouldKeep(
                "com.huaweicloud.router.client.RouterClientAutoConfiguration"));
        assertFalse(HuaweiGovernanceImportFilter.shouldKeep(
                "com.huaweicloud.router.client.loadbalancer.webmvc.WebMvcConfiguration"));
    }

    @Test
    void keepsUnrelatedAndNullEntries() {
        assertTrue(HuaweiGovernanceImportFilter.shouldKeep("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration"));
        assertTrue(HuaweiGovernanceImportFilter.shouldKeep(null));
        assertTrue(HuaweiGovernanceImportFilter.shouldKeep(""));
    }

    @Test
    void matchMasksGovernanceEntries() {
        HuaweiGovernanceImportFilter filter = new HuaweiGovernanceImportFilter();
        String[] classes = {
                "com.huaweicloud.servicecomb.discovery.DiscoveryAutoConfiguration",
                "com.huaweicloud.governance.adapters.webmvc.WebMvcConfiguration",
                "com.huaweicloud.router.client.RouterClientAutoConfiguration"
        };
        boolean[] matches = filter.match(classes, null);
        assertTrue(matches[0]);
        assertFalse(matches[1]);
        assertFalse(matches[2]);
    }
}
