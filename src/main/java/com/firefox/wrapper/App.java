package com.firefox.wrapper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.util.Scanner;

public class App {

    private static WebDriver driver;

    private static final String APP_URL = "http://192.168.31.73:3000/";

    public static void main(String[] args) {
        WebDriverManager.firefoxdriver().setup();

        String userProfileDir = System.getProperty("user.home") + "/.mozilla/firefox/";
        File profilesDir = new File(userProfileDir);
        File[] profiles = profilesDir.listFiles((dir, name) -> name.endsWith(".default-release"));
        if (profiles == null || profiles.length == 0) {
            System.err.println("❌ No Firefox profile found.");
            System.exit(1);
        }

        FirefoxProfile profile = new FirefoxProfile(profiles[0]);
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(profile);

        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();

        // Add shutdown hook to save data if JVM terminates unexpectedly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n⚠ JVM Shutdown detected. Saving data before exit...");
            saveAll();
        }));

        try {
            StorageManager.loadStorage(driver, APP_URL);
            CookieManager.loadCookies(driver, APP_URL);

            System.out.println("🔥 Browser launched with persisted cookies and storage!");

            Scanner scanner = new Scanner(System.in);
            String input = "";

            System.out.println("Type 'save' and press Enter to save data and close browser...");

            while (!input.equalsIgnoreCase("save")) {
                System.out.print("> ");
                input = scanner.nextLine();
                System.out.println("You typed: " + input);
            }

            Thread.sleep(500);  // Give browser a moment to finish updates

            saveAll();

            System.out.println("💾 Storage and cookies saved. Closing browser now...");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    }

    private static void saveAll() {
        if (driver != null) {
            CookieManager.saveCookies(driver);
            StorageManager.saveStorage(driver);
        }
    }
}
