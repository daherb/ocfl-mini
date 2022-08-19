/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.ParseException;

/**
 * Class for the action to get info about an object in the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class GetObjectInfo extends Action {

    @Override
    public String getActionName() {
        return "info";
    }

    @Override
    public List<String> getActionParams() {
        return Arrays.asList("object_id");
    }

    @Override
    public void run(Storage storage, List<String> parameters) throws ParseException, StorageException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
