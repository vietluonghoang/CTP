/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import driver.RunningDriver;
import gui.TargetDashboard;
import interfaces.CTP;

/**
 *
 * @author Rypon
 */
public class TargetRunner implements Runnable {

    private CTP target;
    private TargetDashboard parent;

    public TargetRunner(CTP target, TargetDashboard parent) {
        this.target = target;
        this.parent = parent;
    }

    @Override
    public void run() {
        target.setDriver(new RunningDriver().firefoxDriver());
        target.login();
        while (parent.keepRunning()) {
            target.openAd();
            target.openCustomAds();
        }
        target.closeAll();
    }

}
