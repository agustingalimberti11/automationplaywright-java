package com.demo.newtours.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

/**
 * Buscador de vuelos: reservation.php
 *
 * Aqui practicas tres cosas distintas de Playwright:
 *  - radio buttons  -> check()
 *  - combos select  -> selectOption()
 *  - boton imagen   -> click()
 */
public class FlightFinderPage extends BasePage {

    private final Locator oneWay;
    private final Locator roundTrip;
    private final Locator passengers;
    private final Locator fromPort;
    private final Locator fromMonth;
    private final Locator fromDay;
    private final Locator toPort;
    private final Locator toMonth;
    private final Locator toDay;
    private final Locator coach;
    private final Locator business;
    private final Locator first;
    private final Locator airline;
    private final Locator findFlights;

    public FlightFinderPage(Page page) {
        super(page);
        this.oneWay = page.locator("input[name='tripType'][value='oneway']");
        this.roundTrip = page.locator("input[name='tripType'][value='roundtrip']");
        this.passengers = page.locator("select[name='passCount']");
        this.fromPort = page.locator("select[name='fromPort']");
        this.fromMonth = page.locator("select[name='fromMonth']");
        this.fromDay = page.locator("select[name='fromDay']");
        this.toPort = page.locator("select[name='toPort']");
        this.toMonth = page.locator("select[name='toMonth']");
        this.toDay = page.locator("select[name='toDay']");
        this.coach = page.locator("input[name='servClass'][value='Coach']");
        this.business = page.locator("input[name='servClass'][value='Business']");
        this.first = page.locator("input[name='servClass'][value='First']");
        this.airline = page.locator("select[name='airline']");
        this.findFlights = page.locator("input[name='findFlights']");
    }

    @Step("Abrir buscador de vuelos")
    public void abrir() {
        abrir("reservation.php");
    }

    @Step("Buscar vuelo {origen} -> {destino}")
    public void buscarVuelo(
            String tipoViaje,
            String cantidadPasajeros,
            String origen,
            String mesIda,
            String diaIda,
            String destino,
            String mesVuelta,
            String diaVuelta,
            String clase,
            String aerolinea
    ) {
        if ("oneway".equalsIgnoreCase(tipoViaje)) {
            oneWay.check();
        } else {
            roundTrip.check();
        }

        passengers.selectOption(cantidadPasajeros);
        fromPort.selectOption(origen);
        fromMonth.selectOption(mesIda);
        fromDay.selectOption(diaIda);
        toPort.selectOption(destino);
        toMonth.selectOption(mesVuelta);
        toDay.selectOption(diaVuelta);

        switch (clase.toLowerCase()) {
            case "business" -> business.check();
            case "first" -> first.check();
            default -> coach.check();
        }

        airline.selectOption(aerolinea);
        findFlights.scrollIntoViewIfNeeded();
        findFlights.click();
    }

    public Locator radioIda() {
        return oneWay;
    }

    public Locator comboOrigen() {
        return fromPort;
    }
}
