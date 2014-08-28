package nl.amc.biolab.autodock.ajaxFunctions;

import java.util.List;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;

/**
 * Gets error details from database and returns the message as json object
 *
 * @author Allard van Altena
 */
public class SubmissionDetails extends AjaxInterface {
    public SubmissionDetails() {}
    
    @Override
	protected void _run() {
		_getDetails();
	}
    
    public void _getDetails() {
    	Long submissionId = new Long(_getSearchTermEntry("submission_id"));
    	
        // Get the updated status from the database
        List<nl.amc.biolab.datamodel.objects.Error> errors = _getPersistence().get.submission(submissionId).getErrors();
        
        String description = "";
        String message = "";
        
        if (errors.size() > 0) {
        	description = errors.get(errors.size() - 1).getDescription();
        	message = errors.get(errors.size() - 1).getMessage();
        }
        
        _getJSONObj().add("description", description);
        _getJSONObj().add("message", message);
    }
}
