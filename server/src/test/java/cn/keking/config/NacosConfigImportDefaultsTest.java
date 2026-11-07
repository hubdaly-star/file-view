package cn.keking.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.regex.Pattern;

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
        assertTrue(yaml.contains("namespace showName：dev"),
                "comments must distinguish Nacos showName dev from the namespace ID");
        assertTrue(yaml.contains("4529b5c0-a7c0-4891-b735-a1c7d0bc0a8e"),
                "nacos profile comments must record the local dev namespace ID");
        assertTrue(yaml.contains("ConfigConstants"),
                "nacos profile must document ConfigConstants defaults when the profile is off");
        assertFalse(yaml.contains("完整本地回退"),
                "local application.yml is no longer a full business-config fallback");
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
        assertTrue(yaml.contains("NACOS_NAMESPACE:4529b5c0-a7c0-4891-b735-a1c7d0bc0a8e"),
                "config and discovery must default to the local Nacos dev namespace ID");
        assertFalse(yaml.contains("NACOS_NAMESPACE:}"),
                "namespace default must not be empty public");
        assertTrue(yaml.contains("showName=dev"),
                "comments must mention namespace showName dev versus ID");
        assertTrue(yaml.contains("ConfigConstants"),
                "without nacos profile, comments must point at ConfigConstants defaults");
        assertFalse(yaml.contains("完整本地回退"),
                "local application.yml is no longer a full business-config fallback");
        assertFalse(yaml.contains("spring.profiles.active: nacos"),
                "nacos must not be enabled by default");
    }

    @Test
    void localApplicationYmlOmitsBusinessPreviewSettings() throws IOException {
        String yaml = Files.readString(CONFIG_DIR.resolve("application.yml"), StandardCharsets.UTF_8);
        for (String key : new String[]{
                "office", "cad", "pdf", "pdf2jpg", "tif", "media", "convertMedias",
                "file", "local", "cache", "base", "trust", "prohibit", "watermark",
                "ftp", "beian", "home", "delete", "aes", "basic", "useragent", "simText",
                "kk"
        }) {
            Pattern mapping = Pattern.compile("(?m)^" + Pattern.quote(key) + ":");
            assertFalse(mapping.matcher(yaml).find(),
                    "application.yml must not duplicate Nacos business key '" + key + ":'");
        }
        assertFalse(Pattern.compile("(?m)^[ \\t]*redisson:").matcher(yaml).find(),
                "spring.redisson belongs in Nacos; RedissonConfig only loads when cache.type=redis");
        assertTrue(yaml.contains("SPRING_APPLICATION_NAME:kkfileview"),
                "boot identity must remain");
        assertTrue(yaml.contains("KK_SERVER_PORT:8012"));
        assertTrue(yaml.contains("template-loader-path"));
        assertTrue(yaml.contains("kkfileview.yml"),
                "comments must point at Nacos dataId kkfileview.yml in namespace dev");
    }
}
