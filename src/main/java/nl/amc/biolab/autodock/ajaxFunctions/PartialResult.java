package nl.amc.biolab.autodock.ajaxFunctions;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;

public class PartialResult extends AjaxInterface {
	public PartialResult() {
		
	}
	
	@Override
	protected void _run() {
		_getPartialResult();
	}

	private void _getPartialResult() {
		// Get processId we want to update from the ajax params
		// Long processId = new Long(_getSearchTermEntry("processing_id"));

		// Output the new status to the ajax request
		_getJSONObj().add("project_id", _getSearchTermEntry("project_id"));
		_getJSONObj().add("processing_id", _getSearchTermEntry("processing_id"));
	}
}