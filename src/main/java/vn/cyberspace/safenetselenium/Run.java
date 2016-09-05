/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author thuylh
 */
public class Run {

    final static Logger logger = Logger.getLogger(Run.class);

    public static void main(String[] args) {
        
        try{

        SafeNetFamily safenet = new SafeNetFamily();

        //Init
        safenet.init();

        //Do log in
        int status = safenet.Login();
        //if logging fails
        if(status == -1){
            System.out.println("Log in failed.");
        }else{//when logging was successful
            //Get system status
            int systemStatus = safenet.getSystemStatus();
            
            if(systemStatus == 0){
                System.out.println("Hệ thống đang tắt");
            }else if(systemStatus == 1){
                System.out.println("Hệ thống đang bật");
            }
            
            try{
                Thread.sleep(1000);
                
            }catch(InterruptedException e){
                logger.debug(e);
            }
            
            //////////////////////////////////////////////////////
            //Quét
            //
            /////////////////////////////////////////////////////
            
           // status = safenet.checkFiltering(systemStatus);
            
            ////////////////////////////////////////////////////
            //Change system status
            //
            ////////////////////////////////////////////////////
            System.out.println("Chuyển trạng thái hệ thống...");

            safenet.changeSystemStatus();

            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                logger.debug(e);
            }

            //Get new system status
            systemStatus = safenet.getSystemStatus();

            if (systemStatus == 0) {
                System.out.println("Hệ thống đang tắt");

            } else if (systemStatus == 1) {
                System.out.println("Hệ thống đang bật");

            }
            
            
        }

        safenet.destroy();
        }catch(InterruptedException e){
            System.out.println(e.toString());
        }
    }
    

}
