package com.demo.newtours.steps;

import com.demo.newtours.hooks.ScenarioState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegistroSteps {

    private final ScenarioState state;

    public RegistroSteps(ScenarioState state) {
        this.state = state;
    }

    @Dado("que el usuario abre la pagina de registro")
    public void abrirRegistro() {
        state.registro().abrir();
    }

    @Cuando("completa el registro con:")
    public void completarRegistro(DataTable tabla) {
        Map<String, String> datos = tabla.asMap();
        String usuario = valor(datos, "usuario") + System.currentTimeMillis();
        state.setUsuarioCreado(usuario);
        state.registro().registrar(
                valor(datos, "nombre"),
                valor(datos, "apellido"),
                valor(datos, "telefono"),
                valor(datos, "mailContacto"),
                valor(datos, "direccion"),
                valor(datos, "ciudad"),
                valor(datos, "provincia"),
                valor(datos, "codigoPostal"),
                valor(datos, "pais"),
                usuario,
                valor(datos, "clave")
        );
    }

    @Entonces("ve el nombre de usuario creado")
    public void veUsuarioCreado() {
        assertThat(state.registro().usuarioCreado(state.usuarioCreado())).isVisible();
    }

    @Entonces("el campo nombre del registro es visible")
    public void campoNombreVisible() {
        assertThat(state.registro().campoNombre()).isVisible();
    }

    private String valor(Map<String, String> datos, String clave) {
        String valor = datos.get(clave);
        if (valor == null) {
            throw new IllegalArgumentException("Falta la columna: " + clave);
        }
        return valor.trim();
    }
}
