package nl.amc.biolab.autodock.ajaxHandlers;

import java.io.IOException;

import javax.portlet.ResourceResponse;

import nl.amc.biolab.autodock.constants.VarConfig;

import org.json.simple.JSONObject;

/**
 * JSON class which makes communication with the client easier, set a ResourceResponse object and add data to the JSONObject to communicate
 *
 * @author Allard van Altena
 */
public class JSONOutput extends VarConfig {
    private JSONObject JSONObj;
    private ResourceResponse RESPONSE;
    
    /**
     * Constructor which sets a new JSON object to the class variable and adds the input ResourceResponse object where the JSON output should go to
     * @param response ResourceResponse where the class can output its JSON
     */
    public JSONOutput(ResourceResponse response) {
        _setJSONObj(new JSONObject());
        _setResponseObj(response);
    }
    
    /**
     * Add value to JSON object
     * @param key Key of this entry
     * @param val Value of this entry
     */
    @SuppressWarnings("unchecked")
	public void add(String key, Object val) {
        _getJSONObj().put(key, val);
    }
    
    /**
     * Outputs the JSON response to the client through the ResourceResponse object
     * @return Boolean whether if the function succeeded in outputting the response
     */
    public boolean echo() {        
        if (_getJSONObj().toString().length() < 4000) {
            log.log("writing response " + _getJSONObj().toString());
        }

        try {
            if (!_getJSONObj().toString().isEmpty()) {
                // Output json string back to ajax call
                _getResponseObj().setContentType("json");
                _getResponseObj().resetBuffer();
                _getResponseObj().getWriter().print(_getJSONObj().toString());
                _getResponseObj().flushBuffer();
                
                return true;
            }
        } catch(IOException e) {
            log.log(e);
        }
        
        return false;
    }
    
    /**
     * Gets the JSON object as a string
     * @return JSON object as a string
     */
    public String getAsString() {
        return _getJSONObj().toJSONString();
    }
    
    /**
     * Overwrite the current JSON object with new input
     * @param obj New JSON object
     */
    public void setWholeObj(JSONObject obj) {
        _setJSONObj(obj);
    }
    
    /**
     * Set new JSON object from input
     * @param obj New JSON object
     */
    private void _setJSONObj(JSONObject obj) {
        JSONObj = obj;
    }
    
    /**
     * Get the current JSON object
     * @return Current JSON object
     */
    private JSONObject _getJSONObj() {
        return JSONObj;
    }
    
    /**
     * Set the ResourceResponse class variable
     * @param response ResourceResponse object
     */
    private void _setResponseObj(ResourceResponse response) {
        RESPONSE = response;
    }
    
    /**
     * Get the ResourceResponse class variable
     * @return ResourceResponse object
     */
    private ResourceResponse _getResponseObj() {
        return RESPONSE;
    }
}
