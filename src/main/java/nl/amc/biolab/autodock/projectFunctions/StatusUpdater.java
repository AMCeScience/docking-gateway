package nl.amc.biolab.autodock.projectFunctions;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.nsg.pm.ProcessingManagerClient;

/**
 *
 * @author Allard van Altena
 */
public class StatusUpdater extends AjaxInterface {
    public StatusUpdater() {}
    
    @Override
    protected void _run() {
        _updateStatus();
    }
    
    private void _updateStatus() {        
        ProcessingManagerClient client = new ProcessingManagerClient(config.getProcessingWSDL());
        
        // Get processId we want to update from the ajax params
        Long processId = new Long(_getSearchTermEntry("project_id"));
        
        // Update the status through the processingmanager webservice
        client.updateStatus(processId);
        
        // Get the updated status from the database
        String newStatus = _getPersistence().getProcessing(processId).getSubmissions().iterator().next().getStatus();
        
        // Output the new status to the ajax request
        _getJSONObj().add("new_status", newStatus);
    }
}
