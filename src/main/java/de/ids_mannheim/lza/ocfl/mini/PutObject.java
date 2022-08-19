/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.util.Arrays;
import java.util.List;

/**
 * Class for the action to put an object into the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class PutObject extends Action {

    @Override
    public String getActionName() {
        return "put";
    }

    @Override
    public List<String> getActionParams() {
        return Arrays.asList("path","object_id");
    }

    @Override
    public void run(Storage storage, List<String> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
