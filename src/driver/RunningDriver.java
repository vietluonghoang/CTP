/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author Rypon
 */
public class RunningDriver {
    
    public WebDriver firefoxDriver(){
        DesiredCapabilities capabilites = new DesiredCapabilities();
        WebDriver driver = new FirefoxDriver(capabilites);
        return driver;
    }
}
