package nl.amc.biolab.pgportal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxDispatcher;
import nl.amc.biolab.autodock.input.tools.FormSubmitter;
import nl.amc.biolab.autodock.input.tools.UserConfigurator;
import nl.amc.biolab.exceptions.PersistenceException;
import docking.crappy.logger.Logger;

/**
 * @author Allard van Altena
 */
public class AutodockVinaPortlet extends GenericPortlet {
    private final String NEW_JOB_PAGE = "new_job";
    private final String PROJECT_DISPLAY_PAGE = "project_display";

    public AutodockVinaPortlet() {}
    
    // Handle job form submission
    @ProcessAction(name = "submitJobForm")
    public void handleSubmitJobForm(ActionRequest formParameters, ActionResponse response) {
    	Logger.log("submitJobForm", Logger.debug);
        
        FormSubmitter submit = new FormSubmitter();
        boolean success = false;
        
		try {
			success = submit.saveForm(formParameters);
		} catch (PersistenceException e) {
			Logger.log(e, Logger.exception);
		}
        
        if (!success) {
            // Set errors and reload form page    
            response.setRenderParameter("form_errors", submit.getErrors());
            response.setRenderParameter("nextJSP", NEW_JOB_PAGE);
            
            Logger.log(submit.getErrors(), Logger.error);
        } else {
            // Redirect to project display page of projects in process
            response.setRenderParameter("page_type", "in_process");
            response.setRenderParameter("nextJSP", PROJECT_DISPLAY_PAGE);
        }
    }

    // Handle menu clicks
    @ProcessAction(name = "goToPage")
    public void handleGoToPage(ActionRequest request, ActionResponse response) {
        String nextJSP = (String) request.getParameter("page");
        String page_type = (String) request.getParameter("page_type");
        
        // Clear errors
        response.setRenderParameter("form_errors", "");
        
        // Set page type if available
        response.setRenderParameter("page_type", page_type);

        // Set next page to load
        response.setRenderParameter("nextJSP", nextJSP);
    }

    // Handle view changes
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException {
        try {
            String nextJSP = (String) request.getParameter("nextJSP");
            
            // If no page is set load the new job page
            if (nextJSP == null) {
        		nextJSP = NEW_JOB_PAGE;
            }
            
            if (nextJSP.equals(NEW_JOB_PAGE)) {
            	Logger.log("checking user", Logger.debug);
            	
            	UserConfigurator userConfig = null;
            	
				try {
					userConfig = new UserConfigurator();
					
					if (!userConfig.checkUser(request.getRemoteUser())) {
	            		Logger.log("user setup", Logger.debug);
	            		
	            		userConfig.setupUser(request.getRemoteUser());
	            	}
				} catch (PersistenceException e) {
					Logger.log(e, Logger.exception);
				} finally {
					// Close db session
	            	userConfig.close();
				}
            }

            // Load new JSP page
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/".concat(nextJSP).concat(".jsp"));
            dispatcher.include(request, response);
        } catch (IOException e) {
        	Logger.log(e, Logger.exception);
        }
    }
    
    // Handle ajax calls
    @Override
    public void serveResource(ResourceRequest ajaxParameters, ResourceResponse response) throws PortletException {
    	Logger.log("serveResource", Logger.debug);
    	
        AjaxDispatcher dispatch = new AjaxDispatcher();
        
        // Init ajax
        dispatch.dispatch(ajaxParameters, response);
        
        // Get and write JSON response
        dispatch.response();
    }
}
