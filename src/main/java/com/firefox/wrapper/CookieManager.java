package com.firefox.wrapper;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.Set;

public class CookieManager {

    private static final String COOKIE_FILE = "cookies.data";

    public static void saveCookies(WebDriver driver) {
        Set<Cookie> cookies = driver.manage().getCookies();
        System.out.println("🍪 Saving cookies (" + cookies.size() + " total):");
        for (Cookie c : cookies) {
            System.out.println(" - " + c.getName() + " = " + c.getValue() + ", domain=" + c.getDomain());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COOKIE_FILE))) {
            oos.writeObject(cookies);
            System.out.println("🍪 Cookies saved successfully!");
        } catch (IOException e) {
            System.err.println("❌ Failed to save cookies:");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadCookies(WebDriver driver, String url) {
        File file = new File(COOKIE_FILE);
        if (!file.exists()) {
            System.out.println("⚠ No cookies file found, starting fresh.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Set<Cookie> cookies = (Set<Cookie>) ois.readObject();
            System.out.println("🍪 Loading cookies (" + cookies.size() + " total):");
            driver.get(url);
            for (Cookie cookie : cookies) {
                System.out.println(" - Adding cookie: " + cookie.getName() + " = " + cookie.getValue() + ", domain=" + cookie.getDomain());
                driver.manage().addCookie(cookie);
            }
            driver.navigate().refresh();
            System.out.println("✅ Cookies loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Failed to load cookies:");
            e.printStackTrace();
        }
    }
}
