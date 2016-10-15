/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import interfaces.CTP;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.Helper;

/**
 *
 * @author Rypon
 */
public class Neobux implements CTP {

    private WebDriver driver;
    private String homeUrl = "https://www.neobux.com/";
    private String advertismentPageUrl;
    private String currentWindow;
    private Helper helper;
    private String username;
    private String password;
    private JTextArea logger;

    public Neobux(WebDriver driver) {
        this.driver = driver;
    }

    public Neobux(WebDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    public Neobux(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getHomeUrl() {
        return this.homeUrl;
    }

    @Override
    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    @Override
    public String getAdvertismentPageUrl() {
        return this.advertismentPageUrl;
    }

    @Override
    public void setAdvertismentPageUrl(String advertismentPageUrl) {
        this.advertismentPageUrl = advertismentPageUrl;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setDriver(WebDriver driver) {
        this.driver = driver;
        driver.manage().window().setSize(new Dimension(400, 300));
    }

    private String getCurrentWindow() {
        if (this.currentWindow == null) {
            this.currentWindow = driver.getWindowHandle();
        }
        return this.currentWindow;
    }

    private Helper getHelper() {
        if (this.helper == null) {
            this.helper = new Helper(driver);
        }
        return this.helper;
    }

    @Override
    public void login(String username, String pwd) {
        String captcha = "";
        driver.get(homeUrl);
        driver.findElement(By.linkText("Login")).click();
        boolean correct = false;
        while (!correct) {
            driver.findElement(By.id("Kf1")).sendKeys(username);
            driver.findElement(By.id("Kf2")).sendKeys(pwd);
            if (driver.findElements(By.id("Kf3")).size() > 0) {
                if (driver.findElement(By.id("Kf3")).isDisplayed()) {
                    captcha = JOptionPane.showInputDialog("Captcha for " + homeUrl);
                    driver.findElement(By.id("Kf3")).sendKeys(captcha);
                }
            }
            driver.findElement(By.id("botao_login")).click();

            if (driver.findElements(By.xpath("//table[@id='ubar_w1']")).size() > 0) {
                correct = true;
                appendLog("--- Logged in ---");
            }
        }
//        this.homeUrl = driver.getCurrentUrl();
        this.advertismentPageUrl = driver.findElement(By.linkText("View Advertisements")).getAttribute("href");
        appendLog("home: " + homeUrl);
        appendLog("ads page: " + advertismentPageUrl);
        appendLog("-----");
    }

    private void openAdsPage() {
        appendLog("--- Opening Ads page.... ---");
        getHelper().cleanupWindows(getCurrentWindow());
        while (!driver.getCurrentUrl().equals(this.advertismentPageUrl)) {
            appendLog("Opening Ads page \n\t" + advertismentPageUrl + "\nbut another page is opening\n\t" + driver.getCurrentUrl());
            driver.get(advertismentPageUrl);
        }
        appendLog("--- Ads Page opened ---");
    }

    private ArrayList getAllAds() {
        openAdsPage();
        ArrayList<String> links = new ArrayList<String>();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] target = {
            "//table[@id=\"tl\"]/tbody/tr/td/div[1]/div/div[2]/div",
            "//table[@id=\"tl\"]/tbody/tr/td/div[3]/div/div[2]/div",
            "//table[@id=\"tl\"]/tbody/tr/td/div[5]/div/div[2]/div"
        };
        for (String str : target) {
            for (WebElement e : driver.findElements(By.xpath(str))) {
                if (getHelper().isAdAvailable(e.findElement(By.xpath("./table/tbody/tr[1]/td/div[1]/div[1]/a")))) {
                    e.click();
                    links.add(e.findElement(By.xpath("./table/tbody/tr[1]/td/div[2]/span/a")).getAttribute("href"));
                }
            }
        }
        appendLog("--- Checking Ads.... --- \nAvailable Ads: " + links.size() + "\n-----");
        return links;
    }

    @Override
    public void openAd() {
        openAdsPage();
        ArrayList<String> links = getAllAds();
        for (String link : links) {
            int timeout = 30;
            appendLog("-- Opening Ad: " + link);
            driver.get(link);
            while (timeout > 0) {
                List<WebElement> elements = null;
                try {
                    elements = driver.findElements(By.xpath("//*[@id='frtp']/td/table/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr/td[2]"));
                } catch (UnhandledAlertException ex) {
                    appendLog("\tError: UnhandledAlertException");
                    elements = driver.findElements(By.xpath("//*[@id='frtp']/td/table/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr/td[2]"));
                }
                if (elements.size() > 0) {
                    WebElement e = elements.get(0);
                    if (e.getText().length() < 5 || e.getText().contains("Waiting for the advertisement to load")) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        timeout--;
                    } else if (e.getText().contains("validated") || e.getText().contains("expired") || e.getText().contains("already")) {
                        break;
                    } else {
                        driver.navigate().refresh();
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    timeout--;
                    if (timeout < (timeout / 3)) {
                        driver.navigate().refresh();
                    }
                }
            }
        }
    }

    public void openAdPrize() {
        openAdsPage();
        for (WebElement e : driver.findElements(By.xpath("//td[@id='ap_ctr']/div"))) {
            appendLog("--- Checking Prize.... ---");
            if (getHelper().isAdAvailable(e)) {
                appendLog("Available Prize: " + e.findElement(By.xpath("./table[2]/tbody/tr/td[2]")).getText() + "\n-----");
                e.click();
                for (String window : driver.getWindowHandles()) {
                    if (!getCurrentWindow().equals(window)) {
                        driver.switchTo().window(window);
                        int timeout = 30;
                        while (timeout > 0) {
                            List<WebElement> elements = null;
                            try {
                                elements = driver.findElements(By.xpath("//*[@id='frtp']/td/table/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr/td[2]"));
                            } catch (UnhandledAlertException ex) {
                                appendLog("\tError: UnhandleAlertException");
                                elements = driver.findElements(By.xpath("//*[@id='frtp']/td/table/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr/td[2]"));
                            }
                            if (elements.size() > 0) {
                                WebElement el = elements.get(0);
                                if (el.getText().length() < 5 || el.getText().contains("Waiting for the advertisement to load")) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    timeout--;
                                } else if (el.getText().contains("validated") || el.getText().contains("expired") || el.getText().contains("already")) {
                                    if (driver.findElements(By.id("nxt_bt_td")).size() > 0) {
                                        if (driver.findElement(By.id("nxt_bt_td")).isDisplayed()) {
                                            driver.findElement(By.id("nxt_bt_td")).click();
                                        } else {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                    timeout = 30;
                                } else {
                                    driver.navigate().refresh();
                                }
                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                timeout--;
                            }
                        }
                    }
                }
            } else {
                appendLog("Prize is not available\n-----");
            }
        }
    }

    private void appendLog(String log) {
        if (logger == null) {
            System.out.println("No logger available for:\n\t" + homeUrl + "\n\t" + username + " - " + password);
        } else {
            logger.append("\n" + log);
        }
    }

    @Override
    public void login() {
        login(this.username, this.password);
    }

    @Override
    public void openCustomAds() {
        openAdPrize();
    }

    @Override
    public void closeAll() {
        getHelper().closeAllWindows();
    }

    @Override
    public void setLogger(JTextArea taLog) {
        this.logger = taLog;
    }

}
