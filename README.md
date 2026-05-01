# 🔐 CRM Login Automation Framework

Enterprise-grade automation test framework for the **Login** feature of the CRM application ([crm.anhtester.com](https://crm.anhtester.com/admin/authentication)).

Built with **Selenium 4 + TestNG + Cucumber + Allure Report**.

---

## 📋 Table of Contents

- [Requirements](#-requirements)
- [Quick Start](#-quick-start)
- [Project Structure](#-project-structure)
- [Architecture](#-architecture)
- [Test Cases](#-test-cases)
- [Configuration](#-configuration)
- [Allure Report](#-allure-report)
- [FAQ](#-faq)
- [Tech Stack](#-tech-stack)

---

## 🛠 Requirements

| Tool | Minimum Version | Check |
|------|-----------------|-------|
| **Java JDK** | 17+ | `java -version` |
| **Maven** | 3.8+ | `mvn -version` |
| **Browser** | Chrome / Firefox / Edge (latest) | Auto-managed by Selenium Manager |

> **Note:** Selenium Manager (built-in since Selenium 4.6+) handles driver downloads automatically — no manual ChromeDriver installation needed.

---

## 🚀 Quick Start

### 1. Clone & Run

```bash
git clone <repository-url>
cd crm-login-automation
mvn clean test
```

### 2. Run a specific test

```bash
mvn test -Dtest=LoginTest#testLoginSuccess
```

### 3. Run with custom browser (CI/CD override)

```bash
mvn clean test -Dbrowser=firefox -Dheadless=true
```

### 4. Run Cucumber tests only

```bash
mvn test -Dtest=TestRunner
```

### 5. View Allure Report

```bash
mvn allure:serve
```

---

## 📁 Project Structure

```text
crm-login-automation/
├── pom.xml                                  # Maven config & dependencies
├── README.md
├── .gitignore
│
└── src/test/
    ├── java/
    │   ├── base/
    │   │   ├── BasePage.java                # 30+ reusable DOM interaction methods
    │   │   ├── BaseTest.java                # TestNG lifecycle (setup/teardown)
    │   │   └── DriverFactory.java           # Thread-safe WebDriver factory (enum-based)
    │   │
    │   ├── pages/
    │   │   └── LoginPage.java               # Page Object — Login page (fluent API)
    │   │
    │   ├── tests/
    │   │   └── LoginTest.java               # TestNG tests with DDT (@DataProvider)
    │   │
    │   ├── stepdefinitions/
    │   │   ├── Hooks.java                   # Cucumber lifecycle hooks
    │   │   └── LoginSteps.java              # Cucumber step definitions
    │   │
    │   ├── runners/
    │   │   └── TestRunner.java              # Cucumber-TestNG runner
    │   │
    │   └── utils/
    │       ├── ConfigManager.java           # Properties reader (system prop override)
    │       ├── DataHelper.java              # JSON test data reader (dot-notation)
    │       ├── LogHelper.java               # Caller-aware Log4j2 + Allure logging
    │       ├── ScreenRecorderHelper.java    # Thread-safe screenshot & video capture
    │       ├── TestListener.java            # TestNG event listener (auto report)
    │       ├── RetryListener.java           # Auto-apply retry to all tests
    │       └── RetryAnalyzer.java           # Flaky test retry mechanism
    │
    └── resources/
        ├── config.properties                # Browser, timeout, feature flags
        ├── testdata.json                    # Test data (nested JSON)
        ├── testng.xml                       # TestNG suite config (parallel support)
        ├── log4j2.xml                       # Log4j2 configuration
        ├── allure.properties                # Allure output directory
        └── features/
            └── login.feature                # Cucumber feature file
```

---

## 🏗 Architecture

### 1. DriverFactory — Thread-Safe Browser Management

All `WebDriver` and `WebDriverWait` instances are managed via `ThreadLocal`, enabling **parallel test execution** with complete thread isolation.

Browser creation uses a clean **enum + switch expression** pattern:

```java
public enum BrowserType { CHROME, FIREFOX, EDGE }

private static WebDriver createBrowser(BrowserType type, boolean headless) {
    return switch (type) {
        case CHROME -> { /* ... */ yield new ChromeDriver(opts); }
        case FIREFOX -> { /* ... */ yield new FirefoxDriver(opts); }
        case EDGE -> { /* ... */ yield new EdgeDriver(opts); }
    };
}
```

### 2. BasePage — Reusable Interaction Layer

30+ methods with built-in explicit waits, automatic retry for `StaleElementReferenceException` and `ElementClickInterceptedException`, and Allure step logging:

```java
protected void click(WebElement element);
protected void setText(WebElement element, String text);
protected void selectByText(WebElement dropdown, String text);
protected void scrollToElement(WebElement element);
protected void clickByJs(WebElement element);
// ... and many more
```

### 3. Data-Driven Testing

Test data is stored in `testdata.json` with nested structure, accessed via dot-notation:

```java
DataHelper.get("login.validEmail")     // → "admin@example.com"
DataHelper.get("security.xssPayload")  // → "<script>alert('xss')</script>"
```

### 4. Dual Test Framework (TestNG + Cucumber)

- **TestNG tests** (`tests/LoginTest.java`): DDT with `@DataProvider`, `@Severity` annotations
- **Cucumber tests** (`features/login.feature`): BDD scenarios with Gherkin syntax
- Both share the same `DriverFactory`, `BasePage`, and Page Objects

### 5. Automatic Reporting (TestListener)

`BaseTest` handles only driver lifecycle. `TestListener` silently handles:
- 📸 Screenshot capture on failure
- 🎥 Video recording on failure (via Monte Screen Recorder)
- 📊 Allure environment properties generation

### 6. Flaky Test Protection (RetryAnalyzer)

Tests that fail due to network latency are automatically retried once before being marked as failed. Applied globally via `RetryListener`.

---

## 🧪 Test Cases

| # | Test Name | Type | Severity |
|---|-----------|------|----------|
| TC01 | Login successfully with valid credentials | ✅ Positive | Blocker |
| TC02 | Verify Dashboard title after login | ✅ Positive | Normal |
| TC03-TC08 | Login failure with invalid credentials (DDT) | ❌ Negative | Critical |
| TC09 | Password field should be masked | 🎨 UI | Minor |
| TC10 | Login button should be visible | 🎨 UI | Minor |
| TC11 | Login page title check | 🎨 UI | Minor |
| TC12 | SQL Injection should not bypass login | 🔒 Security | Blocker |
| TC13 | XSS payload should not bypass login | 🔒 Security | Blocker |

> **Note:** TC03-TC08 use `@DataProvider` — Allure Report counts each data row as a separate test execution.

---

## ⚙ Configuration

### `config.properties`

```properties
browser=chrome           # Options: chrome, firefox, edge
headless=false           # true = headless mode (CI/CD)
implicit.wait=10         # seconds
explicit.wait=15         # seconds
screenshot.on.fail=true  # auto-capture on test failure
video.on.fail=true       # auto-record on test failure
```

All properties can be overridden via **system properties** (CLI):

```bash
mvn test -Dbrowser=edge -Dheadless=true
```

### `testdata.json`

```json
{
  "urls": { "login": "...", "dashboard": "..." },
  "login": { "validEmail": "...", "validPassword": "..." },
  "security": { "sqlInjectionPayload": "...", "xssPayload": "..." }
}
```

---

## 📊 Allure Report

After running tests, results are saved to `target/allure-results/`.

```bash
mvn allure:serve    # Opens report in browser
```

**Report includes:**
- 📈 **Timeline** — execution duration per test
- 🔄 **Retries** — auto-retry history for flaky tests
- 📸 **Screenshots** — auto-captured on failure
- 🎥 **Videos** — screen recordings on failure (auto-deleted if test passes)
- 🏷 **Severity** — Blocker / Critical / Normal / Minor labels

---

## ❓ FAQ

### How to run tests in parallel?

Already configured! See `testng.xml`:

```xml
<suite parallel="methods" thread-count="3">
```

The entire framework is **100% thread-safe** via `ThreadLocal`.

### How to switch browsers?

Edit `config.properties`:
```properties
browser=firefox
```

Or override via CLI:
```bash
mvn test -Dbrowser=edge
```

### How to run headless on CI/CD?

```bash
mvn clean test -Dheadless=true
```

### Video recording fails on headless Linux server?

Monte Screen Recorder requires a GUI environment. Disable it:
```properties
video.on.fail=false
```

### Where are screenshots and videos stored?

| Type | Path | Allure |
|------|------|--------|
| Screenshot | `target/screenshots/` | ✅ Auto-attached |
| Video | `target/videos/` | ✅ Auto-attached |

---

## 📦 Tech Stack

| Library | Version | Role |
|---------|---------|------|
| Java | 17 | Language |
| Maven | 3.8+ | Build tool |
| Selenium | 4.27.0 | Browser automation (Selenium Manager built-in) |
| TestNG | 7.10.2 | Test framework |
| Cucumber | 7.18.0 | BDD framework |
| Allure | 2.29.0 | Test reporting |
| Log4j2 | 2.23.1 | Logging |
| Gson | 2.11.0 | JSON data reader |
| Monte Screen Recorder | 0.7.7.0 | Screen video recording |
