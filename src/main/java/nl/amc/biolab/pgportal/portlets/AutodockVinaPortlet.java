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
import crappy.logger.Logger;

/**
 * @author Allard van Altena
 */
public class AutodockVinaPortlet extends GenericPortlet {
    private final String NEW_JOB_PAGE = "new_job";
    private final String PROJECT_DISPLAY_PAGE = "project_display";
    private Logger LOG = new Logger();

    public AutodockVinaPortlet() {}
    
    // Handle job form submission
    @ProcessAction(name = "submitJobForm")
    public void handleSubmitJobForm(ActionRequest formParameters, ActionResponse response) {
    	LOG.log("submitJobForm");
        
        FormSubmitter submit = new FormSubmitter();
        
        boolean success = submit.saveForm(formParameters);
        
        if (!success) {
            // Set errors and reload form page    
            response.setRenderParameter("form_errors", submit.getErrors());
            response.setRenderParameter("nextJSP", NEW_JOB_PAGE);
            
            LOG.log(submit.getErrors());
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
            	LOG.log("checking user");
            	
            	UserConfigurator userConfig = new UserConfigurator();
            	
            	if (!userConfig.checkUser(request.getRemoteUser())) {
            		LOG.log("user setup");
            		
            		userConfig.setupUser(request.getRemoteUser());
            	}
            	
            	// Close db session
            	userConfig.close();
            }

            // Load new JSP page
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/".concat(nextJSP).concat(".jsp"));
            dispatcher.include(request, response);
        } catch (IOException e) {
            LOG.log(e);
        }
    }
    
    // Handle ajax calls
    @Override
    public void serveResource(ResourceRequest ajaxParameters, ResourceResponse response) throws PortletException {
    	LOG.log("serveResource");
    	
        AjaxDispatcher dispatch = new AjaxDispatcher();
        
        // Init ajax
        dispatch.dispatch(ajaxParameters, response);
        
        // Get and write JSON response
        dispatch.response();
    }
}
