/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

/**
 * Class for default storage extension using object id as path, potentially 
 * splitting of a prefix which is basically equivalent to
 * https://github.com/OCFL/extensions/blob/main/docs/0006-flat-omit-prefix-storage-layout.md
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class DefaultStorageExtension implements StorageExtension {

    String prefix = "";
    
    /**
     * Constructor without a prefix
     */
    public DefaultStorageExtension() {}
    
    /**
     * Constructor with a prefix
     * @param prefix the prefix to split the object id on
     */
    public DefaultStorageExtension(String prefix) {        
        this.prefix = prefix;
    }    
    
    @Override
    public String getObjectPath(String id) {
        // No prefix -> just the id
        if (prefix.isBlank())
            return id;
        else {
            // Split on prefix and return final part
            String[] splitted = id.split(prefix);
            return splitted[splitted.length-1];
        }
    }
    
}
