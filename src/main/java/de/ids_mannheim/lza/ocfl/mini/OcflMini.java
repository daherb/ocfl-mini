/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package de.ids_mannheim.lza.ocfl.mini;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;

/**
 *
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public class OcflMini {
    
    private final Logger logger = Logger.getGlobal();
    
    private Storage ocflStore;
        
    public OcflMini(String root) {
        try {
            // Open or create the ocfl store
            ocflStore = new Storage.Builder(root).build();
        } catch (StorageException | NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, "Exception when initializing storage", ex);
        }
    }
    
    public static void main(String[] args) {
        // Get description of all supported actions
        Reflections refl = new Reflections(Action.class.getPackageName());       
        ArrayList<Class<? extends Action>> actions = 
                new ArrayList<>(refl.getSubTypesOf(Action.class));
        // Sort actions by name
        actions.sort((t, t1) -> {
            return t.getCanonicalName().compareTo(t1.getCanonicalName());
        });        
        StringBuilder description = new StringBuilder("The action to be executed:\n");
        for (Class<? extends Action> action : actions) {
            try {                
                Action actionObject = (Action) action.getDeclaredConstructor().newInstance();
                description.append(String.format("\t%s %s",
                        actionObject.getActionName(),
                        actionObject.getActionParams().stream()
                                .map((p) -> "<" + p + ">")
                                .collect(Collectors.joining(" "))
                ));
            }
            catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                Logger.getGlobal().log(Level.SEVERE, "Exception when listing actions",e);
            }
        }
        // Define command line uptions
        Options options = new Options();
        options.addOption(Option.builder("r")
                .argName("ocfl-root")
                .required()
                .desc("The storage root for OCFL")
                .longOpt("ocfl-root")
                .hasArg()
                .build());
        options.addOption(Option.builder("a")
                .argName("action")
                .required()
                .hasArgs()
                .desc(description.toString())
                .longOpt("ocfl-root")
                .build());
        try {
            CommandLine cl = DefaultParser.builder().build().parse(options, args);
            OcflMini mini = new OcflMini(cl.getOptionValue("r"));
            mini.doAction(cl.getOptionValues("a"));
            
        } catch (ParseException ex) {
            new HelpFormatter().printHelp("ocfl-mini", options);
            System.exit(1);
        }
        catch (StorageException ex) {
            System.err.println("Exception encountered\n");
            ex.printStackTrace();
        }
    }

    private void doAction(String[] actionInfo) throws ParseException, StorageException {
       Action.runAction(ocflStore, Arrays.asList(actionInfo));
    }
}
