package cn.keking.utils;

import org.jodconverter.core.util.OSUtils;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Resolves LibreOffice / OpenOffice home.
 *
 * <p>When Nacos config is on (default {@code nacos} profile, or {@code cse} which
 * still imports Nacos), {@code office.home} comes from Spring {@link Environment}
 * (Nacos {@code kkfileview.yml}). The local {@code application.yml} is skipped so
 * a stripped boot YAML cannot hide the Nacos path.
 *
 * <p>When Nacos is off ({@code --spring.profiles.active=none}), this still reads
 * {@code office.home} from the local config file, then auto-detects installs.
 */
public class LocalOfficeUtils {

    public static final String OFFICE_HOME_KEY = "office.home";
    public static final String DEFAULT_OFFICE_HOME_VALUE = "default";
    static final String NACOS_CONFIG_ENABLED_KEY = "spring.cloud.nacos.config.enabled";

    private static final String EXECUTABLE_DEFAULT = "program/soffice.bin";
    private static final String EXECUTABLE_MAC = "program/soffice";
    private static final String EXECUTABLE_MAC_41 = "MacOS/soffice";
    private static final String EXECUTABLE_WINDOWS = "program/soffice.exe";

    public static File getDefaultOfficeHome() {
        return getDefaultOfficeHome(null);
    }

    public static File getDefaultOfficeHome(Environment environment) {
        String officeHome = resolveOfficeHome(environment);
        if (isConfigured(officeHome)) {
            return new File(officeHome);
        }
        return detectInstalledOfficeHome();
    }

    static String resolveOfficeHome(Environment environment) {
        if (environment != null) {
            String fromEnv = environment.getProperty(OFFICE_HOME_KEY);
            if (isConfigured(fromEnv)) {
                return fromEnv;
            }
            if (isNacosConfigEnabled(environment)) {
                return fromEnv;
            }
        }
        return readOfficeHomeFromLocalYaml();
    }

    static boolean isConfigured(String officeHome) {
        if (officeHome == null) {
            return false;
        }
        String trimmed = officeHome.trim();
        return !trimmed.isEmpty() && !DEFAULT_OFFICE_HOME_VALUE.equalsIgnoreCase(trimmed);
    }

    static boolean isNacosConfigEnabled(Environment environment) {
        return environment != null
                && Boolean.TRUE.equals(environment.getProperty(NACOS_CONFIG_ENABLED_KEY, Boolean.class, Boolean.FALSE));
    }

    static String readOfficeHomeFromLocalYaml() {
        try {
            Properties properties = ConfigUtils.loadYamlProperties(ConfigUtils.getCustomizedConfigPath());
            return properties.getProperty(OFFICE_HOME_KEY);
        } catch (Exception ignored) {
            return null;
        }
    }

    static File detectInstalledOfficeHome() {
        if (OSUtils.IS_OS_WINDOWS) {
            String userDir = ConfigUtils.getUserDir();
            // Try to find the most recent version of LibreOffice or OpenOffice,
            // starting with the 64-bit version. %ProgramFiles(x86)% on 64-bit
            // machines; %ProgramFiles% on 32-bit ones
            final String programFiles64 = System.getenv("ProgramFiles");
            final String programFiles32 = System.getenv("ProgramFiles(x86)");
            return findOfficeHome(EXECUTABLE_WINDOWS,
                    userDir + File.separator + "LibreOfficePortable" + File.separator  + "App" + File.separator + "libreoffice",
                    programFiles32 + File.separator + "LibreOffice",
                    programFiles64 + File.separator + "LibreOffice 7",
                    programFiles32 + File.separator + "LibreOffice 7",
                    programFiles64 + File.separator + "LibreOffice 6",
                    programFiles32 + File.separator + "LibreOffice 6",
                    programFiles64 + File.separator + "LibreOffice 5",
                    programFiles32 + File.separator + "LibreOffice 5",
                    programFiles64 + File.separator + "LibreOffice 4",
                    programFiles32 + File.separator + "LibreOffice 4",
                    programFiles32 + File.separator + "OpenOffice 4",
                    programFiles64 + File.separator + "LibreOffice 3",
                    programFiles32 + File.separator + "LibreOffice 3",
                    programFiles32 + File.separator + "OpenOffice.org 3");
        } else if (OSUtils.IS_OS_MAC) {
            File homeDir = findOfficeHome(EXECUTABLE_MAC_41,
                            "/Applications/LibreOffice.app/Contents",
                            "/Applications/OpenOffice.app/Contents",
                            "/Applications/OpenOffice.org.app/Contents");

            if (homeDir == null) {
                homeDir = findOfficeHome(EXECUTABLE_MAC,
                                "/Applications/LibreOffice.app/Contents",
                                "/Applications/OpenOffice.app/Contents",
                                "/Applications/OpenOffice.org.app/Contents");
            }
            return homeDir;
        } else {
            // Linux or other *nix variants
            return findOfficeHome(EXECUTABLE_DEFAULT,
                    "/opt/libreoffice6.0",
                    "/opt/libreoffice6.1",
                    "/opt/libreoffice6.2",
                    "/opt/libreoffice6.3",
                    "/opt/libreoffice6.4",
                    "/opt/libreoffice7.0",
                    "/opt/libreoffice7.1",
                    "/opt/libreoffice7.2",
                    "/opt/libreoffice7.3",
                    "/opt/libreoffice7.4",
                    "/opt/libreoffice7.5",
                    "/opt/libreoffice7.6",
                    "/opt/libreoffice24.2",
                    "/opt/libreoffice24.8",
                    "/opt/libreoffice25.2",
                    "/opt/libreoffice25.8",
                    "/opt/libreoffice26.2",
                    "/opt/libreoffice26.8",
                    "/usr/lib64/libreoffice",
                    "/usr/lib/libreoffice",
                    "/usr/local/lib64/libreoffice",
                    "/usr/local/lib/libreoffice",
                    "/opt/libreoffice",
                    "/usr/lib64/openoffice",
                    "/usr/lib64/openoffice.org3",
                    "/usr/lib64/openoffice.org",
                    "/usr/lib/openoffice",
                    "/usr/lib/openoffice.org3",
                    "/usr/lib/openoffice.org",
                    "/opt/openoffice4",
                    "/opt/openoffice.org3");
        }
    }

    private static File findOfficeHome(final String executablePath, final String... homePaths) {
        return Stream.of(homePaths)
                .filter(homePath -> homePath != null && Files.isRegularFile(Paths.get(homePath, executablePath)))
                .findFirst()
                .map(File::new)
                .orElse(null);
    }
}
