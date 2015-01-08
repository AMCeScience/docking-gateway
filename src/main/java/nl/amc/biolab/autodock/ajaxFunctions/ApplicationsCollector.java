package nl.amc.biolab.autodock.ajaxFunctions;

import java.util.List;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.datamodel.objects.Application;
import nl.amc.biolab.persistencemanager.PersistenceManagerPlugin;

import org.json.simple.JSONObject;

public class ApplicationsCollector extends AjaxInterface {
    public ApplicationsCollector() {}
    
    @Override
    protected void _run() {
        _getApplicationsObject();
    }
    
    @SuppressWarnings("unchecked")
	private void _getApplicationsObject() {
    	PersistenceManagerPlugin pm = new PersistenceManagerPlugin();
    	
    	pm.init();
    	
    	List<Application> apps = pm.get.applications();
    	
    	for (Application app : apps) {
    		JSONObject obj = new JSONObject();
    		
    		obj.put("name", app.getName());
    		obj.put("description", app.getDescription());
    		
    		_getJSONObj().add(Long.toString(app.getDbId()), obj);
    	}
    }
}
