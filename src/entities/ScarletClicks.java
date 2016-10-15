/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import interfaces.CTP;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.StaticImageScreenRegion;
import utilities.Helper;

/**
 *
 * @author Rypon
 */
public class ScarletClicks implements CTP {

    private WebDriver driver;
    private String homeUrl = "http://www.scarlet-clicks.info/";
    private String advertismentPageUrl;
    private String currentWindow;
    private Helper helper;
    private String username;
    private String password;
    private JTextArea logger;

    public ScarletClicks(WebDriver driver, String username, String password) {
        this.driver = driver;
        this.username = username;
        this.password = password;
    }

    public ScarletClicks(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ScarletClicks(WebDriver driver) {
        this.driver = driver;
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

    private void appendLog(String log) {
        if (logger == null) {
            System.out.println("No logger available for:\n\t" + homeUrl + "\n\t" + username + " - " + password);
        } else {
            logger.append("\n" + log);
        }
    }

    @Override
    public void login() {
        login(username, password);
    }

    @Override
    public void login(String username, String pwd) {
        String captcha = "";
        driver.get(homeUrl);

        boolean correct = false;
        while (!correct) {
            if (driver.findElements(By.linkText("Login")).size() > 0 && driver.findElements(By.id("l2loader2loginform")).size() < 1) {
                appendLog("--- Logging in.... ---");
                driver.findElement(By.linkText("Login")).click();
                driver.findElement(By.xpath("//*[@id=\"loginform\"]/table/tbody/tr[1]/td[2]/input")).sendKeys(username);
                driver.findElement(By.xpath("//*[@id=\"loginform\"]/table/tbody/tr[2]/td[2]/input")).sendKeys(pwd);
                if (driver.findElements(By.id("captcha_login")).size() > 0) {
                    if (driver.findElement(By.id("captcha_login")).isDisplayed()) {
                        captcha = JOptionPane.showInputDialog("Captcha for " + homeUrl);
                        driver.findElement(By.id("captcha_login")).sendKeys(captcha);
                    }
                }
                driver.findElement(By.xpath("//*[@id=\"loginform\"]/table/tbody/tr[5]/td[2]/input")).click();
                while (driver.findElements(By.id("l2loader2loginform")).size() > 0) {
                    try {
//                        if (driver.findElement(By.id("l2loader2loginform")).isDisplayed()) {
                        Thread.sleep(3000);
//                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ScarletClicks.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception e) {
                        Logger.getLogger(ScarletClicks.class.getName()).log(Level.SEVERE, null, e);
                    }

                }
            }
            while (driver.findElements(By.id("progress")).size() > 0) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ScarletClicks.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (driver.findElements(By.linkText("Logout")).size() > 0) {
                correct = true;
                appendLog("--- Logged in ---");
            }
        }
        this.advertismentPageUrl = driver.findElement(By.linkText("View Ads")).getAttribute("href");
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
            "//div[contains(@class,'ad-block')]"
        };
        for (String str : target) {
            for (WebElement e : driver.findElements(By.xpath(str))) {
                if (getHelper().isAdAvailable(e)) {
                    links.add(e.findElement(By.xpath("./div[@class='ad-title']/span")).getAttribute("onclick").split("'")[1]);
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
            driver.get(homeUrl + link);
            while (timeout > 0) {
                List<WebElement> elements = null;
                try {
                    elements = driver.findElements(By.xpath("//div[@id='surfbar' and not(contains(@style,'display: none'))]"));
                } catch (UnhandledAlertException ex) {
                    appendLog("\tError: UnhandledAlertException");
                    elements = driver.findElements(By.xpath("//div[@id='surfbar' and not(contains(@style,'display: none'))]"));
                }
                if (elements.size() > 0) {
                    WebElement e = elements.get(0);
                    if (e.getText().contains("Wait please") || (e.findElements(By.xpath("./div/div[@id='progressbar' and not(contains(@style,'display: none'))]")).size() > 0)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        timeout--;
                    } else if (e.getText().contains("Thanks for watching!") || e.getText().contains("Invalid Advertisement") || e.getText().contains("already visited")) {
                        break;
                    } else if (e.findElements(By.xpath("./div[@id='focusoff' and not(contains(@style,'display: none'))]")).size() > 0) {
                        appendLog("--- Off Focus...... ---");
                        driver.switchTo().window(getCurrentWindow());
                        e.findElement(By.xpath("./a")).click();
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
                } else if (driver.findElements(By.xpath("//div[@id='vnumbers' and not(contains(@style,'display: none'))]")).size() > 0) {
                    try {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Neobux.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        WebElement element = driver.findElement(By.xpath("//*[@id=\"vnumbers\"]/table/tbody/tr/td[2]"));
                        Point point = element.getLocation();
                        File ss = (File) ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                        BufferedImage src = ImageIO.read(ss);
                        BufferedImage screen = src.getSubimage(point.getX(), point.getY(), element.getSize().getWidth(), element.getSize().getHeight());
                        StaticImageScreenRegion region = new StaticImageScreenRegion(screen);
                        boolean found = false;
                        for (File file : new File("./src/imgs").listFiles()) {
                            if (file.isFile()) {
                                for (ScreenRegion match : region.findAll(new ImageTarget(ImageIO.read(file)))) {
                                    found = true;

                                    System.out.println("---location: " + point + " - " + element.getSize());
                                    System.out.println("---match: " + match.getCenter());
                                    System.out.println("-order: " + ((match.getCenter().getX() / 50) + 1));
                                    System.out.println("- target: " + (point.getX() + 50 + match.getCenter().getX()));
                                    new Actions(driver).moveToElement(element, 0, 0).moveByOffset(match.getCenter().getX(), match.getCenter().getY()).click().perform();
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                        if (!found) {
                            ImageIO.write(screen, "PNG", new File("./src/imgs/detected/notfound-" + link.substring(link.lastIndexOf("=") + 1) + ".png"));
                        }
//                        break;
                    } catch (Exception ex) {
                        Logger.getLogger(ScarletClicks.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public void openCustomAds() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLogger(JTextArea taLog) {
        this.logger = taLog;
    }

    @Override
    public void closeAll() {
        getHelper().closeAllWindows();
    }

}
