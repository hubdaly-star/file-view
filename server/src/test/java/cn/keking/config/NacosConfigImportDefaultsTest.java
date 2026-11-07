package cn.keking.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the Nacos YAML dataId / file-extension defaults against a properties regression,
 * and the default-nacos-profile start path against a "must pass --spring.profiles.active" regression.
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
        assertTrue(yaml.contains("Default profile") || yaml.contains("默认 profile"),
                "nacos profile comments must say this is the default start path");
        assertTrue(yaml.contains("spring.profiles.active=none"),
                "nacos profile must document how to turn Nacos off");
        assertFalse(yaml.contains("完整本地回退"),
                "local application.yml is no longer a full business-config fallback");
    }

    @Test
    void localApplicationYmlDefaultsToNacosProfileAndYamlFileExtension() throws IOException {
        String yaml = Files.readString(CONFIG_DIR.resolve("application.yml"), StandardCharsets.UTF_8);
        assertTrue(yaml.contains("NACOS_CONFIG_FILE_EXTENSION:yaml"),
                "file-extension default must be yaml to match published Nacos type");
        assertFalse(yaml.contains("NACOS_CONFIG_FILE_EXTENSION:properties"),
                "file-extension must not default to properties");
        assertTrue(yaml.contains("nacos profile 下业务配置以 Nacos 为准"));
        assertTrue(Pattern.compile("(?m)^\\s*active:\\s*nacos\\s*$").matcher(yaml).find(),
                "plain java -jar / IDE start must activate profile nacos by default");
        assertTrue(yaml.contains("java -jar"),
                "comments must document that a plain start pulls Nacos kkfileview.yml");
        assertTrue(yaml.contains("office.home"),
                "comments must say office.home comes from Nacos / Environment");
        assertTrue(yaml.contains("--spring.profiles.active=cse") || yaml.contains("cse"),
                "comments must document the CSE override");
        assertTrue(yaml.contains("spring.profiles.active=none") || yaml.contains("none"),
                "comments must document turning Nacos off");
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
        assertFalse(yaml.contains("默认全部关闭"),
                "Nacos must be on by default via profile nacos");
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

    @Test
    void cseProfileImportsNacosYamlWithoutNacosDiscovery() throws IOException {
        String yaml = Files.readString(CONFIG_DIR.resolve("application-cse.yml"), StandardCharsets.UTF_8);
        assertTrue(yaml.contains("NACOS_CONFIG_DATA_ID:kkfileview.yml"),
                "cse profile must still import Nacos dataId kkfileview.yml as the config center");
        assertFalse(yaml.contains("kkfileview.properties"),
                "cse profile must not default to kkfileview.properties");
        assertTrue(yaml.contains("spring.cloud.servicecomb.discovery")
                        || yaml.contains("servicecomb:"),
                "cse profile must enable ServiceComb discovery");
        assertTrue(yaml.contains("enabled: false"),
                "cse profile must keep Nacos discovery off");
        assertTrue(yaml.contains("register-enabled: false"),
                "cse profile must not register this instance with Nacos");
        assertFalse(yaml.contains("register-enabled: true"),
                "cse must not register with Nacos (dual registry)");
        assertTrue(yaml.contains("--spring.profiles.active=cse"),
                "comments must document the CSE override");
        assertFalse(yaml.contains("nacos.discovery.enabled: true"),
                "cse must not turn on Nacos discovery (dual registry)");
    }

    @Test
    void configDirectoryAndClasspathUseYamlNotLegacyProperties() throws IOException {
        assertTrue(Files.isRegularFile(CONFIG_DIR.resolve("application.yml")));
        assertTrue(Files.isRegularFile(CONFIG_DIR.resolve("application-nacos.yml")));
        assertTrue(Files.isRegularFile(CONFIG_DIR.resolve("application-cse.yml")));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(CONFIG_DIR, "*.properties")) {
            assertFalse(stream.iterator().hasNext(),
                    "src/main/config must not contain leftover application*.properties");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL applicationYml = classLoader.getResource("application.yml");
        if (applicationYml != null) {
            assertNotNull(classLoader.getResource("application-nacos.yml"),
                    "packaged classpath must include application-nacos.yml");
            assertNotNull(classLoader.getResource("application-cse.yml"),
                    "packaged classpath must include application-cse.yml");
            assertNull(classLoader.getResource("application.properties"),
                    "packaged classpath must not include leftover application.properties");
            assertNull(classLoader.getResource("application-nacos.properties"),
                    "packaged classpath must not include leftover application-nacos.properties");
            assertNull(classLoader.getResource("application-cse.properties"),
                    "packaged classpath must not include leftover application-cse.properties");
        }
    }
}
