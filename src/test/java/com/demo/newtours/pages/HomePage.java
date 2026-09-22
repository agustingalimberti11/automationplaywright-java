package com.demo.newtours.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

/**
 * Pagina de bienvenida: https://demo.guru99.com/test/newtours/
 *
 * Tiene el formulario de login a la derecha:
 *   input[name='userName']
 *   input[name='password']
 *   input[name='submit']
 *
 * Esta pagina es vieja (HTML de los 90). No tiene labels bien conectados,
 * por eso usamos el atributo name y no getByLabel.
 */
public class HomePage extends BasePage {

    private final Locator userName;
    private final Locator password;
    private final Locator submit;

    public HomePage(Page page) {
        super(page);
        this.userName = page.locator("input[name='userName']");
        this.password = page.locator("input[name='password']");
        this.submit = page.locator("input[name='submit']");
    }

    @Step("Abrir Home de New Tours")
    public void abrir() {
        abrir("");
    }

    @Step("Iniciar sesion con usuario {usuario}")
    public void iniciarSesion(String usuario, String clave) {
        userName.fill(usuario);
        password.fill(clave);
        submit.click();
    }

    public Locator campoUsuario() {
        return userName;
    }
}
