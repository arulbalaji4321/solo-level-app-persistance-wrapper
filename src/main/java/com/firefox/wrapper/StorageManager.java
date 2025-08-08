package com.firefox.wrapper;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StorageManager {

    private static final String STORAGE_FILE = "storage.json";

    public static void saveStorage(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String localStorage = (String) js.executeScript("return JSON.stringify(window.localStorage);");
            String sessionStorage = (String) js.executeScript("return JSON.stringify(window.sessionStorage);");

            System.out.println("💾 Saving localStorage: " + localStorage);
            System.out.println("💾 Saving sessionStorage: " + sessionStorage);

            String json = "{\"localStorage\":" + localStorage + ",\"sessionStorage\":" + sessionStorage + "}";

            try (FileWriter writer = new FileWriter(STORAGE_FILE)) {
                writer.write(json);
            }
            System.out.println("✅ localStorage and sessionStorage saved successfully");
        } catch (IOException e) {
            System.err.println("❌ Error saving storage:");
            e.printStackTrace();
        }
    }

    public static void loadStorage(WebDriver driver, String url) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(STORAGE_FILE)), StandardCharsets.UTF_8);
            System.out.println("📂 Loaded storage JSON: " + json);

            driver.get(url);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "var storage = JSON.parse(arguments[0]);" +
                "for (var key in storage.localStorage) { window.localStorage.setItem(key, storage.localStorage[key]); }" +
                "for (var key in storage.sessionStorage) { window.sessionStorage.setItem(key, storage.sessionStorage[key]); }",
                json
            );

            driver.navigate().refresh();
            System.out.println("✅ localStorage and sessionStorage loaded successfully");
        } catch (IOException e) {
            System.out.println("⚠️ Storage file not found, skipping load");
        }
    }
}
