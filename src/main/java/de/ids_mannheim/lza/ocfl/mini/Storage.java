/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Class representing the OCFL store
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class Storage {

    private final Logger LOG = Logger.getLogger(Storage.class.getName());

    // default ocfl version
    private static final String OCFL_VERSION_STRING = "1.0";

    public static class Builder {

        private final String root;

        private String version = Storage.OCFL_VERSION_STRING;
        private StorageExtension storageExtension = new DefaultStorageExtension();
        private DigestAlgorithm digestAlgorithm;
        private final Set<Extension> extensions = new HashSet();

        /**
         * Builder constructor
         * @param root the storage root
         * @throws NoSuchAlgorithmException if setting the digest algorithm fails
         */
        public Builder(String root) throws NoSuchAlgorithmException {
            this.digestAlgorithm = new DefaultDigestAlgorithm();        
            this.root = root;  
        }

        /**
         * Sets the ocfl version to be used
         * @param version the ocfl version
         * @return the builder
         */
        public Storage.Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets the storage extension to be used
         * @param storageExtension the storage extension
         * @return the builder
         */
        public Storage.Builder setStorageExtension(StorageExtension storageExtension) {
            this.storageExtension = storageExtension;
            return this;
        }

        /**
         * Sets the digest algorithm to be used
         * @param digestAlgorithm the digest algorithm
         * @return the builder
         */
        public Storage.Builder setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
            this.digestAlgorithm = digestAlgorithm;
            return this;
        }

        /**
         * Adds an additional extension
         * @param extension the extension
         * @return the builder
         */
        public Storage.Builder addExtension(Extension extension) {
            this.extensions.add(extension);
            return this;
        }

        /**
         * Builds a storage
         * @return the storage
         * @throws StorageException if initializing the storage fails
         */
        public Storage build() throws StorageException {
            return new Storage(root, version, storageExtension, digestAlgorithm, extensions);
        }
    }
    // File object to keep track of the store
    File storageRoot;
    // The current ocfl version
    String ocflVersion;
    // The current storage extension
    private final StorageExtension storageExtension;
    // The current digest algorithm    
    private final DigestAlgorithm digestAlgorithm;
    // The set of all loaded extensions
    private final Set<Extension> extensions = new HashSet<>();

    public Storage(String root, String version, StorageExtension storageExtension, 
            DigestAlgorithm digestAlgorithm, Set<Extension> extensions) throws StorageException {
        this.storageRoot = new File(root);
        this.ocflVersion = version;
        this.storageExtension = storageExtension;
        this.digestAlgorithm = digestAlgorithm;
        this.extensions.add(storageExtension);
        this.extensions.addAll(extensions);
        
        if (!storageRoot.exists()) {
            try {
                init();
            } catch (IOException ex) {
                throw new StorageException("Problem when initializing storage root", ex);
            }
        }
        else if (!storageRoot.isDirectory()) {
            throw new StorageException("Storage root does exist but is not "+
                    "a directory");
        }
    }

    /**
     * Getter for the digest algorithm
     * @return the reference to the digest algorithm
     */
    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * Initializes a new storage root
     * @param root the root directory
     */
    private void init() throws StorageException, IOException {
        storageRoot.mkdirs();
        // Create files in the root
        // First the ocfl info following NAMASTE
        String dvalue = "ocfl_" + ocflVersion;
        File ocflInfoFile = new File(storageRoot.getPath() + "/0=" + dvalue);
        if (ocflInfoFile.createNewFile()) {
            String content = dvalue + "\n";
            try (FileOutputStream fos = new FileOutputStream(ocflInfoFile)) {
                fos.write(content.getBytes());
            }
        }
        else
            throw new StorageException("Problem creating file 0=ocfl_" 
                    + ocflVersion +  " in storage root " + storageRoot.toString());
        // Download the matching ocfl specification
        try {
            URL url = new URL("https://raw.githubusercontent.com/OCFL/spec/main/" 
                    + ocflVersion + "/spec/index.html");
            try (FileOutputStream fos = new FileOutputStream(storageRoot + "/" 
                    + dvalue + ".html")) {
                url.openStream().transferTo(fos);                
            }
        }
        catch(FileNotFoundException e) {
            throw new StorageException("Problem downloading OCFL spec",e);
        }
        try {
        for (Extension e : extensions) {
            try (FileOutputStream fos = new FileOutputStream(
                    Path.of(storageRoot.getPath(), e.getName() + ".md").toString())
            ) {
                e.getUrl().openStream().transferTo(fos);
            }
        }
        }
        catch (IOException e) {
            throw new StorageException("Exception when downloading extension specs", e);
        }
    }

    /**
     * Returns the file pointing to the current storage root
     * @return the file pointing to the storage root
     */
    public File getStorageRoot() {
        return storageRoot;
    }

    /**
     * Returns the current ocfl version
     * @return the current ocfl version
     */
    public String getOcflVersion() {
        return ocflVersion;
    }

    /**
     * Lists all top-level object inventory files in the store
     * @return the list of inventory files
     */
    public List<File> listObjectInventories() {
        return new ArrayList<>(FileUtils.listFiles(storageRoot,
            new NameFileFilter("inventory.json"),
            new AndFileFilter(Arrays.asList(
                DirectoryFileFilter.DIRECTORY,
                new NotFileFilter(
                    new WildcardFileFilter("v*")
                    )
                ))
            ));
    }
    
    /**
     * Lists all object ids and head version for all objects in the store
     * @return the list of inventory files
     * @throws StorageException if reading inventories fails
     */
    public List<ObjectInfo> listObjects() throws StorageException {
        List<ObjectInfo> objects = new ArrayList<>();        
        for (File inventoryFile : listObjectInventories()) {
            Inventory inventory = readInventory(inventoryFile);
            objects.add(new ObjectInfo(inventory.id,inventory.head));
        }
        return objects;
    }

    /**
     * Reads an inventory from a file
     * @param inventoryFile the inventory file
     * @return the resulting inventory
     * @throws StorageException if reading the inventory fails
     */
    public Inventory readInventory(File inventoryFile) throws StorageException {
        ObjectMapper om = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return om.readValue(inventoryFile, Inventory.class);            
        }
        catch (IOException e) {
            throw new StorageException("Problem reading object inventories",e);
        }
    }

    /**
     * Gets the object inventory for an object id
     * @param id the object id
     * @return the object inventory
     * @throws StorageException  if accessing the inventory fails
     */
    public Inventory getObjectInventory(String id) throws StorageException {
        String path = getObjectPath(id);        
        return readInventory(Path.of(path,"inventory.json").toFile());
    }

    /**
     * Computes hash 
     * @param id
     * @return 
     */
    public String hashId(String id) {
        return digestAlgorithm.hashString(id);
    }

    /**
     * Returns the file system path for an object
     * @param id the object id
     * @return the path containing the ocfl object
     */
    public String getObjectPath(String id) {
        return Path.of(storageRoot.getPath(),storageExtension.getObjectPath(id)).toString();
    }

    /**
     * Checks if object is already in store
     * @param id the object id
     * @return true if object already exists, false otherwise
     */
    public boolean existsObject(String id) throws StorageException {
        return listObjects().stream().anyMatch((o) -> id.equals(o.getId()));
    }
    
    /**
     * Computes the next object version
     * @param version the current version
     * @return the next version
     */
    public String nextVersion(String version) {
        return "v" + String.valueOf(Integer.parseInt(version.replace("v", ""))+1);
    }
}
