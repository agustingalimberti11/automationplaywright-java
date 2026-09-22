package com.demo.newtours.steps;

import com.demo.newtours.hooks.BrowserManager;
import com.demo.newtours.hooks.ScenarioState;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginSteps {

    private final ScenarioState state;

    public LoginSteps(ScenarioState state) {
        this.state = state;
    }

    @Cuando("inicia sesion con el usuario de demo")
    public void iniciaSesionDeDemo() {
        state.home().iniciarSesion(BrowserManager.DEMO_USER, BrowserManager.DEMO_PASSWORD);
    }

    @Cuando("inicia sesion con {string} y {string}")
    public void iniciaSesion(String usuario, String clave) {
        state.home().iniciarSesion(usuario, clave);
    }

    @Entonces("el formulario de login es visible")
    public void formularioLoginVisible() {
        assertThat(state.home().campoUsuario()).isVisible();
    }
}
