/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.io.IOException;

/**
 * Exception class for OCFL storage handling
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class StorageException extends IOException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        String str = super.toString();
        if (this.getCause() != null) {
            str += ", cause: " + this.getCause().toString();
        }
        return str;
    }
    
    
}
