package nl.amc.biolab.autodock.ajaxHandlers;

import java.util.Enumeration;
import java.util.LinkedHashMap;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import nl.amc.biolab.autodock.ajaxFunctions.AjaxError;
import docking.crappy.logger.Logger;

/**
 * This class handles calling the appropriate class for the ajax request and passes on the ajax parameters.
 * 
 * @author Allard van Altena
 */
public class AjaxDispatcher {
    private AjaxInterface AJAXOBJ;
            
    public AjaxDispatcher() {}
    
    /**
     * Finds and sets the correct ajax class the client is requesting and then fires it by calling the init function
     * @param ajaxParameters ResourceRequest with ajax parameters
     * @param response ResourceResponse object to which we can output the JSON response
     */
    public void dispatch(ResourceRequest ajaxParameters, ResourceResponse response) {
    	boolean error = false;
    	String error_msg = "";
        LinkedHashMap<String, String> params = _processParams(ajaxParameters);
        
        String callFunction = params.get("callFunction").toString();
        
        // Init AjaxInterface as null
        _setAjaxObj(null);
        
        Logger.log(callFunction, Logger.debug);
        
        // Get called for class, catch errors
        try {
        	@SuppressWarnings("unchecked")
			Class<AjaxInterface> call = (Class<AjaxInterface>) Class.forName("nl.amc.biolab.autodock.ajaxFunctions." + callFunction);
        	
        	_setAjaxObj(call.newInstance());
        } catch(ClassNotFoundException e) {
        	error_msg = e.toString();
			error = true;
        } catch (InstantiationException e) {
        	error_msg = e.toString();
			error = true;
		} catch (IllegalAccessException e) {
			error_msg = e.toString();
			error = true;
		}
        
        // Check if AjaxInterface was set
        if (_getAjaxObj() != null && !error) {
            // Initiate AjaxInterface response object
            _getAjaxObj().init(params, new JSONOutput(response));
        } else {
        	_setAjaxObj(new AjaxError());
        	_getAjaxObj().init(params, new JSONOutput(response));
        	_getAjaxObj()._getJSONObj().add("error_val", error_msg);
        	
        	Logger.log(error_msg, Logger.error);
        }
    }
    
    /**
     * Writes the ajax response to the client
     */
    public void response() {
        Logger.log("Writing response.", Logger.debug); 
       
        if(!_getAjaxObj().getResponse()) {
            Logger.log("Ajax message not set.", Logger.error);
        }
    }
    
    /**
     * Maps the ResourceRequest parameters to a LinkedHashMap so it is easier to get the parameters 
     * @param params ResourceRequest from client with the ajax parameters
     * @return LinkedHashMap with the mapped parameters, parameters are saved as: <name, value>
     */
    private LinkedHashMap<String, String> _processParams(ResourceRequest params) {
        // Create return map
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        // Get names of parameters passed by ajax request
        Enumeration<String> names = params.getParameterNames();
        
        String name;
        
        // Loop over names
        while (names.hasMoreElements()) {
            // Get single name
            name = names.nextElement();
            
            // Get parameter value belonging to name and put in return map
            paramMap.put(name, params.getParameter(name).toString());
        }
        
        // Add extra parameters
        paramMap.put("callFunction", params.getResourceID().toString());
        paramMap.put("liferay_user", params.getRemoteUser());
        
        return paramMap;
    }
    
    /**
     * Set the AjaxInterface for this class
     * @param interfaceOb The AjaxInterface we want to set
     */
    private void _setAjaxObj(AjaxInterface interfaceOb) {
        AJAXOBJ = interfaceOb;
    }
    
    /**
     * Get the current AjaxInterface object
     * @return AjaxInterface object set to the class variable
     */
    private AjaxInterface _getAjaxObj() {
        return AJAXOBJ;
    }
}
