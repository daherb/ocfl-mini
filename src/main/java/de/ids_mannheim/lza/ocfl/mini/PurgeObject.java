/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

/**
 * Class for the action to purge an object from the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class PurgeObject extends Action {

    @Override
    public String getActionName() {
        return "purge";
    }

    @Override
    public List<String> getActionParams() {
        return Arrays.asList("object_id");
    }

    @Override
    public void run(Storage storage, List<String> parameters) throws ParseException, StorageException {
        if (parameters.size() <1)
            throw new ParseException("Missing parameter object_id for action info");
        String id = parameters.get(0);
        String path = storage.getObjectPath(id);
        try {
            FileUtils.deleteDirectory(new File(storage.storageRoot + "/" + path));
        } catch (IOException ex) {
            throw new StorageException("Problem when purging object " + id, ex);
        }
    }
    
}
