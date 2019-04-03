package de.scarpex.mrf.command.bing;

import de.scarpex.mrf.Launcher;
import de.scarpex.mrf.account.MSAccount;
import de.scarpex.mrf.account.MSAccountManager;
import de.scarpex.mrf.command.Command;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private ChromeDriver driver;

    public SearchCommand(MSAccountManager accountManager, File folder) {
        super("search", "Automatic farming of reward points via the Bing search.");

        this.random = new Random();
        this.words = new ArrayList<>();
        this.accountManager = accountManager;

        loadWordList(folder);
    }

    @Override
    public void execute(String[] args) {
        this.driver = new ChromeDriver();
        long epoch = System.currentTimeMillis();

        this.accountManager.getAccounts().forEach(account -> {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(String.format("user-agent=%s", Launcher.PC_AGENT));
            this.driver.getCapabilities().merge(options);

            login(account);

            // TODO: dont know, does it works? Test the desktop and mobile support later...
            for (int i = 0; i < DEFAULT_DESKTOP_REQUEST_VALUE; i++) {
                search();
            }

            options = new ChromeOptions();
            options.addArguments(String.format("user-agent=%s", Launcher.MOBILE_AGENT));
            this.driver.getCapabilities().merge(options);
            for (int i = 0; i < DEFAULT_MOBILE_REQUEST_VALUE; i++) {
                search();
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
        search.sendKeys(this.words.get(this.random.nextInt(this.words.size())));

        this.driver.findElement(By.id("sb_form_go")).click();

        sleep();
    }

    /**
     * Loads a file containing 370,099 words.
     */
    private void loadWordList(File folder) {
        File file = new File(folder, "words.txt");
        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + "words.txt")) {
                try (OutputStream output = new FileOutputStream(file)) {
                    IOUtils.copy(input, output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Stream<String> stream = Files.lines(Paths.get("words.txt"))) {
            stream.forEach(this.words::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait a moment between one and two seconds.
     */
    private void sleep() {
        try {
            Thread.sleep(this.random.nextInt(3000 - 1000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
