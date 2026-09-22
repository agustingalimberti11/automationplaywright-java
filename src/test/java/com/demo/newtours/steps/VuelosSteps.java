package com.demo.newtours.steps;

import com.demo.newtours.hooks.ScenarioState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class VuelosSteps {

    private final ScenarioState state;

    public VuelosSteps(ScenarioState state) {
        this.state = state;
    }

    @Dado("que el usuario abre el buscador de vuelos")
    public void abrirBuscador() {
        state.vuelos().abrir();
    }

    @Entonces("el buscador de vuelos es visible")
    public void buscadorVisible() {
        assertThat(state.vuelos().radioIda()).isVisible();
        assertThat(state.vuelos().comboOrigen()).isVisible();
    }

    @Cuando("busca un vuelo con:")
    public void buscaVuelo(DataTable tabla) {
        Map<String, String> datos = tabla.asMap();
        state.vuelos().buscarVuelo(
                valor(datos, "tipo"),
                valor(datos, "pasajeros"),
                valor(datos, "origen"),
                valor(datos, "mesIda"),
                valor(datos, "diaIda"),
                valor(datos, "destino"),
                valor(datos, "mesVuelta"),
                valor(datos, "diaVuelta"),
                valor(datos, "clase"),
                valor(datos, "aerolinea")
        );
    }

    private String valor(Map<String, String> datos, String clave) {
        String valor = datos.get(clave);
        if (valor == null) {
            throw new IllegalArgumentException("Falta la columna: " + clave);
        }
        return valor.trim();
    }

    @Entonces("la url contiene {string}")
    public void urlContiene(String fragmento) {
        String url = state.page().url();
        if (!url.contains(fragmento)) {
            throw new AssertionError("Se esperaba que la url contenga '" + fragmento + "' pero fue: " + url);
        }
    }
}
