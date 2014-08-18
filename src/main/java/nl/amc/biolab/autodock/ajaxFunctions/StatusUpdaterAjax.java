package nl.amc.biolab.autodock.ajaxFunctions;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;

/**
 * Calls the processing manager client update status function and returns the new status as json object
 *
 * @author Allard van Altena
 */
public class StatusUpdaterAjax extends AjaxInterface {
    public StatusUpdaterAjax() {}
    
    @Override
    protected void _run() {
        _updateStatus();
    }
    
    private void _updateStatus() {
        // Get processId we want to update from the ajax params
        Long processId = new Long(_getSearchTermEntry("processing_id"));
        
        log("updating status...");
        
        // Update status
        StatusUpdater status = new StatusUpdater();
        String newStatus = status.updateStatus(processId);
        
        log("done");
        
        // Output the new status to the ajax request
        _getJSONObj().add("project_id", _getSearchTermEntry("project_id"));
        _getJSONObj().add("processing_id", _getSearchTermEntry("processing_id"));
        _getJSONObj().add("new_status", newStatus);
    }
}
