/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing the Hashed N-tuple Storage Layout
 * https://github.com/OCFL/extensions/blob/main/docs/0004-hashed-n-tuple-storage-layout.md
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class HashedNtupleStorageExtension implements StorageExtension {

    // The digest algorithm used for hashing
    DigestAlgorithm digestAlgorithm;
    // Number of characters in a tuple
    int tupleSize;
    // Number of tuples to be used in path
    int tupleCount;
    
    /**
     * Constructor for the Hashed N-tuple Storage Layout extension
     * @param tupleSize the number of characters in a tuple
     * @param tupleCount the number of tuples
     * @param digestAlgorithm the digest algorithm
     */
    public HashedNtupleStorageExtension(int tupleSize,int tupleCount, DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
        this.tupleSize = tupleSize;
        this.tupleCount = tupleCount;        
    }

    
    @Override
    public String getObjectPath(String id) {
        StringBuilder path = new StringBuilder();
        String hash = digestAlgorithm.hashString(id);
        for (int i = 0; i < tupleCount; i++) {
            path.append(hash.substring(i*tupleSize, i*tupleSize + tupleSize));
            path.append("/");
        }
        path.append(hash);
        return path.toString();
    }

    @Override
    public URL getUrl() throws MalformedURLException {
        return new URL("https://github.com/OCFL/extensions/blob/main/docs/0004-hashed-n-tuple-storage-layout.md");
    }

    @Override
    public String getName() {
        return "0004-hashed-n-tuple-storage-layout";
    }
    
    
}
