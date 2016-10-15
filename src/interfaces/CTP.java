/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import javax.swing.JTextArea;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Rypon
 */
public interface CTP {

    public String getHomeUrl();

    public void setHomeUrl(String homeUrl);

    public String getAdvertismentPageUrl();

    public void setAdvertismentPageUrl(String advertismentPageUrl);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public void setDriver(WebDriver driver);

    public void login();

    public void login(String username, String pwd);

    public void openAd();

    public void openCustomAds();
    
    public void setLogger(JTextArea taLog);

    public void closeAll();
}
