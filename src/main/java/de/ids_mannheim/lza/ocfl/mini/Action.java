/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.ids_mannheim.lza.ocfl.mini;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;

/**
 *
 * @author Herbert Lange <lange@ids-mannheim.de>
 */
public abstract class Action implements ActionInterface {
    public Action() {};
        
    /**
     * Runs an action based on given command line parameters
     * @param storage the storage
     * @param parameters  parameters given to the -a command line parameter
     * @throws ParseException if parsing the action fails
     * @throws StorageException if accessing the store fails
     */
    static void runAction(Storage storage, List<String> parameters) throws ParseException, StorageException {
        String actionName = parameters.get(0);
        // Use reflections to find the correct class
        Reflections refl = new Reflections(Action.class.getPackageName());        
        for (Class<? extends Action> action : refl.getSubTypesOf(Action.class)) {
            try {
            Action actionObject = (Action) action.getDeclaredConstructor().newInstance();
                if (actionObject.getActionName().equalsIgnoreCase(actionName)) {
                    actionObject.run(storage, parameters.subList(1, parameters.size()));
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                throw new ParseException("Invalid action " + actionName);
            }
        }        
    }
}
