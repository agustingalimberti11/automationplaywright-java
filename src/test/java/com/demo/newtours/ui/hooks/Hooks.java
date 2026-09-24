package com.demo.newtours.ui.hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Equivale al viejo BaseTest, pero en Cucumber.
 *
 * @BeforeAll / @AfterAll -> browser
 * @Before / @After       -> pestana nueva por escenario
 */
public class Hooks {

    private final ScenarioState state;

    public Hooks(ScenarioState state) {
        this.state = state;
    }

    @BeforeAll
    public static void beforeAll() {
        PlaywrightAssertions.setDefaultAssertionTimeout(BrowserManager.TIMEOUT_MS);
    }

    @Before
    public void openFreshPage() {
        BrowserManager.ensureBrowser();
        state.setPage(BrowserManager.openPage());
    }

    @After
    public void closePage(Scenario scenario) {
        Page page = state.page();
        if (scenario.isFailed() && page != null && !page.isClosed()) {
            byte[] image = page.screenshot();
            scenario.attach(image, "image/png", "captura");
            Allure.addAttachment("Captura al fallar", "image/png", new ByteArrayInputStream(image), "png");
            guardarArchivo(scenario, image);
        }
        BrowserManager.closeContext();
    }

    @AfterAll
    public static void afterAll() {
        BrowserManager.closeBrowser();
    }

    private void guardarArchivo(Scenario scenario, byte[] image) {
        try {
            Path folder = Path.of("target", "screenshots");
            Files.createDirectories(folder);
            String fileName = scenario.getName().replaceAll("[^a-zA-Z0-9._-]", "_") + ".png";
            Files.write(folder.resolve(fileName), image);
        } catch (Exception ignored) {
            // Si no se puede guardar el archivo, queda adjunto en Cucumber y Allure.
        }
    }
}
