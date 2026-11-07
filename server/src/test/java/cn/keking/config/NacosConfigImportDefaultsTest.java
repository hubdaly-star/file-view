package cn.keking.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the Nacos YAML dataId / file-extension defaults against a properties regression.
 */
class NacosConfigImportDefaultsTest {

    private static final Path CONFIG_DIR = Path.of("src/main/config");

    @Test
    void nacosProfileImportsPublishedYamlDataId() throws IOException {
        String yaml = Files.readString(CONFIG_DIR.resolve("application-nacos.yml"), StandardCharsets.UTF_8);
        assertTrue(yaml.contains("NACOS_CONFIG_DATA_ID:kkfileview.yml"),
                "nacos profile must import published dataId kkfileview.yml");
        assertFalse(yaml.contains("kkfileview.properties"),
                "nacos profile must not default to kkfileview.properties");
        assertTrue(yaml.contains("http://127.0.0.1:8848/nacos"));
        assertTrue(yaml.contains("startup.cmd -m standalone"));
    }

    @Test
    void localApplicationYmlDefaultsToYamlFileExtension() throws IOException {
        String yaml = Files.readString(CONFIG_DIR.resolve("application.yml"), StandardCharsets.UTF_8);
        assertTrue(yaml.contains("NACOS_CONFIG_FILE_EXTENSION:yaml"),
                "file-extension default must be yaml to match published Nacos type");
        assertFalse(yaml.contains("NACOS_CONFIG_FILE_EXTENSION:properties"),
                "file-extension must not default to properties");
        assertTrue(yaml.contains("nacos profile 下业务配置以 Nacos 为准"));
        assertTrue(yaml.contains("默认全部关闭"),
                "nacos stays off unless --spring.profiles.active=nacos");
    }
}
