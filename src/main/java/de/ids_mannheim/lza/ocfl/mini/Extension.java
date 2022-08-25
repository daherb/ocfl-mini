/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generic interface for extensions
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public interface Extension {
    
    /**
     * The url for the extension spec
     * @return the url to the spec
     * @throws MalformedURLException if constructing the url fails
     */
    abstract URL getUrl() throws MalformedURLException;
    
    /**
     * The name of the extension
     * @return the name of the extension
     */
    abstract String getName();
}
