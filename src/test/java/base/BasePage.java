package base;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.LogHelper;

import java.util.List;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final int MAX_RETRIES = 3;

    public BasePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForVisible(WebElement element, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(By locator, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean waitForInvisible(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected boolean waitForInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    protected void waitForTitleContains(String titlePart) {
        wait.until(ExpectedConditions.titleContains(titlePart));
    }

    private void retryOperation(Runnable operation, String operationName) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                operation.run();
                return;
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                LogHelper.warn(operationName + " failed due to " + e.getClass().getSimpleName() + ". Retrying... (" + (attempts + 1) + "/" + MAX_RETRIES + ")");
                attempts++;
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Failed to perform " + operationName + " after " + MAX_RETRIES + " attempts.");
    }

    @Step("Click on element")
    protected void click(WebElement element) {
        LogHelper.info("Clicking on element: " + element);
        retryOperation(() -> waitForClickable(element).click(), "click");
    }

    @Step("Click on locator: {locator}")
    protected void click(By locator) {
        LogHelper.info("Clicking on locator: " + locator);
        retryOperation(() -> waitForClickable(locator).click(), "click");
    }

    @Step("Set text: {text}")
    protected void setText(WebElement element, String text) {
        LogHelper.info("Setting text: " + text);
        retryOperation(() -> {
            waitForVisible(element).clear();
            element.sendKeys(text);
        }, "setText");
    }

    @Step("Set text: {text} to locator: {locator}")
    protected void setText(By locator, String text) {
        LogHelper.info("Setting text: " + text + " to locator: " + locator);
        retryOperation(() -> {
            WebElement el = waitForVisible(locator);
            el.clear();
            el.sendKeys(text);
        }, "setText");
    }

    @Step("Clear text in element")
    protected void clearText(WebElement element) {
        LogHelper.info("Clearing text in element");
        waitForVisible(element).clear();
    }

    protected void pressKey(WebElement element, Keys key) {
        LogHelper.info("Pressing key: " + key.name());
        element.sendKeys(key);
    }

    protected void pressEnter(WebElement element) {
        LogHelper.info("Pressing ENTER");
        element.sendKeys(Keys.ENTER);
    }

    protected void pressTab(WebElement element) {
        LogHelper.info("Pressing TAB");
        element.sendKeys(Keys.TAB);
    }

    @Step("Set checkbox to: {checked}")
    protected void setCheckbox(WebElement checkbox, boolean checked) {
        LogHelper.info("Setting checkbox to: " + checked);
        waitForClickable(checkbox);
        if (checkbox.isSelected() != checked) {
            checkbox.click();
        }
    }

    @Step("Select dropdown by text: {text}")
    protected void selectByText(WebElement dropdown, String text) {
        LogHelper.info("Selecting dropdown by text: " + text);
        new Select(waitForVisible(dropdown)).selectByVisibleText(text);
    }

    @Step("Select dropdown by value: {value}")
    protected void selectByValue(WebElement dropdown, String value) {
        LogHelper.info("Selecting dropdown by value: " + value);
        new Select(waitForVisible(dropdown)).selectByValue(value);
    }

    @Step("Select dropdown by index: {index}")
    protected void selectByIndex(WebElement dropdown, int index) {
        LogHelper.info("Selecting dropdown by index: " + index);
        new Select(waitForVisible(dropdown)).selectByIndex(index);
    }

    protected String getSelectedText(WebElement dropdown) {
        return new Select(waitForVisible(dropdown)).getFirstSelectedOption().getText();
    }

    protected String getText(WebElement element) {
        return waitForVisible(element).getText().trim();
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected String getAttribute(WebElement element, String attribute) {
        return waitForVisible(element).getAttribute(attribute);
    }

    protected String getCssValue(WebElement element, String property) {
        return element.getCssValue(property);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return waitForVisible(element).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isEnabled(WebElement element) {
        try {
            return waitForVisible(element).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isSelected(WebElement element) {
        return element.isSelected();
    }

    protected void hoverElement(WebElement element) {
        LogHelper.info("Hovering over element");
        new Actions(driver).moveToElement(waitForVisible(element)).perform();
    }

    protected void doubleClick(WebElement element) {
        LogHelper.info("Double clicking element");
        new Actions(driver).doubleClick(waitForClickable(element)).perform();
    }

    protected void rightClick(WebElement element) {
        LogHelper.info("Right clicking element");
        new Actions(driver).contextClick(waitForClickable(element)).perform();
    }

    protected void dragAndDrop(WebElement source, WebElement target) {
        LogHelper.info("Dragging and dropping element");
        new Actions(driver).dragAndDrop(source, target).perform();
    }

    protected void scrollToElement(WebElement element) {
        LogHelper.info("Scrolling to element");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @Step("Click on element using JS")
    protected void clickByJs(WebElement element) {
        LogHelper.info("Clicking element via JS");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    @Step("Set text: {text} using JS")
    protected void setTextByJs(WebElement element, String text) {
        LogHelper.info("Setting text via JS: " + text);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1];", element, text);
    }

    protected Object executeJs(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    @Step("Accept alert")
    protected void acceptAlert() {
        LogHelper.info("Accepting alert");
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    @Step("Dismiss alert")
    protected void dismissAlert() {
        LogHelper.info("Dismissing alert");
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }

    protected String getAlertText() {
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();
    }

    protected void switchToFrame(WebElement frame) {
        LogHelper.info("Switching to frame");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
    }

    protected void switchToFrame(int index) {
        LogHelper.info("Switching to frame by index: " + index);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
    }

    protected void switchToFrame(String nameOrId) {
        LogHelper.info("Switching to frame by name/id: " + nameOrId);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
    }

    protected void switchToDefaultContent() {
        LogHelper.info("Switching to default content");
        driver.switchTo().defaultContent();
    }

    protected String getWindowHandle() {
        return driver.getWindowHandle();
    }

    protected void switchToWindow(String handle) {
        LogHelper.info("Switching to window: " + handle);
        driver.switchTo().window(handle);
    }

    protected void switchToNewWindow() {
        LogHelper.info("Switching to new window");
        String currentHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected int countElements(By locator) {
        return driver.findElements(locator).size();
    }

    @Step("Navigate to: {url}")
    protected void navigateTo(String url) {
        LogHelper.info("Navigating to URL: " + url);
        driver.get(url);
    }

    protected void refreshPage() {
        LogHelper.info("Refreshing page");
        driver.navigate().refresh();
    }

    protected void goBack() {
        LogHelper.info("Navigating back");
        driver.navigate().back();
    }

    protected void goForward() {
        LogHelper.info("Navigating forward");
        driver.navigate().forward();
    }

    @Step("Wait for page load to complete")
    public void waitForPageLoad() {
        LogHelper.info("Waiting for page load to complete");
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }
}
