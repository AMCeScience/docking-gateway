package nl.amc.biolab.autodock.ajaxFunctions;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import docking.crappy.logger.Logger;

/**
 * Calls the processing manager client update status function and returns the
 * new status as json object
 *
 * @author Allard van Altena
 */
public class StatusUpdater extends AjaxInterface {
	public StatusUpdater() {
	}

	@Override
	protected void _run() {
		_updateStatus();
	}

	private void _updateStatus() {
		// Get processId we want to update from the ajax params
		// Long processId = new Long(_getSearchTermEntry("processing_id"));

		Logger.log("updating status...", Logger.debug);

		String newStatus = "Hello World";

		Logger.log("done", Logger.debug);

		// Output the new status to the ajax request
		_getJSONObj().add("project_id", _getSearchTermEntry("project_id"));
		_getJSONObj().add("processing_id", _getSearchTermEntry("processing_id"));
		_getJSONObj().add("new_status", newStatus);
	}
}