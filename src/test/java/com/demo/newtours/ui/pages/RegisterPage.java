package com.demo.newtours.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Formulario de registro: register.php
 *
 * Ojo: en esta web los names estan al reves de lo que pensarias:
 *  - userName  = email de contacto
 *  - email     = usuario para iniciar sesion
 */
public class RegisterPage extends BasePage {

    private final Locator firstName;
    private final Locator lastName;
    private final Locator phone;
    private final Locator contactEmail;
    private final Locator address;
    private final Locator city;
    private final Locator state;
    private final Locator postalCode;
    private final Locator country;
    private final Locator userName;
    private final Locator password;
    private final Locator confirmPassword;
    private final Locator submit;

    public RegisterPage(Page page) {
        super(page);
        this.firstName = page.locator("input[name='firstName']");
        this.lastName = page.locator("input[name='lastName']");
        this.phone = page.locator("input[name='phone']");
        this.contactEmail = page.locator("input[name='userName']");
        this.address = page.locator("input[name='address1']");
        this.city = page.locator("input[name='city']");
        this.state = page.locator("input[name='state']");
        this.postalCode = page.locator("input[name='postalCode']");
        this.country = page.locator("select[name='country']");
        this.userName = page.locator("input[name='email']");
        this.password = page.locator("input[name='password']");
        this.confirmPassword = page.locator("input[name='confirmPassword']");
        this.submit = page.locator("input[name='submit']");
    }

    @Step("Abrir pagina de registro")
    public void abrir() {
        abrir("register.php");
    }

    @Step("Completar y enviar el registro de {usuario}")
    public void registrar(
            String nombre,
            String apellido,
            String telefono,
            String mailContacto,
            String direccion,
            String ciudad,
            String provincia,
            String codigoPostal,
            String pais,
            String usuario,
            String clave
    ) {
        firstName.fill(nombre);
        lastName.fill(apellido);
        phone.fill(telefono);
        contactEmail.fill(mailContacto);
        address.fill(direccion);
        city.fill(ciudad);
        state.fill(provincia);
        postalCode.fill(codigoPostal);
        country.selectOption(pais);
        userName.fill(usuario);
        password.fill(clave);
        confirmPassword.fill(clave);
        submit.click();
    }

    public Locator campoNombre() {
        return firstName;
    }

    public Locator mensajeGracias() {
        return page.getByText("Thank you for registering");
    }

    public Locator usuarioCreado(String usuario) {
        return page.getByText(usuario);
    }

    public Locator enlaceSignOff() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("SIGN-OFF").setExact(true));
    }
}
