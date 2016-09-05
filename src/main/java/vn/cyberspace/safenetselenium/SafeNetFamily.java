/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import vn.cyberspace.safenetselenium.Utils.ConfigFile;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import vn.cyberspace.safenetselenium.safenetfiltering.SafeNetFiltering;

/**
 *
 * @author thuylh
 */
public class SafeNetFamily {

    private ConfigFile config = new ConfigFile("web.properties");
    private FirefoxDriver driver = null;
    private final Logger logger = Logger.getLogger(this.getClass());

    //Open web-browser and navigate to website
    public void init() throws InterruptedException {

        ProfilesIni prof = new ProfilesIni();
        FirefoxProfile ffProfile = prof.getProfile("FireFoxProfile");
//        ffProfile.setAcceptUntrustedCertificates(false);
        //      ffProfile.setAssumeUntrustedCertificateIssuer(false);

        driver = new FirefoxDriver(ffProfile);

        String Controller_URL = config.getConfig("Controller_URL");

        driver.manage()
                .window().maximize();
        driver.navigate()
                .to(Controller_URL);
        //driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        Thread.sleep(
                2000);
    }

    //Input: no param
    //Output: 
    //          0
    //          1
    //          -1
    //
    //          -2
    //Action: Log into web
    public int Login() {

        String username = config.getConfig("username");
        String password = config.getConfig("password");

        String username_field_id = config.getConfig("username_field_id");
        String password_field_id = config.getConfig("password_field_id");
        String login_button_id = config.getConfig("login_button_id");

        String username_field_xpath = config.getConfig("username_field_xpath");
        String password_field_xpath = config.getConfig("password_field_xpath");
        String login_button_xpath = config.getConfig("login_button_xpath");

        String msg_result_xpath = config.getConfig("msg_result_xpath");
        String invalid_login_msg = config.getConfig("invalid_login_msg");

        //Click to show login form
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.debug(e);
            return -1;
        }
        String login_form_button_xpath = config.getConfig("login_form_button_xpath");
        if (login_form_button_xpath != "") {
            driver.findElement(By.xpath(login_form_button_xpath)).click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.debug(e);
                return -1;
            }
        } else {
            logger.debug("Can't get log in form.");
            return -1;
        }

        //Send username to GUI
        if (username_field_id != "") {
            driver.findElement(By.id(username_field_id)).clear();
            driver.findElement(By.id(username_field_id)).sendKeys(username);
        } else if (username_field_xpath != "") {
            driver.findElement(By.xpath(username_field_xpath)).clear();
            driver.findElement(By.xpath(username_field_xpath)).sendKeys(username);
        } else {
            logger.debug("Can't get username field.");
            return -1;
        }

        //Send password to GUI
        if (password_field_id != "") {
            driver.findElement(By.id(password_field_id)).clear();
            driver.findElement(By.id(password_field_id)).sendKeys(password);
        } else if (password_field_xpath != "") {
            driver.findElement(By.xpath(password_field_xpath)).clear();
            driver.findElement(By.xpath(password_field_xpath)).sendKeys(password);
        } else {
            logger.debug("Can't get password field.");
            return -1;
        }

        //Click Login button
        if (login_button_id != "") {
            driver.findElement(By.id(login_button_id)).click();
        } else if (login_button_xpath != "") {
            driver.findElement(By.xpath(login_button_xpath)).click();
        } else {
            logger.debug("Can't find login button.");
            return -1;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.debug(e);
            return -1;
        }

        int found = driver.findElements(By.xpath(msg_result_xpath)).size();
        if (found == 1) {
            String msg_result = driver.findElement(By.xpath(msg_result_xpath)).getText();

            if (invalid_login_msg.equals(msg_result)) {
                System.out.println("Đăng nhập thất bại!");
                return -1;
            } else {
                return -2;
            }
        }else
        return 0;
    }

    //Input: no param
    //Output:   -1  error
    //          0   off
    //          1   on    
    public int getSystemStatus() {
        String on_off_switch_button_xpath = config.getConfig("on_off_switch_button_xpath");
        String off_pattern = config.getConfig("off_pattern");
        String on_pattern = config.getConfig("on_pattern");

        String status = driver.findElement(By.xpath(on_off_switch_button_xpath)).getAttribute("class");

        if (status.indexOf(on_pattern) != -1) {
            return 1;
        } else if (status.indexOf(off_pattern) != -1) {
            return 0;
        } else {
            return -1;
        }

    }

    //Input: no param
    //Output: 
    //Action: click on status switch
    public int changeSystemStatus() {

        String on_off_switch_button_xpath = config.getConfig("on_off_switch_button_xpath");
        driver.findElement(By.xpath(on_off_switch_button_xpath)).click();

        return 0;
    }

    //Input: system status
    //Output:   0: FAILED
    //          1: PASSED
    //         -1: ERROR
    //Action: click on status switch
    public int checkFiltering(int status) {

        SafeNetFiltering filter = new SafeNetFiltering();

        try {
            filter.run("websites.txt");
        } catch (FileNotFoundException | InterruptedException | ExecutionException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        //Create array to store result domain list
        List<String> unblocked_Domains = new ArrayList<String>();
        List<String> blocked_Domains = new ArrayList<String>();
        List<String> nonExisted_Domains = new ArrayList<String>();

        try {
            
            String unblocked_Domains_filename = config.getConfig("unblocked_Domains");
            String blocked_Domains_filename = config.getConfig("blocked_Domains");
            String nonExisted_Domains_filename = config.getConfig("nonExisted_Domains");
            
            
            unblocked_Domains = SafeNetFiltering.readFromFile(unblocked_Domains_filename);
            blocked_Domains = SafeNetFiltering.readFromFile(blocked_Domains_filename);
            nonExisted_Domains = SafeNetFiltering.readFromFile(nonExisted_Domains_filename);

            switch (status) {
                case 0: //OFF
                    //In case: system is off
                    if (blocked_Domains.size() == 0) {
                        return 1;
                    } else {
                        return 0;
                    }

                case 1: //ON
                    //In case: system is on
                    if (unblocked_Domains.size() == 0) {
                        return 1;
                    } else {
                        return 0;
                    }

                default:
                    return -1;
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return -1;
        }

    }

    //Input: system status
    //Output:   0: FAILED
    //          1: PASSED
    //         -1: ERROR
    //Action: click on status switch
    public int enableAllCategories() {

        return -1;
    }

    public void destroy() {
        driver.quit();
        config.destroy();
    }

}
