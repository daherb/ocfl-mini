/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Class representing an inventory  
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class Inventory {
    @JsonProperty("contentDirectory")
    String contentDirectory;
    @JsonProperty("digestAlgorithm")
    String digestAlgorithm;
    @JsonProperty("head")
    String head;
    @JsonProperty("id")
    String id;
    @JsonProperty("type")
    String type;   
    @JsonProperty("fixity")
    Fixity fixity;
    @JsonProperty("manifest")
    Map<String,List<String>> manifest;
    @JsonProperty("versions")
    Map<String,Version> versions;

    @JsonCreator
    public Inventory(
            @JsonProperty("contentDirectory") String contentDirectory, 
            @JsonProperty("digestAlgorithm")  String digestAlgorithm, 
            @JsonProperty("head")             String head, 
            @JsonProperty("id")               String id, 
            @JsonProperty("type")             String type,
            @JsonProperty("fixity")           Fixity fixity,
            @JsonProperty("manifest")         Map<String, List<String>> manifest, 
            @JsonProperty("versions")         Map<String, Version> versions
    ) {
        this.contentDirectory = contentDirectory;
        this.digestAlgorithm = digestAlgorithm;
        this.head = head;
        this.id = id;
        this.type = type;
        this.fixity = fixity;
        this.manifest = manifest;
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "Inventory{" + "contentDirectory=" + contentDirectory 
                + ", digestAlgorithm=" + digestAlgorithm 
                + ", head=" + head 
                + ", id=" + id 
                + ", type=" + type 
                + ", fixity=" + fixity 
                + ", manifest=" + manifest 
                + ", versions=" + versions + '}';
    }
    
    static class Fixity {
        
        @JsonCreator
        public Fixity() {
        }

        @Override
        public String toString() {
            return "Fixity{" + '}';
        }
        
        
    }

    static class Version {
        @JsonProperty("created")
        String created;
        @JsonProperty("message")
        String message;
        @JsonProperty("state")
        Map<String,List<String>> state;
        @JsonProperty("user")
        User user ;

        @JsonCreator
        public Version(
                @JsonProperty("created") String created, 
                @JsonProperty("message") String message, 
                @JsonProperty("state")   Map<String, List<String>> state, 
                @JsonProperty("user")    User user
        ) {
            this.created = created;
            this.message = message;
            this.state = state;
            this.user = user;
        }

        @Override
        public String toString() {
            return "Version{" 
                    + "created=" + created 
                    + ", message=" + message 
                    + ", state=" + state
                    + ", user=" + user + '}';
        }
        
        
    }

    static class User {
        @JsonProperty("name")
        String name;
        @JsonProperty("address")
        String address;

        @JsonCreator
        public User(
                @JsonProperty("name")    String name, 
                @JsonProperty("address") String address
        ) {
            this.name = name;
            this.address = address;
        }

        @Override
        public String toString() {
            return "User{" + "name=" + name + ", address=" + address + '}';
        }                
    }
}
