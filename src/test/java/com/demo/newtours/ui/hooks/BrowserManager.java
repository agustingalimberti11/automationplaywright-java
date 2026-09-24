package com.demo.newtours.ui.hooks;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Arranca Playwright por hilo.
 *
 * Cucumber puede correr escenarios en paralelo. Playwright no es thread-safe,
 * por eso cada hilo tiene su propio Playwright / Browser / Context / Page.
 *
 * - Playwright + Browser: se reutilizan (arrancar Chrome es caro)
 * - Context + Page: uno nuevo en cada escenario (sesion limpia)
 *
 * Si queres ver el browser: mvn test -Dheadless=false
 */
public final class BrowserManager {

    public static final String BASE_URL = "https://demo.guru99.com/test/newtours/";
    public static final String BROWSER = "chromium";
    public static final double TIMEOUT_MS = 45_000;
    public static final String DEMO_USER = "mercury";
    public static final String DEMO_PASSWORD = "mercury";

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER_INSTANCE = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private BrowserManager() {
    }

    public static boolean headless() {
        String fromCli = System.getProperty("headless");
        if (fromCli != null && !fromCli.isBlank()) {
            return Boolean.parseBoolean(fromCli);
        }
        return true;
    }

    public static void ensureBrowser() {
        if (PLAYWRIGHT.get() != null) {
            return;
        }

        boolean headless = headless();
        Playwright playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(headless ? 0 : 200);

        Browser browser = switch (BROWSER.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(options);
            case "webkit" -> playwright.webkit().launch(options);
            default -> playwright.chromium().launch(options);
        };

        PLAYWRIGHT.set(playwright);
        BROWSER_INSTANCE.set(browser);
    }

    public static Page openPage() {
        BrowserContext context = BROWSER_INSTANCE.get().newContext(new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setViewportSize(1366, 768));
        context.setDefaultTimeout(TIMEOUT_MS);
        Page page = context.newPage();
        CONTEXT.set(context);
        PAGE.set(page);
        return page;
    }

    public static Page page() {
        return PAGE.get();
    }

    public static void closeContext() {
        BrowserContext context = CONTEXT.get();
        if (context != null) {
            context.close();
        }
        CONTEXT.remove();
        PAGE.remove();
    }

    public static void closeBrowser() {
        closeContext();
        Browser browser = BROWSER_INSTANCE.get();
        if (browser != null) {
            browser.close();
        }
        Playwright playwright = PLAYWRIGHT.get();
        if (playwright != null) {
            playwright.close();
        }
        BROWSER_INSTANCE.remove();
        PLAYWRIGHT.remove();
    }
}
