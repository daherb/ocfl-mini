/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

/**
 * Class for the action to get an object from the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class GetObject extends Action {

    private static final Logger LOG = Logger.getLogger(GetObject.class.getName());

    @Override
    public String getActionName() {
        return "get";
    }

    @Override
    public List<String> getActionParams() {
        return Arrays.asList("object_id", "path");
    }

    @Override
    public void run(Storage storage, List<String> parameters) throws StorageException, ParseException {
        if (parameters.size() < 2)
            throw new ParseException("Missing parameter object_id or path for action get");
        // Get id from parameter
        String id = parameters.get(0);
        // Get inventory for id
        Inventory objectInventory = storage.getObjectInventory(id);
        // Get and check destination
        File destinationPath = new File(parameters.get(1));
        if (destinationPath.exists())
            throw new ParseException("Destination path " + destinationPath + " already exists");
        // Get path to the object
        String objectPath = storage.getObjectPath(id);
        // Access head
        String head = objectInventory.head;
        File headPath = Path.of(objectPath, head).toFile();
        File headInventoryFile = Path.of(headPath.getPath(), "inventory.json").toFile();
        Inventory headInventory = storage.readInventory(headInventoryFile);
        // Get file lists from inventories
        Map<String,List<String>> sourceFiles = objectInventory.manifest;
        Map<String,List<String>> destinationFiles = headInventory.versions.get(head).state;
        // Copy all source files to testination
        for (String hash : sourceFiles.keySet()) {
            if (sourceFiles.get(hash).size() != destinationFiles.get(hash).size()) {
                throw new StorageException("Mismatch between source and destination count for copy operation");
            }
            for (int i = 0 ; i < sourceFiles.get(hash).size(); i++) {
                if (!sourceFiles.get(hash).get(i).endsWith(destinationFiles.get(hash).get(i))) {
                    throw new StorageException("Mismatch between source and destination file name for copy operation");
                }
                File fromFile = Path.of(objectPath,sourceFiles.get(hash).get(i)).toFile();
                File toFile = Path.of(destinationPath.getPath(),destinationFiles.get(hash).get(i)).toFile();

                try {
                    LOG.log(Level.INFO, "Copy from {0} to {1}",
                            new String[]{fromFile.toString(), toFile.toString()});
                    FileUtils.copyFile(fromFile, toFile, true);
                    // Check hash after copy
                    try {
                        String newHash = storage.getDigestAlgorithm().hashFile(toFile);
                        if (!hash.equals(newHash)) {
                            throw new StorageException(String.format("Invalid hash for destination file. "
                                    + "Expected %s but got %s.",hash,newHash));
                        }
                    }
                    catch (IOException e) {
                        throw new StorageException("Problem when hashing destination file " + toFile,e);
                    }
                }
                catch (IOException e) {
                    throw new StorageException("Problem when copying from " + fromFile + " to " + toFile,e);
                }
            }
        }
    }
    
}
