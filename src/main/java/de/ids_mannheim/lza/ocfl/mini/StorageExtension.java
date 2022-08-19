/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

/**
 * Interface defining basic storage properties
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public interface StorageExtension {
    
    /**
     * Returns a path for the object id
     * @param id the object id
     * @return the object path
     */
    String getObjectPath(String id);
}
