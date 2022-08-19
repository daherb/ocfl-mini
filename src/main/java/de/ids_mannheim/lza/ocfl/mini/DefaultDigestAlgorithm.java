package de.ids_mannheim.lza.ocfl.mini;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class using SHA-512 as the default digest algorithm
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class DefaultDigestAlgorithm implements DigestAlgorithm {

    MessageDigest md;

    public DefaultDigestAlgorithm() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-512");        
    }
    
    @Override
    public String hashFile(File file) throws IOException {
        md.reset();
        DigestInputStream dis = new DigestInputStream(new FileInputStream(file), md);
        dis.on(true);
        dis.readAllBytes();
        return bytesToHexString(dis.getMessageDigest().digest());       
    }

    @Override
    public String hashString(String id) {
        md.reset();
        md.update(id.getBytes());
        return bytesToHexString(md.digest());
    }
    
    /**
     * Converts a digest into a hex string
     * @param md the message digest
     * @return the hex string
     */
    private String bytesToHexString(byte [] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte b : digest){
            sb.append(String.format("%02x",b));
        }
        return sb.toString();
    }
    
}
