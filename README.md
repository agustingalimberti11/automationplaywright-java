# New Tours + Playwright + Cucumber (Java / Maven)

Proyecto de aprendizaje para automatizar [DemoGuru New Tours](https://demo.guru99.com/test/newtours/) con Playwright y BDD.

Cucumber describe **qué** validar (escenarios en español). Los Page Objects y Playwright hacen **cómo** se hace en el browser.

Las pantallas usan **herencia** (`HomePage` extiende `BasePage`). No hay composición de componentes.

## Qué necesitas

- **Java 17 o más** (`java -version`). Esto sí o sí.
- **Maven no hace falta instalarlo.** El repo trae `mvnw.cmd` (Windows) y `mvnw` (Linux/Mac): la primera vez se baja Maven solo.

Si en la PC del laburo no tenés `mvn` en el PATH, usá siempre `.\mvnw.cmd` en vez de `mvn`. Hace lo mismo.

La primera corrida necesita internet (Maven, librerías y Chromium). Si el firewall del laburo bloquea `repo.maven.apache.org` o `cdn.playwright.dev`, no va a poder bajar nada.

## Cómo correrlo

En PowerShell, desde la carpeta del proyecto. Primera vez (descarga Chromium):

```powershell
.\mvnw.cmd exec:java "-Dexec.args=install chromium"
```

Todos los escenarios:

```powershell
.\mvnw.cmd test
```

Sin headless (se abre Chromium y ves cada paso):

```powershell
.\mvnw.cmd test -Dheadless=false
```

Por defecto `.\mvnw.cmd test` corre headless (sin ventana). `-Dheadless=false` lo apaga. Se puede combinar con tags:

```powershell
.\mvnw.cmd test -Dheadless=false "-Dcucumber.filter.tags=@smoke"
```

Solo smoke (casos cortos / críticos):

```powershell
.\mvnw.cmd test "-Dcucumber.filter.tags=@smoke"
```

Solo regresion (el resto de la suite):

```powershell
.\mvnw.cmd test "-Dcucumber.filter.tags=@regresion"
```

Reporte Allure (después de `.\mvnw.cmd test`):

```powershell
.\mvnw.cmd allure:serve
```

Usuario de demo de New Tours: `mercury` / `mercury`. Está en `BrowserManager`.

---

## Arquitectura

Cuando corrés `mvn test`, Maven no abre el browser directo. Pasa por estas capas:

```text
mvn test
    → pom.xml (Surefire) corre RunCucumberTest
        → Cucumber lee los .feature
            → Hooks abre Playwright / Browser / Page
                → Steps interpretan cada frase del .feature
                    → Page Objects hablan con la pantalla
                        → Playwright mueve el browser
```

Cada capa tiene un trabajo solo:

| Capa | Pregunta que responde | Dónde está |
|---|---|---|
| Feature | ¿Qué tiene que pasar en negocio? | `features/ui/` (pantalla) y `features/api/` (HTTP) |
| Runner | ¿Por dónde entra Maven a Cucumber? | `runners/RunCucumberTest.java` |
| Hooks | ¿Cuándo se abre y cierra el browser? | `ui/hooks/` |
| Steps | ¿Qué Java ejecuta cada frase? | `ui/steps/` |
| Pages | ¿Cómo se hace clic/fill en ESA pantalla? | `ui/pages/` |
| Playwright | ¿Cómo se controla Chromium? | librería, no código nuestro |

Un escenario de login, de punta a punta:

1. `login.feature` dice: *Cuando inicia sesion con el usuario de demo*.
2. `LoginSteps` recibe esa frase y llama a `home.iniciarSesion(...)`.
3. `HomePage` hace `fill` en usuario/password y `click` en Submit.
4. Playwright espera solo a que el elemento esté listo (no hay `Thread.sleep`).
5. El `Entonces` afirma el texto *Login Successfully*.

Si un escenario falla, `Hooks` saca una captura (Allure + `target/screenshots`).

Playwright no es thread-safe. Por eso `BrowserManager` guarda **un browser por hilo**, y cada escenario recibe un `BrowserContext` y una `Page` nuevos (sesión limpia: sin cookies del escenario anterior).

---

## Mapa de carpetas

```text
automationplaywright/
├── pom.xml                              Maven: librerías y cómo se corren los tests
├── mvnw.cmd / mvnw                      Wrapper: corre Maven sin instalarlo
├── .mvn/wrapper/                        Qué versión de Maven baja el wrapper
├── Jenkinsfile                          Pipeline de CI
├── .gitignore                           Qué no se sube a Git
├── README.md
└── src/test/
    ├── resources/
    │   ├── features/
    │   │   ├── ui/                      Escenarios de pantalla
    │   │   │   ├── login.feature
    │   │   │   ├── registro.feature
    │   │   │   ├── navegacion.feature
    │   │   │   └── vuelos.feature
    │   │   └── api/                     Escenarios de API (vacío por ahora)
    │   ├── junit-platform.properties    Cómo corre Cucumber (glue, paralelo, Allure)
    │   └── allure.properties            Dónde guarda Allure los JSON
    └── java/com/demo/newtours/
        ├── runners/RunCucumberTest.java Punto de entrada de Maven
        ├── ui/                          Todo lo de pantalla
        │   ├── hooks/
        │   │   ├── Hooks.java           @Before / @After de cada escenario
        │   │   ├── BrowserManager.java  Arranque del browser + URL/usuario
        │   │   └── ScenarioState.java   Comparte la Page entre steps
        │   ├── steps/                   Una clase por área funcional
        │   └── pages/
        │       ├── BasePage.java        Menú común (padre)
        │       ├── HomePage.java
        │       ├── RegisterPage.java
        │       └── FlightFinderPage.java
        └── api/                         Clientes HTTP (vacío por ahora)
```

### Java (qué hace cada clase)

- **`RunCucumberTest`**: clase vacía con anotaciones. Le dice a JUnit: usá el motor Cucumber, leé `features/` y buscá steps en `com.demo.newtours`.
- **`Hooks`**: antes de cada escenario abre una pestaña; después la cierra. Si falló, adjunta screenshot.
- **`BrowserManager`**: crea Playwright y Chromium. Acá están URL, browser, timeout, usuario demo y headless.
- **`ScenarioState`**: PicoContainer inyecta la misma instancia en Hooks y Steps, para no usar variables `static` con la `Page`.
- **`BasePage`**: `abrir(ruta)`, menú (REGISTER, Flights, etc.) y cierre del banner de cookies si aparece.
- **`HomePage` / `RegisterPage` / `FlightFinderPage`**: locators y acciones de esa pantalla. El test no habla con el DOM.

### Features y tags

Solo hay dos tags:

- `@smoke`: home, login válido, registro, menú REGISTER
- `@regresion`: login inválido, Flights, SUPPORT, búsqueda de vuelo

Los datos del registro y del vuelo van **en el `.feature`** (tablas), no en una clase Java de datos.

---

## Archivos de configuración

### `pom.xml`

Es el corazón de Maven. Sin este archivo `.\mvnw.cmd test` (o `mvn test`) no existe.

- **`properties`**: versiones (Java 17, Playwright, Cucumber, Allure, Maven Wrapper 3.9.9).
- **`dependencyManagement`**: BOM de Allure y Cucumber para que todos los módulos usen la misma versión.
- **Dependencias de test**:
  - `playwright`: el browser
  - `cucumber-java`: `@Dado` / `@Cuando` / `@Entonces`
  - `cucumber-junit-platform-engine`: Cucumber corre dentro de JUnit 5
  - `cucumber-picocontainer`: inyecta `ScenarioState` en steps y hooks
  - `junit-platform-suite`: permite la clase `RunCucumberTest`
  - `allure-cucumber7-jvm`: cada escenario aparece en Allure
  - `aspectjrt`: hace que `@Step` de Allure en los Page Objects se grabe
- **`maven-compiler-plugin`**: compila con Java 17.
- **`maven-surefire-plugin`**: ejecuta tests. Solo incluye `RunCucumberTest` (así no se duplican escenarios). El `argLine` de AspectJ es para Allure. `allure.results.directory` apunta a `target/allure-results`.
- **`allure-maven`**: `.\mvnw.cmd allure:serve` arma el HTML del reporte.
- **`exec-maven-plugin`**: `.\mvnw.cmd exec:java "-Dexec.args=install chromium"` descarga el browser de Playwright.
- **`maven-wrapper-plugin`**: deja `mvnw.cmd` en el repo. En una PC sin Maven instalado, ese script baja Maven 3.9.9 solo. La versión está en `properties` (`maven.version`).

### `src/test/resources/junit-platform.properties`

Cucumber / JUnit leen este archivo al arrancar.

| Clave | Para qué |
|---|---|
| `cucumber.glue` | Paquete donde están steps y hooks (`com.demo.newtours`) |
| `cucumber.plugin` | `pretty` y `summary` en consola; Allure genera el reporte |
| `cucumber.publish.quiet` | No molesta con el banner de Cucumber Reports |
| `cucumber.execution.parallel.enabled` | Escenarios en paralelo (2 hilos) |
| `cucumber.execution.parallel.config.fixed.parallelism` | Cuántos hilos |
| `cucumber.junit-platform.naming-strategy=long` | Nombres claros en Surefire / Jenkins |

Filtrar por tag **no** va acá: se pasa en la consola (`-Dcucumber.filter.tags=@smoke`).

### `src/test/resources/allure.properties`

Una sola línea: `allure.results.directory=target/allure-results`.

Ahí Cucumber deja JSON crudo. `mvn allure:serve` o Jenkins los convierten en HTML. Esa carpeta no se commitea.

### `.gitignore`

Lista lo que Git no debe subir:

- `target/` (compilado y reportes)
- `.idea/`, `*.iml` (IntelliJ)
- `.allure/`, `allure-results/` (cache y resultados de Allure)
- logs y basura de SO

### `Jenkinsfile`

Pipeline de Jenkins, no de Maven. En cada build:

1. Instala Chromium con la CLI de Playwright.
2. Corre `mvn clean test -Dheadless=true`.
3. **Siempre** (pase o falle): publica Allure desde `target/allure-results` y archiva screenshots.

Las tools `jdk17` y `maven3` tienen que existir en Jenkins con esos nombres, o hay que cambiarlas en este archivo.

### Constantes en `BrowserManager.java`

No hay `config.properties`. La config de entorno está en esa clase:

| Constante | Significado |
|---|---|
| `BASE_URL` | Home de New Tours |
| `BROWSER` | `chromium` (también podría ser `firefox` o `webkit`) |
| `TIMEOUT_MS` | Espera máxima de Playwright y de `assertThat` |
| `DEMO_USER` / `DEMO_PASSWORD` | Login válido de la demo |
| `headless()` | Por defecto `true`. Con `-Dheadless=false` ves el browser |

Si cambia la URL o el usuario de demo, se edita solo ese archivo.
