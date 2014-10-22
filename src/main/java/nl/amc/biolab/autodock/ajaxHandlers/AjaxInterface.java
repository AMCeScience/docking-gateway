package nl.amc.biolab.autodock.ajaxHandlers;

import java.util.LinkedHashMap;

import nl.amc.biolab.exceptions.PersistenceException;
import nl.amc.biolab.persistencemanager.PersistenceManagerPlugin;
import docking.crappy.logger.Logger;

/**
 * Interface class for all the ajax requests.
 * Create an instance of one of the extending classes and call the init() function on it to create a correct ajax response object.
 * 
 * @author Allard van Altena
 */
public abstract class AjaxInterface {
    private JSONOutput JSONOBJ;
    private LinkedHashMap<String, String> PARAMS;
    private PersistenceManagerPlugin PERSISTENCE;
    
    /**
     * Init function where the parameters for this ajax request and the response object are set.
     * @param params LinkedHashMap of the data that was sent with this ajax request.
     * @param response JSONOutput object where we can write the response to.
     */
    public void init(LinkedHashMap<String, String> params, JSONOutput response) {
        Logger.log("Init ajaxInterface", Logger.debug);
        
        // Get new object of the persistence manager
        _setPersistence(new PersistenceManagerPlugin());

        // Open a session
        try {
			_getPersistence().init();
			
			_setJSONObj(response);
	        _setParams(params);
	        
	        // Call the _run function, this is overridden in the instantiated class of this interface
	        _run();
		} catch (PersistenceException e) {
			Logger.log(e, Logger.exception);
		} finally {
			// Close the session
	        _getPersistence().shutdown();
		}
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
     * Add one parameter to the parameter map
     * @param key String value of the key for this param
     * @param value String value of the value for this param
     */
    protected void _addParam(String key, String value) {
    	if (!PARAMS.isEmpty()) {
    		PARAMS.put(key, value);
    	}
    }
    
    /**
     * Get a parameter from the parameter map.
     * @param key String of the item we are looking for in the parameter map.
     * @return Returns the value belonging to the key as a String or null if there is no such key.
     */
    protected String _getSearchTermEntry(String key) {
        if (_getParams().containsKey(key) && _getParams().get(key).toString().length() != 0) {
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
    protected void _setPersistence(PersistenceManagerPlugin persist) {
        PERSISTENCE = persist;
    }
    
    /**
     * Get the persistence manager object.
     * @return PersistenceManager object.
     */
    protected PersistenceManagerPlugin _getPersistence() {
        return PERSISTENCE;
    }
}
