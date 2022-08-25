/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

/**
 * Class for the action to put an object into the store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class PutObject extends Action {

    private static final Logger LOG = Logger.getLogger(PutObject.class.getName());

    private static class FileInfo {
        File file;
        String relativePath;
        String hash;
        FileInputStream handle;

        public FileInfo(File file, String relativePath, String hash, FileInputStream handle) {
            this.file = file;
            this.relativePath = relativePath;
            this.hash = hash;
            this.handle = handle;
        }
        
    }
    
    @Override
    public String getActionName() {
        return "put";
    }

    @Override
    public List<String> getActionParams() {
        return Arrays.asList("path","object_id");
    }

    @Override
    public void run(Storage storage, List<String> parameters) throws ParseException, StorageException {
        if (parameters.size() < 2)
            throw new ParseException("Missing parameter path or object_id for action put");
        // Get path from parameter
        File sourcePath = Path.of(parameters.get(0)).toFile();
        // Get and check destination
        String id = parameters.get(1);        
        // Check if object already in store
        if (storage.existsObject(id)) {
            updateObject(storage, id,sourcePath);
        }
        else {
            createObject(storage, id,sourcePath);
           
        }
    }

    private void createObject(Storage storage, String id, File path) throws StorageException {
        // Create temporary object directory
        File tmpFolder = createTempDir();
        // Create temporary version and content directory
        File versionFolder = Path.of(tmpFolder.getPath(), "v1").toFile();
        File contentFolder = Path.of(tmpFolder.getPath(), "v1","content").toFile();
        if (!contentFolder.mkdirs())
            throw new StorageException("Problem when creating temporary content directory");
        // Create a file list
        List<FileInfo> fileInfos = listFiles(storage, path);
        // Copy files into content directory
        copyToContent(storage, fileInfos, contentFolder);
        // Create inventories
        HashMap<String,List<String>> state = new HashMap<>();
        HashMap<String,List<String>> manifest = new HashMap<>();
        for (FileInfo info : fileInfos) {
            state.putIfAbsent(info.hash, new ArrayList<>());
            manifest.putIfAbsent(info.hash, new ArrayList<>());
            state.get(info.hash).add(info.relativePath);
            manifest.get(info.hash).add(Path.of("v1", "content",info.relativePath).toString());
        }

        Inventory.Version newVersion = new Inventory.Version(
                //LocalDateTime.now().format( ) // created
                //new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZZZZZ").format(Date.from(Instant.now())) // created
                LocalDateTime.now(ZoneId.of("Z")).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                , "Initial version of object " + id //message
                , state // state
                , new Inventory.User("","mailto:") // user
        );
        HashMap<String,Inventory.Version> versions = new HashMap<>();
        versions.put("v1", newVersion);
        // Finally the inventory
        Inventory inventory = new Inventory(
                "content" // contentDirectory
                , storage.getDigestAlgorithm().toString() // digestAlgorithm
                , "v1" // head
                , id // id
                , "https://ocfl.io/1.0/spec/#inventory" // type
                , new Inventory.Fixity() // fixity
                , manifest // manifest
                , versions // versions
        );
        // Write metadata
        // Create namaste identifier for object
        try (FileOutputStream fos = new FileOutputStream(
                Path.of(tmpFolder.toString(),"0=ocfl_object_1.0").toFile())) {
            fos.write("ocfl_object_1.0\n".getBytes());
            fos.close();
        }
        catch (IOException e) {
            throw new StorageException("Exception when writing namaste object identifier",e);
        }
        // Write the inventories and thir hashes
        File versionInventoryFile = Path.of(versionFolder.getPath(),"inventory.json").toFile();
        File objectInventoryFile = Path.of(tmpFolder.getPath(),"inventory.json").toFile();
        writeInventory(storage, inventory, versionInventoryFile, versionFolder);
        writeInventory(storage, inventory, objectInventoryFile, tmpFolder);
        // Move temp files into ocfl
        try {
            File objectPath = new File(storage.getObjectPath(id));
            if (!objectPath.exists()) {
                FileUtils.moveDirectory(tmpFolder, objectPath);
            }
            else {
                throw new StorageException("Object folder already exists");
            }
        }
        catch (IOException e) {
            throw new StorageException("Exception when moving object data to the store",e);
        }
        // Cleanup
        cleanupTempDir(tmpFolder);
    }

    /**
     * Updates an existing object
     * @param storage the ocfl store
     * @param id the object id
     * @param path the path containing the data
     * @throws StorageException if there are problems during the update procedure
     */
    private void updateObject(Storage storage, String id, File path) throws StorageException {
        Inventory inventory = storage.getObjectInventory(id);
        String nextVersion =  storage.nextVersion(inventory.head);
        // Create temporary object directory
        File tmpFolder = createTempDir();
        // Create temporary version and content directory
        File versionFolder = Path.of(tmpFolder.getPath(), nextVersion).toFile();
        File contentFolder = Path.of(tmpFolder.getPath(), nextVersion,"content").toFile();
        if (!contentFolder.mkdirs())
            throw new StorageException("Problem when creating temporary content directory");
        // Create a file list
        List<FileInfo> fileInfos = listFiles(storage, path);
        // Copy files into content directory
        copyToContent(storage, fileInfos, contentFolder);
        // Update inventory
        HashMap<String,List<String>> state = new HashMap<>();
        for (FileInfo info : fileInfos) {
            state.putIfAbsent(info.hash, new ArrayList<>());
            inventory.manifest.putIfAbsent(info.hash, new ArrayList<>());
            state.get(info.hash).add(info.relativePath);
            inventory.manifest.get(info.hash)
                    .add(Path.of(nextVersion, "content",info.relativePath)
                            .toString());
        }
        inventory.head = nextVersion;
        Inventory.Version newVersion = new Inventory.Version(
                LocalDateTime.now(ZoneId.of("Z"))
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_ZONED_DATE_TIME) // created
                , "Updated version of object " + id //message
                , state // state
                , new Inventory.User("","mailto:") // user
        );
        inventory.versions.put(nextVersion,newVersion);
        // Write inventories
        File versionInventoryFile = Path.of(versionFolder.getPath(),"inventory.json").toFile();
        File objectInventoryFile = Path.of(tmpFolder.getPath(),"inventory.json").toFile();
        writeInventory(storage, inventory, versionInventoryFile, versionFolder);
        writeInventory(storage, inventory, objectInventoryFile, tmpFolder);
        // Move temp files into ocfl
        try {
            File objectPath = new File(storage.getObjectPath(id));
            File objectVersionPath = Path.of(objectPath.getPath(),nextVersion).toFile();
            if (!objectVersionPath.exists()) {
                // Version folder
                FileUtils.moveDirectory(versionFolder, objectVersionPath);
                // Inventory
                FileUtils.copyFileToDirectory(objectInventoryFile, objectPath, true);
                FileUtils.copyFileToDirectory(new File(objectInventoryFile.toString() + ".sha512"),
                        objectPath);
            }
            else {
                throw new StorageException("Version folder already exists");
            }
        }
        catch (IOException e) {
            throw new StorageException("Exception when moving object data to the store",e);
        }
        // Cleanup
        cleanupTempDir(tmpFolder);
    }

    /**
     * Creates a temporary directory
     * @return file pointing to the directory
     * @throws StorageException if creating the directory fails
     */
    private File createTempDir() throws StorageException {
        File tmpDir;
        try {
            tmpDir = File.createTempFile("ocfl", "");
            // Delete file and create directory instead
            if (!(tmpDir.delete() && tmpDir.mkdirs()))
                throw new StorageException("Problem when creating temp directory");
        }
        catch (IOException e) {
            throw new StorageException("Exception when creating temp directory", e);
        }
        return tmpDir;
    }
    
    /**
     * Cleans up a temporary folder
     * @param tmpFolder the folder to be removed
     * @throws StorageException if cleanup fails
     */
    private void cleanupTempDir(File tmpFolder) throws StorageException {
        try {
            FileUtils.deleteDirectory(tmpFolder);
        }
        catch (IOException e) {
            throw new StorageException("Exception when cleaning up temporary files", e);
        }
    }

    /**
     * Lists all relevant file info for an ocfl inventory for files in a given path
     * @param storage the ocfl store
     * @param path the file path
     * @return the list of file infos
     * @throws StorageException if listing files fails
     */
    private List<FileInfo> listFiles(Storage storage, File path) throws StorageException {
        // Create a file list
        List<FileInfo> fileInfos = new ArrayList<>();
        try {
            for (File fromFile : FileUtils.listFiles(path,FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY)) {
                FileInputStream handle = new FileInputStream(fromFile);
                String relativePath = fromFile.toString().replace(path.toString() + "/", "");
                String hash = storage.getDigestAlgorithm().hashFile(fromFile);
                fileInfos.add(new FileInfo(fromFile, relativePath, hash, handle));
            }
        }
        catch (IOException e) {
            throw new StorageException("Exception when creating file list");
        }
        return fileInfos;
    }
    
    /**
     * Copies the content of a content folder and checks file hashes afterwards
     * @param storage the ocfl store
     * @param fileInfos the infos for the files to be copied
     * @param contentFolder the content folder where the files should be stored
     * @throws StorageException if copying fails
     */
    private void copyToContent(Storage storage, List<FileInfo> fileInfos, File contentFolder) 
            throws StorageException {
        try {
            for (FileInfo fileInfo : fileInfos) {
                File toFile = Path.of(contentFolder.toString(),
                        fileInfo.relativePath).toFile();
                FileUtils.copyFile(fileInfo.file,
                        toFile,
                        true);
                // Compare three hashes: the initial file hash, the resulting file hash
                // and a backup hash based on the file handle
                String toHash = storage.getDigestAlgorithm().hashFile(toFile);
                String streamHash = storage.getDigestAlgorithm()
                        .hashStream(fileInfo.handle);
                if (!fileInfo.hash.equals(streamHash) || !fileInfo.hash.equals(toHash)) {
                    throw new StorageException("Hash mismatch after copying files to temporary content directory");
                }
            }
        }
        catch (IOException e) {
            throw new StorageException("Exception when copying files",e);
        }
    }

    /**
     * Writes an inventory and its hash to a path
     * @param storage the ocfl store
     * @param inventory the inventory
     * @param file the inventory file to be written
     * @param path the path
     * @throws StorageException if writing the files fails
     */
    private void writeInventory(Storage storage, Inventory inventory, File file, File path) throws StorageException {
        try {
            ObjectMapper om = new ObjectMapper()
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.writeValue(file, inventory);
            String hash = storage.getDigestAlgorithm().hashFile(file);
            try (FileOutputStream fos = new FileOutputStream(
                    Path.of(path.getPath(),"inventory.json.sha512").toFile())) {
                    fos.write((hash + "\tinventory.json").getBytes());
            }
        }
        catch (IOException e) {
            throw new StorageException("Exception when writing inventory files and hashes",e);
        }
    }
}
