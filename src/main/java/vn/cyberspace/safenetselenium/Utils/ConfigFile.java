/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author thuylh
 */
public class ConfigFile {
    
    private final Logger logger = Logger.getLogger(this.getClass());
    private InputStream input = null;
    
    private Properties prop = new Properties();

    public ConfigFile(String webconfig) {
        try {
            input = new FileInputStream(webconfig);
            prop.load(input);
            
        } catch (IOException e) {
            logger.debug(e);
        }
    }
    
    public String getConfig(String property) {
        String result;
        
        result = prop.getProperty(property) != null ? prop.getProperty(property) : "";
        
        return result;
    }
    
    public void setConfig(String property, String value) {
        prop.put(property, value);
    }
    
    public void destroy() {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            logger.debug(e);
        }
        
    }
    
}
