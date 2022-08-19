/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.util.List;
import org.apache.commons.cli.ParseException;

/**
 * Interface representing OCFL actions
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public interface ActionInterface {
    /**
     * Gets the name of the action
     * @return the name of the action
     */
    String getActionName();
    
    /**
     * Gets the expected parameters for the action
     * @return the list of parameter names
     */
    List<String> getActionParams();
    
    /**
     * Runs the action given the parameters
     * @param storage the storage
     * @param parameters the action parameters
     * @throws ParseException if expected parameters are missing
     * @throws StorageException if there are problems accessing the store
     */
    void run(Storage storage, List<String> parameters) throws ParseException, StorageException;
}
