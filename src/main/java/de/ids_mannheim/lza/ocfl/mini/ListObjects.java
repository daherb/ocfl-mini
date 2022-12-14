/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class for the action to get an object from the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class ListObjects extends Action {

    private static final Logger LOG = Logger.getLogger(ListObjects.class.getName());
    
    @Override
    public String getActionName() {
        return "list";
    }

    @Override
    public List<String> getActionParams() {
        return new ArrayList<>();
    }

    @Override
    public void run(Storage storage, List<String> parameters) throws StorageException {
        for (ObjectInfo object : storage.listObjects()) {
            System.out.println(String.format("{%s,%s}", object.getId(),object.getVersion()));
        }
    }
}
