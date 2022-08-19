/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Class for the action to get an object from the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class GetObject extends Action {

    @Override
    public String getActionName() {
        return "get";
    }

    @Override
    public List<String> getActionParams() {
        return Arrays.asList("object_id", "path");
    }   

    @Override
    public void run(Storage storage, List<String> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
