/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium;

import vn.cyberspace.safenetselenium.Utils.ConfigFile;

/**
 *
 * @author thuylh
 */
public abstract class WebFrontEnd {
    
    ConfigFile config = new ConfigFile("web.config");
    
    public void init(){        
        
    }
    
    abstract int Login(String username, String password, String role);
}
