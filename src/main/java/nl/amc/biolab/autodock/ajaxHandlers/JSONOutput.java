package nl.amc.biolab.autodock.ajaxHandlers;

import crappy.logger.Logger;
import java.io.IOException;
import javax.portlet.ResourceResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Allard van Altena
 */
public class JSONOutput extends Logger {
    private JSONObject JSONObj;
    private ResourceResponse RESPONSE;
    
    public JSONOutput(ResourceResponse response) {
        _setJSONObj(new JSONObject());
        _setResponseObj(response);
    }
    
    public void add(String key, Object val) {
        if (val.toString().length() < 4000) {
            log.log("Adding value " + key + " " + val);
        }
        
        _getJSONObj().put(key, val);
    }
    
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
    
    public String getAsString() {
        return _getJSONObj().toJSONString();
    }
    
    public void setWholeObj(JSONObject obj) {
        JSONObj = obj;
    }
    
    private void _setJSONObj(JSONObject obj) {
        JSONObj = obj;
    }
    
    private JSONObject _getJSONObj() {
        return JSONObj;
    }
    
    private void _setResponseObj(ResourceResponse response) {
        RESPONSE = response;
    }
    
    private ResourceResponse _getResponseObj() {
        return RESPONSE;
    }
}
