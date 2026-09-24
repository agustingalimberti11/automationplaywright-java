package com.demo.newtours.ui.hooks;

import com.demo.newtours.ui.pages.FlightFinderPage;
import com.demo.newtours.ui.pages.HomePage;
import com.demo.newtours.ui.pages.RegisterPage;
import com.microsoft.playwright.Page;

/**
 * Estado de UN escenario.
 *
 * Cucumber + PicoContainer crea una instancia nueva por escenario
 * y la inyecta en Hooks y en los Steps. Asi compartis la Page
 * sin variables static.
 */
public class ScenarioState {

    private Page page;
    private String usuarioCreado;

    public void setPage(Page page) {
        this.page = page;
    }

    public Page page() {
        return page;
    }

    public HomePage home() {
        return new HomePage(page);
    }

    public RegisterPage registro() {
        return new RegisterPage(page);
    }

    public FlightFinderPage vuelos() {
        return new FlightFinderPage(page);
    }

    public void setUsuarioCreado(String usuarioCreado) {
        this.usuarioCreado = usuarioCreado;
    }

    public String usuarioCreado() {
        return usuarioCreado;
    }
}
