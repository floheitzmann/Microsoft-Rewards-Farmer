package de.scarpex.mrf.chromedriver;

/**
 * This class contains the OS types supported for the Chromedriver.
 *
 * @author Florian Heitzmann
 */
public enum OSType {
    WINDOWS("win32"),
    MAC("mac64"),
    LINUX("linux64");

    private String fileName;

    OSType(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the name of the Chromedriver file.
     *
     * @return the name of the Chromedriver file.
     */
    public String getFileName() {
        return this.fileName;
    }
}
