package nl.amc.biolab.autodock.ajaxFunctions;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;

/**
 * Basic error class, creates the response object so we can output an error message back to the client
 * 
 * @author Allard van Altena
 */
public class AjaxError extends AjaxInterface {
    public AjaxError() {}
    
    @Override
	protected void _run() {
		_addError();
	}
    
    public void _addError() {
        _getJSONObj().add("error", true);
        _getJSONObj().add("message", "there was an ajax error");
    }
}