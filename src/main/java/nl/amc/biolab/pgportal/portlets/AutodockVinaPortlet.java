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
    private final String PROJECT_DISPLAY_PAGE = "new_job";

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
        	response.setRenderParameter("form_errors", "");
        	
            // Redirect to project display page of projects in process
        	try {
				response.sendRedirect(PROJECT_DISPLAY_PAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    // Handle view changes
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException {
        try {
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

            // Load new JSP page
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/" + NEW_JOB_PAGE + ".jsp");
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
