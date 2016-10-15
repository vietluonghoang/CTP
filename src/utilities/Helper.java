/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Rypon
 */
public class Helper {

    private WebDriver driver;

    public Helper(WebDriver driver) {
        this.driver = driver;
    }

    public void cleanupWindows(String currentWindow) {
        for (String window : driver.getWindowHandles()) {
            if (!window.equals(currentWindow)) {
                driver.switchTo().window(window).close();
            }
        }
        driver.switchTo().window(currentWindow);
    }

    public void closeAllWindows() {
        for (String window : driver.getWindowHandles()) {
            driver.switchTo().window(window).close();
        }
        driver.close();
    }

    public boolean isAdAvailable(WebElement e) {
        return !(e.getCssValue("color").contains("rgba(170, 170, 170, 1)") 
                || e.getCssValue("color").contains("rgba(136, 136, 136, 1)")
                ||e.getAttribute("class").contains("disabled")
                ||e.getCssValue("display").contains("none"));
    }
}
