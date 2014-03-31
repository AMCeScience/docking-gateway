package nl.amc.biolab.pgportal.portlets.asm;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxDispatcher;
import java.io.IOException;
import javax.portlet.*;
import nl.amc.biolab.autodock.input.tools.FormSubmitter;

/**
 * @author Allard van Altena
 */
public class AutodockVinaPortlet extends GenericPortlet {
    private final String NEW_JOB_PAGE = "new_job";
    private final String PROJECT_DISPLAY_PAGE = "project_display";

    public AutodockVinaPortlet() {}

    @ProcessAction(name = "submitJobForm")
    public void handleSubmitJobForm(ActionRequest formParameters, ActionResponse response) {
        /* Form fields:
         * project_name, project_description, receptor_file, < required
         * center_x, center_y, center_z,                     < required
         * size_x, size_y, size_z,                           < required
         * number_runs, exhaustiveness, energy_range,
         * libraries,                                        < required
         * run_pilot
         */
        
        String userId = formParameters.getRemoteUser();
        
        FormSubmitter submit = new FormSubmitter(userId);
        
        boolean success = submit.saveForm(formParameters);
        
        if (!success) {
            // Set errors and reload form page    
            response.setRenderParameter("form_errors", submit.getErrors());
            response.setRenderParameter("nextJSP", NEW_JOB_PAGE);
            
            System.out.println(submit.getErrors());
        } else {            
            // Redirect to project display page of projects in process
            response.setRenderParameter("page_type", "in_process");
            response.setRenderParameter("nextJSP", PROJECT_DISPLAY_PAGE);
        }
    }

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

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException {
        try {
            String nextJSP = (String) request.getParameter("nextJSP");
            
            // If no page is set load the new job page
            if (nextJSP == null) {
                nextJSP = NEW_JOB_PAGE;
            }

            // Load new JSP page
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/autodock_vina/".concat(nextJSP).concat(".jsp"));
            dispatcher.include(request, response);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    // Handle ajax calls
    @Override
    public void serveResource(ResourceRequest ajaxParameters, ResourceResponse response) throws PortletException {        
        AjaxDispatcher dispatch = new AjaxDispatcher();
        
        // Init ajax
        dispatch.dispatch(ajaxParameters, response);
        
        // Write JSON response
        dispatch.response();
    }
}
