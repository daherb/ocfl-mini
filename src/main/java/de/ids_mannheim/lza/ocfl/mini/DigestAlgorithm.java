/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for hashing both files and strings
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public interface DigestAlgorithm {
    
    /**
     * Computes hash for a file
     * @param file the file to be hashed
     * @return the file hash
     * @throws IOException if hashing the file fails
     */
    String hashFile(File file) throws IOException;
    
    /**
     * Computes hash for an input stream
     * @param stream the stream to be hashed
     * @return the hash
     * @throws IOException if hashing the stream fails
     */
    String hashStream(InputStream stream) throws IOException;
    
    /**
     * Computes hash for a string
     * @param id the string to be hashed
     * @return the string hash
     */
    String hashString(String id);
    
}
