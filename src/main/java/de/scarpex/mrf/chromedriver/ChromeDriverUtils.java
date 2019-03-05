package de.scarpex.mrf.chromedriver;

import org.apache.commons.exec.OS;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class contains methods to simplify working with the Chromedriver.
 *
 * @author Florian Heitzmann
 */
public class ChromeDriverUtils {
    private static final Logger log = LogManager.getLogger(ChromeDriverUtils.class);

    private static final String DRIVER_URL = "https://chromedriver.storage.googleapis.com/%s/chromedriver_%s.zip";
    private static final String VERSION_URL = "https://chromedriver.storage.googleapis.com/LATEST_RELEASE";

    /**
     * Returns the download link of the latest version of Chromedriver. It
     * automatically uses the correct link for the running OS.
     *
     * @return the download link.
     */
    public static String getDownloadLink() {
        return String.format(DRIVER_URL, getLatestVersion(), getOS().getFileName());
    }

    /**
     * Returns the latest version of the Chromedriver.
     *
     * @return the latest version.
     */
    public static String getLatestVersion() {
        String latestVersion = null;
        try (InputStream input = new URL(VERSION_URL).openStream()) {
            latestVersion = IOUtils.toString(input);
        } catch (IOException e) {
            log.error("Error while reading the latest version.", e);
        }
        return latestVersion;
    }

    /**
     * Returns the OS Type, if the OS is not supported then null will be
     * returned.
     *
     * @return the OS type.
     */
    public static OSType getOS() {
        OSType os = null;

        if (OS.isFamilyMac()) {
            os = OSType.MAC;
        } else if (OS.isFamilyUnix()) {
            os = OSType.LINUX;
        } else if (OS.isFamilyWindows() || OS.isFamilyWin9x()) {
            os = OSType.WINDOWS;
        }

        return os;
    }
}
