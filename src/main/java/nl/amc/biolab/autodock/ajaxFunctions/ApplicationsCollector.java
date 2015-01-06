package nl.amc.biolab.autodock.ajaxFunctions;

import java.util.List;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.datamodel.objects.Application;
import nl.amc.biolab.persistencemanager.PersistenceManagerPlugin;

public class ApplicationsCollector extends AjaxInterface {
    public ApplicationsCollector() {}
    
    @Override
    protected void _run() {
        _getApplicationsObject();
    }
    
    private void _getApplicationsObject() {
    	PersistenceManagerPlugin pm = new PersistenceManagerPlugin();
    	
    	pm.init();
    	
    	List<Application> apps = pm.get.applications();
    	
    	for (Application app : apps) {
    		_getJSONObj().add(app.getName(), app.getDescription());
    	}
    }
}
