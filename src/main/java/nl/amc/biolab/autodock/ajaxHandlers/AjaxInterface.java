package nl.amc.biolab.autodock.ajaxHandlers;

import nl.amc.biolab.autodock.constants.VarConfig;
import java.util.LinkedHashMap;
import nl.amc.biolab.persistencemanager.PersistenceManager;

/**
 * Interface class for all the ajax requests.
 * Create an instance of one of the extending classes and call the init() function on it to create a correct ajax response object.
 * 
 * @author Allard van Altena
 */
public abstract class AjaxInterface extends VarConfig {
    private JSONOutput JSONOBJ;
    private LinkedHashMap<String, String> PARAMS;
    private PersistenceManager PERSISTENCE;
    
    /**
     * Init function where the parameters for this ajax request and the response object are set.
     * @param params LinkedHashMap of the data that was sent with this ajax request.
     * @param response JSONOutput object where we can write the response to.
     */
    public void init(LinkedHashMap<String, String> params, JSONOutput response) {
        log.log("Init ajaxInterface.");
        
        // Get new object of the persistence manager
        _setPersistence(new PersistenceManager());
        
        // Open a session
        _getPersistence().init();
        
        _setJSONObj(response);
        _setParams(params);
        
        // Call the _run function, this is overridden in the instantiated class of this interface
        _run();
    }
    
    /**
     * Closes the persistence manager session.
     */
    protected void close() {
        _getPersistence().shutdown();
    }
    
    /**
     * Override this function in the extending class, this function is always called when instantiating the class from AjaxDispatcher.
     */
    protected abstract void _run();
    
    /**
     * Outputs the JSON string back to the client.
     * @return Boolean whether the response function was successful
     */
    public boolean getResponse() {
        close();
        
        return _getJSONObj().echo();
    }
    
    /**
     * Setter for parameter class variable.
     * @param params LinkedHashMap of the data the ajax request sent.
     */
    private void _setParams(LinkedHashMap<String, String> params) {
        PARAMS = params;
    }
    
    /**
     * Get a parameter from the parameter map.
     * @param key String of the item we are looking for in the parameter map.
     * @return Returns the value belonging to the key as a String or null if there is no such key.
     */
    protected String _getSearchTermEntry(String key) {
        log.log("sent parameters: " + _getParams());
        
        if (_getParams().containsKey(key) && _getParams().get(key).toString().length() != 0) {
            log.log("_getEntry returns: " + _getParams().get(key).toString());
            return _getParams().get(key).toString();
        }
        
        return null;
    }
    
    /**
     * Get all the parameters as a LinkedHashMap.
     * @return LinkedHashMap with all the parameters.
     */
    private LinkedHashMap<String, String> _getParams() {
        return PARAMS;
    }
    
    /**
     * Set the JSON response object.
     * @param response JSONOutput object where the response can be written to.
     */
    protected void _setJSONObj(JSONOutput response) {
        JSONOBJ = response;
    }
    
    /**
     * Get the JSONOutput object.
     * @return JSONOutput object.
     */
    protected JSONOutput _getJSONObj() {
        return JSONOBJ;
    }
    
    /**
     * Set the persistence manager class variable.
     * @param persist PersistenceManager object.
     */
    protected void _setPersistence(PersistenceManager persist) {
        PERSISTENCE = persist;
    }
    
    /**
     * Get the persistence manager object.
     * @return PersistenceManager object.
     */
    protected PersistenceManager _getPersistence() {
        return PERSISTENCE;
    }
}
