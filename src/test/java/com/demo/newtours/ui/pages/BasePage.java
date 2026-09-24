package com.demo.newtours.ui.pages;

import com.demo.newtours.ui.hooks.BrowserManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Padre de todas las pantallas.
 *
 * Cada Page Object recibe la misma Page (pestana) y sabe como
 * hablar con ESA pantalla: clics, textos, selects.
 *
 * Las otras clases (HomePage, RegisterPage, etc.) heredan de aqui
 * para reutilizar el menu de New Tours, que se ve en casi todas las paginas.
 *
 * No hay composicion: no hay un objeto Menu aparte. El menu vive aqui.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    @Step("Abrir {ruta}")
    public void abrir(String ruta) {
        page.navigate(BrowserManager.BASE_URL + ruta);
        cerrarBannerCookiesSiAparece();
    }

    @Step("Ir al menu: {nombre}")
    public void irAlMenu(String nombre) {
        enlaceDelMenu(nombre).click();
        cerrarBannerCookiesSiAparece();
    }

    public Locator enlaceDelMenu(String nombre) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(nombre).setExact(true));
    }

    /**
     * A veces Guru99 muestra un iframe de cookies. Si no esta, no hacemos nada.
     * count() no espera: mira el DOM ahora mismo.
     */
    protected void cerrarBannerCookiesSiAparece() {
        Locator iframe = page.locator("iframe#gdpr-consent-notice");
        if (iframe.count() == 0) {
            return;
        }
        try {
            page.frameLocator("iframe#gdpr-consent-notice").locator("#save").click();
        } catch (RuntimeException ignored) {
            // Si el banner ya se cerro o cambio, el test sigue.
        }
    }

    public String titulo() {
        return page.title();
    }

    public String urlActual() {
        return page.url();
    }
}
