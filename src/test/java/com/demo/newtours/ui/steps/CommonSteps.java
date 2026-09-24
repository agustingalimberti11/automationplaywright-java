package com.demo.newtours.ui.steps;

import com.demo.newtours.ui.hooks.ScenarioState;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Pasos que se reutilizan en varios .feature
 */
public class CommonSteps {

    private final ScenarioState state;

    public CommonSteps(ScenarioState state) {
        this.state = state;
    }

    @Dado("que el usuario abre la home de New Tours")
    @Cuando("el usuario abre la home de New Tours")
    public void abrirHome() {
        state.home().abrir();
    }

    @Cuando("va al menu {string}")
    public void vaAlMenu(String nombre) {
        state.home().irAlMenu(nombre);
    }

    @Entonces("el titulo de la pagina es {string}")
    public void tituloEs(String titulo) {
        assertThat(state.page()).hasTitle(titulo);
    }

    @Entonces("ve el mensaje {string}")
    public void veMensaje(String texto) {
        assertThat(state.page().getByText(texto)).isVisible();
    }

    @Entonces("el menu muestra el enlace {string}")
    public void menuMuestraEnlace(String nombre) {
        assertThat(state.home().enlaceDelMenu(nombre)).isVisible();
    }
}
