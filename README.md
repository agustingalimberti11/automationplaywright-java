# New Tours + Playwright + Cucumber (Java / Maven)

Proyecto de aprendizaje para automatizar [DemoGuru New Tours](https://demo.guru99.com/test/newtours/) con Playwright y BDD.

Cucumber escribe el **que** (escenarios en espanol). Los Page Objects + Playwright hacen el **como**.

No usa composicion de paginas. Usa **herencia**: todas las pantallas extienden `BasePage`.

## Que necesitas

- Java 17 o mas
- Maven 3.9+

## Como correrlo

En PowerShell (Windows), la primera vez:

```powershell
mvn exec:java "-Dexec.args=install chromium"
```

Correr todos los escenarios:

```powershell
mvn test
```

Ver el browser:

```powershell
mvn test -Dheadless=false
```

Solo smoke:

```powershell
mvn test "-Dcucumber.filter.tags=@smoke"
```

Solo regresion:

```powershell
mvn test "-Dcucumber.filter.tags=@regresion"
```

Reporte Allure (despues de `mvn test`):

```powershell
mvn allure:serve
```

## Como esta armado

```
src/test/resources/features/     -> escenarios Gherkin (el negocio)
src/test/java/com/demo/newtours/
  runners/RunCucumberTest.java   -> Maven entra por aca
  hooks/                         -> abre/cierra el browser
  steps/                         -> cada frase del .feature
  pages/                         -> una clase por pantalla
```

Flujo:

1. El **.feature** dice QUE validar, en espanol.
2. El **step** conecta la frase con Java.
3. El **Page Object** habla con la UI.
4. Playwright habla con el browser.

## Ideas que este proyecto te muestra

| Idea | Donde verla |
|---|---|
| Escenario BDD | `features/login.feature` |
| Glue / steps | `steps/LoginSteps.java` |
| Playwright / Browser / Page | `hooks/BrowserManager.java` |
| Un context nuevo por escenario | `hooks/Hooks.java` |
| Locator + fill/click | `HomePage` |
| select y radio | `FlightFinderPage` |
| Allure | `mvn allure:serve` |

## Jenkins

1. Instala el plugin **Allure Report**.
2. Configura JDK 17 y Maven como tools (`jdk17`, `maven3`) o cambia los nombres en `Jenkinsfile`.
3. Crea un Pipeline job apuntando a este `Jenkinsfile`.

## Usuario de demo

New Tours tiene un usuario publico: `mercury` / `mercury`.
Está en `BrowserManager` (`DEMO_USER` / `DEMO_PASSWORD`), junto con la URL y el browser.
