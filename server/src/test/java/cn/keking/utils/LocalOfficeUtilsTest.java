package cn.keking.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalOfficeUtilsTest {

    @Test
    void nacosEnvironmentOfficeHomeWins() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.config.enabled", "true");
        environment.setProperty("office.home",
                "C:\\Users\\daly\\Desktop\\workspace\\java\\LibreOfficePortable\\App\\libreoffice");

        assertEquals(
                "C:\\Users\\daly\\Desktop\\workspace\\java\\LibreOfficePortable\\App\\libreoffice",
                LocalOfficeUtils.resolveOfficeHome(environment));
        assertEquals(
                new File("C:\\Users\\daly\\Desktop\\workspace\\java\\LibreOfficePortable\\App\\libreoffice"),
                LocalOfficeUtils.getDefaultOfficeHome(environment));
    }

    @Test
    void nacosEnabledSkipsUnconfiguredLocalFile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.config.enabled", "true");
        environment.setProperty("office.home", "default");

        assertFalse(LocalOfficeUtils.isConfigured(LocalOfficeUtils.resolveOfficeHome(environment)));
    }

    @Test
    void nacosEnabledBlankOfficeHomeDoesNotUseDefaultSentinelAsPath() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.config.enabled", "true");

        String resolved = LocalOfficeUtils.resolveOfficeHome(environment);
        assertFalse(LocalOfficeUtils.isConfigured(resolved));
    }

    @Test
    void withoutNacosFallsBackToLocalYamlReader() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.cloud.nacos.config.enabled", "false");

        String fromLocal = LocalOfficeUtils.readOfficeHomeFromLocalYaml();
        assertEquals(fromLocal, LocalOfficeUtils.resolveOfficeHome(environment));
    }

    @Test
    void localApplicationYmlDoesNotCarryOfficeHome() {
        String fromLocal = LocalOfficeUtils.readOfficeHomeFromLocalYaml();
        assertFalse(LocalOfficeUtils.isConfigured(fromLocal),
                "office.home belongs in Nacos kkfileview.yml, not local application.yml");
    }

    @Test
    void defaultAndBlankAreNotConfiguredPaths() {
        assertFalse(LocalOfficeUtils.isConfigured(null));
        assertFalse(LocalOfficeUtils.isConfigured(""));
        assertFalse(LocalOfficeUtils.isConfigured("  "));
        assertFalse(LocalOfficeUtils.isConfigured("default"));
        assertFalse(LocalOfficeUtils.isConfigured("DEFAULT"));
        assertTrue(LocalOfficeUtils.isConfigured("/opt/libreoffice"));
    }

    @Test
    void nullEnvironmentUsesLocalYaml() {
        assertEquals(LocalOfficeUtils.readOfficeHomeFromLocalYaml(),
                LocalOfficeUtils.resolveOfficeHome(null));
    }
}
