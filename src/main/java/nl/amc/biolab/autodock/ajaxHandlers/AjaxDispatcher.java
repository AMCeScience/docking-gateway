package nl.amc.biolab.autodock.ajaxHandlers;

import nl.amc.biolab.autodock.constants.VarConfig;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import nl.amc.biolab.autodock.output.tools.Downloader;
import nl.amc.biolab.autodock.output.tools.LigandCollector;
import nl.amc.biolab.autodock.output.tools.SearchProjects;
import nl.amc.biolab.autodock.projectFunctions.StatusUpdater;
import nl.amc.biolab.persistencemanager.PersistenceManager;

/**
 *
 * @author Allard
 */
public class AjaxDispatcher extends VarConfig {
    private AjaxInterface AJAXOBJ;
            
    public AjaxDispatcher() {}
    
    public void dispatch(ResourceRequest ajaxParameters, ResourceResponse response) {
        PersistenceManager persist = new PersistenceManager();
        persist.init();
        persist.initStuff(ajaxParameters.getRemoteUser());
        persist.shutdown();
        
        LinkedHashMap params = _processParams(ajaxParameters);
        
        String callFunction = params.get("callFunction").toString();
        
        // Init AjaxInterface as null
        _setAjaxObj(null);
        
        log.log(callFunction);
        
        if (callFunction.equals("doSearch")) {
            _setAjaxObj(new SearchProjects());
        }
        
        if (callFunction.equals("getLigands")) {
            _setAjaxObj(new LigandCollector());
        }
        
        if (callFunction.equals("updateStatus")) {
            _setAjaxObj(new StatusUpdater());
        }
        
        if (callFunction.equals("doDownload")) {
            _setAjaxObj(new Downloader());
        }
        
        // Check if AjaxInterface was set
        if (_getAjaxObj() != null) {
            // Initiate AjaxInterface response object
            _getAjaxObj().init(params, new JSONOutput(response));
        }
    }
    
    public void response() {
        log.log("Writing response."); 
       
        if(!_getAjaxObj().getResponse()) {
            log.log("Ajax message not set.");
        }
    }
    
    private LinkedHashMap _processParams(ResourceRequest params) {
        // Create return map
        LinkedHashMap paramMap = new LinkedHashMap();
        // Get names of parameters passed by ajax request
        Enumeration<String> names = params.getParameterNames();
        
        String name;
        
        // Loop over names
        while (names.hasMoreElements()) {
            // Get single name
            name = names.nextElement();
            // Get parameter value belonging to name and put in return map
            paramMap.put(name, params.getParameter(name).toString());
        }
        
        // Add extra parameters
        paramMap.put("callFunction", params.getResourceID().toString());
        paramMap.put("liferay_user", params.getRemoteUser());
        
        return paramMap;
    }
    
    private void _setAjaxObj(AjaxInterface interfaceOb) {
        AJAXOBJ = interfaceOb;
    }
    
    private AjaxInterface _getAjaxObj() {
        return AJAXOBJ;
    }
}
