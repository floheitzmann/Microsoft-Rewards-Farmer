package de.scarpex.mrf.command.bing;

import de.scarpex.mrf.Launcher;
import de.scarpex.mrf.account.MSAccount;
import de.scarpex.mrf.account.MSAccountManager;
import de.scarpex.mrf.command.Command;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * To finish the search requests for a Microsoft account. All points are
 * collected automatically. All accounts from the "accounts.json" list
 * are worked through.
 */
public class SearchCommand extends Command {
    private static final Logger log = LogManager.getLogger(SearchCommand.class);
    private static final int DEFAULT_DESKTOP_REQUEST_VALUE = 30;
    private static final int DEFAULT_MOBILE_REQUEST_VALUE = 20;

    private final Random random;
    private final List<String> words;
    private final MSAccountManager accountManager;
    private final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private int highestBreak, lowestBreak;

    private ChromeDriver driver;

    public SearchCommand(MSAccountManager accountManager, File folder, Properties properties) {
        super("search", "Automatic farming of reward points via the Bing search.");

        this.random = new Random();
        this.words = new ArrayList<>();
        this.accountManager = accountManager;

        this.highestBreak = Integer.valueOf(properties.getProperty("highest-break-time")) * 1000;
        this.lowestBreak = Integer.valueOf(properties.getProperty("lowest-break-time")) * 1000;
    }

    @Override
    public void execute(String[] args) {
        long epoch = System.currentTimeMillis();
        List<String> array = Arrays.asList(args);

        ChromeOptions options = new ChromeOptions();
        if (array.contains("--pc")) options.addArguments(String.format("user-agent=%s", Launcher.PC_AGENT));
        if (array.contains("--mobile")) options.addArguments(String.format("user-agent=%s", Launcher.MOBILE_AGENT));
        if (array.contains("--headless")) options.addArguments("--headless");
        this.driver = new ChromeDriver(options);

        this.accountManager.getAccounts().forEach(account -> {
            login(account);
            loadWordList();

            if (array.contains("--pc")) {
                int random = this.random.nextInt(DEFAULT_DESKTOP_REQUEST_VALUE);
                for (int i = 0; i < DEFAULT_DESKTOP_REQUEST_VALUE; i++) {
                    if (random == i) {
                        loadWordList();
                    }
                    search();
                }
            }

            // TODO: fix the mobile problem
            if (array.contains("--mobile")) {
                int random = this.random.nextInt(DEFAULT_MOBILE_REQUEST_VALUE);
                for (int i = 0; i < DEFAULT_MOBILE_REQUEST_VALUE; i++) {
                    if (random == i) {
                        loadWordList();
                    }
                    search();
                }
            }

            logout();
        });

        log.info(String.format("The command took %sms.", (System.currentTimeMillis() - epoch)));
    }

    /**
     * Sign into an account.
     *
     * @param account the Microsoft account.
     */
    private void login(MSAccount account) {
        this.driver.get("https://www.bing.com");

        sleep();

        (new WebDriverWait(this.driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("id_s"))).click();

        WebElement loginfmt = (new WebDriverWait(this.driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.name("loginfmt")));

        loginfmt.sendKeys(account.getLogin());
        this.driver.findElement(By.id("idSIButton9")).click();

        sleep();

        WebElement passwd = (new WebDriverWait(this.driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.name("passwd")));

        passwd.sendKeys(account.getPassword());
        this.driver.findElement(By.id("idSIButton9")).click();

        sleep();
    }

    /**
     * Log out of an account.
     */
    private void logout() {
        (new WebDriverWait(this.driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("id_l"))).click();

        (new WebDriverWait(this.driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("b_idProviders"))).click();
    }

    /**
     * Complete a search query in Bing.
     */
    private void search() {
        WebElement search = this.driver.findElement(By.id("sb_form_q"));
        search.clear();
        String word = this.words.get(this.random.nextInt(this.words.size()));
        search.sendKeys(word);
        log.info("Search request: " + word);

        this.driver.findElement(By.id("sb_form_go")).click();

        sleep();
    }

    /**
     * Loads a random file with categorized words.
     */
    private void loadWordList() {
        if (!this.words.isEmpty()) {
            this.words.clear();
        }

        try (Stream<String> stream = Files.lines(getRandomWordFile().toPath())) {
            stream.forEach(this.words::add);
        } catch (IOException e) {
            log.error("Can not read the file!", e);
        }
    }

    /**
     * Returns a random word file.
     *
     * @return a file.
     */
    private File getRandomWordFile() {
        File file = null;

        try (Stream<Path> paths = Files.walk(Paths.get(System.getProperty("user.dir") + "/words"))) {
            List<Object> list = Arrays.asList(paths.filter(Files::isRegularFile).toArray());
            int random = this.random.nextInt(list.size()) + 1;
            file = ((Path) list.get(random)).toFile();

            while (!file.getName().endsWith(".txt")) {
                random = this.random.nextInt(list.size()) + 1;
                file = ((Path) list.get(random)).toFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Wait a moment to simulate a humanity.
     */
    private void sleep() {
        try {
            int random = this.random.nextInt(this.highestBreak - this.lowestBreak) + 1000;
            log.info("Break time: " + random);
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
