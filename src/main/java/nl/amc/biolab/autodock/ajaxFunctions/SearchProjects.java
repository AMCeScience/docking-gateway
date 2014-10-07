package nl.amc.biolab.autodock.ajaxFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.autodock.output.objects.LocalProject;
import nl.amc.biolab.datamodel.objects.Processing;
import nl.amc.biolab.datamodel.objects.Project;

/**
 * Searches for projects and their data and outputs formatted json data
 * 
 * @author Allard van Altena
 */
public class SearchProjects extends AjaxInterface {
    // Create hashmaps for the sql query building
    private LinkedHashMap<String, String> SELECTS = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> TABLES = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> JOINS = new LinkedHashMap<String, String>();
    private LinkedHashMap<Object, String> WHERES = new LinkedHashMap<Object, String>();
    private LinkedHashMap<String, String> ORDERS = new LinkedHashMap<String, String>();
    private String LIMIT = "";
    private boolean SINGLE_PROJECT = false;
    
    @Override
    protected void _run() {
    	if (_getSearchTermEntry("processing_id") != null) {
    		SINGLE_PROJECT = true;
    	}
    	
        // Start the initiating function
        _getProjects();
    }
    
    /**
     * Main function of this class, gets called after setting the single_project variable
     */
    private void _getProjects() {
        // Perform search to get the projects
    	// First get the SQL string
    	_initSql();
    	// Handle pagination
        _setPagination();
    	
        // Returns map with Project object as key and corresponding Processing object as value (each Project object has one Processing object)
        ArrayList<LinkedHashMap<String, Object>> projects = _doSearch();
        
        // Create JSON object out of project map
        _setProjectsData(projects);
    }
    
    /**
     * Handles pagination, counts pages and adds both the page count plus the current page to the response object
     */
    private void _setPagination() {
    	// Add page count to response object
    	// Cast to integer floors the result
    	_getJSONObj().add("pages", (int) ((_countProjects() - 1) / config.getItemsPerPage()) + 1);
    	
    	// Set default to page 1 if no page is set
    	if (_getSearchTermEntry("page") == null) {
    		_addParam("page", "1");
    	}
    	
    	_getJSONObj().add("page", _getSearchTermEntry("page"));
    }
    
    /**
     * Receives a list of projects as LinkedHashMaps and changes them to LocalProjects for easier output
     * @param projects ArrayList of LinkedHashMaps with projects as returned by the database call
     */
    private void _setProjectsData(ArrayList<LinkedHashMap<String, Object>> projects) {
        log.log("Projects size: " + projects.size());
        
        ArrayList<Map<String, Object>> projectData = new ArrayList<Map<String, Object>>();

        if (projects.isEmpty()) {
            // Indicate that there are no projects and return
            _getJSONObj().add("no_projects", true);
            
            return;
        }
        
        // Indicate that there are projects
        _getJSONObj().add("no_projects", false);
        
        // Make data nice for display in front end
        for (LinkedHashMap<String, Object> rawProject : projects) {
        	Project thisProject = (Project) rawProject.get("project");
        	Processing thisProcessing = (Processing) rawProject.get("processing");
        	
            // Create LocalProject object which contains more information than the nsgdm Project object
            LocalProject project = new LocalProject(thisProject, thisProcessing, _isSingleProject());
            
            // Add project to data map
            projectData.add(project.getProjectMap());
        }
        
        // Add project data map to the JSON
        _getJSONObj().add("projects", projectData);
    }

    /**
     * Counts the projects for the given search parameters
     * @return Integer of project count
     */
    private int _countProjects() {
    	// Set select
    	SELECTS = new LinkedHashMap<String, String>();
    	SELECTS.put("COUNT(*)", null);
    	
    	// Remove limit
    	LIMIT = "";
    	
    	int projects = _getPersistence().countProjectsBySQL(_buildSql());
    	
    	return projects;
    }
    
    /**
     * Performs search for projects for the given search parameters
     * @return ArrayList of LinkedHashMaps with the projects
     */
    private ArrayList<LinkedHashMap<String, Object>> _doSearch() {
    	SELECTS = new LinkedHashMap<String, String>();
        
        // Add selections
        SELECTS.put("{p.*}", null);
        SELECTS.put("{po.*}", null);
    	
        int page_nr = Integer.parseInt(_getSearchTermEntry("page"));
        int items_per_page = config.getItemsPerPage(); 
        
        if (!_isSingleProject()) {
        	LIMIT = ((page_nr - 1) * items_per_page) + ", " + items_per_page;
        }
        
    	// Create the return object
        ArrayList<LinkedHashMap<String, Object>> filteredProjects = new ArrayList<LinkedHashMap<String, Object>>();
        
        // Execute query, entities Project and Processing are added to query
        // Array entry 0 = Project, 1 = Processing
        List<Object[]> projectList;
        
        if (_isSingleProject()) {
        	projectList = _getPersistence().getSingleProjectBySQL(_buildSql());
        } else {
        	projectList = _getPersistence().getProjectsBySQL(_buildSql());
        }
        
        // Add each row to the filteredProjects map, so we have the objects split into project and processing
        for (Object[] row : projectList) {
            LinkedHashMap<String, Object> thisProject = new LinkedHashMap<String, Object>();
            
            Project thisProjectObj = (Project) row[0];
            Processing thisProcessing = (Processing) row[1];
            
            thisProject.put("project", thisProjectObj);
            thisProject.put("processing", thisProcessing);
            
            filteredProjects.add(thisProject);
        }
        
        return filteredProjects;
    }
    
    /**
     * Build the sql string from the sql class variables
     * @return Formatted sql string ready for input into the database
     */
    private String _buildSql() {
    	_getSQLBuilder().setSelect(SELECTS);
        _getSQLBuilder().setTables(TABLES);
        _getSQLBuilder().setJoin(JOINS);
        
        if (WHERES.size() > 0) {
        	_getSQLBuilder().setWhere(WHERES);
        }
        
        if (ORDERS.size() > 0) {
        	_getSQLBuilder().setSort(ORDERS);
        }
        
        if (LIMIT.length() > 0) {
        	//_getSQLBuilder().setLimit(LIMIT);
        }
        
        // Get SQL string
        String sql = _getSQLBuilder().getQuery();
        
        // Clear variable
        _getSQLBuilder().resetQuery();
        
        log("SQL search string: " + sql);
        
        return sql;
    }
    
    /**
     * Take the input parameters and set up the sql class variables
     */
    private void _initSql() {
        // We are at: SELECT ...
        
        // Add tables
        TABLES.put("Project", "p");
        
        // We are at: SELECT ... FROM ...
        
        // Add joins
        JOINS.put("Processing as po", "p.ProjectID = po.ProjectID");
        JOINS.put("Application as app", "po.ApplicationID = app.ApplicationID");
        JOINS.put("User as u", "po.UserID = u.UserID");
        JOINS.put("Submission as sub", "po.ProcessingID = sub.ProcessingID");
        
        // We are at: SELECT ... FROM ... JOIN ... ON ...
        
        // Add user id
        if (_getSearchTermEntry("liferay_user") != null) {	
            WHERES.put(_getSQLBuilder().getWhere("u.LiferayID", "=", _getSearchTermEntry("liferay_user")), "AND");
        }
        
        // Add project id
        if (_getSearchTermEntry("processing_id") != null) {
        	WHERES.put(_getSQLBuilder().getWhere("po.ProcessingID", "=", _getSearchTermEntry("processing_id")), "AND");
        }
        
        // Add where parameters to query
        if (_getSearchTermEntry("search_terms") != null && !_getSearchTermEntry("search_terms").trim().equals("")) {
        	LinkedHashMap<Object, String> tempMap = new LinkedHashMap<Object, String>();
        	ArrayList<String> tempList = new ArrayList<String>();
        	
        	String concat = "CONCAT_WS(',', LOWER(p.ProjectName), LOWER(p.ProjectDescription), LOWER(u.FirstName), LOWER(u.LastName), LOWER(po.ProcessingDate))";

        	String[] terms = _getSearchTermEntry("search_terms").trim().split(" ");
        	
        	for (String term : terms) {
        		// Split on '+' for and-search
        		String[] andTerms = term.split(Pattern.quote("+"));
        		
        		// Check if there was a split
        		if (andTerms.length > 1) {
        			LinkedHashMap<ArrayList<String>, String> andMap = new LinkedHashMap<ArrayList<String>, String>();
            		ArrayList<String> andList = new ArrayList<String>();
            		
        			// Add the 'and' terms
    	    		for (String andTerm : andTerms) {
    	    			if (andTerm.length() > 0) {
    	    				andList.add(_getSQLBuilder().getWhere(concat, "LIKE", "LOWER('%" + andTerm + "%')"));
    	    			}
    	    		}
    	    		
    	    		if (andList.size() > 0) {
            			andMap.put(andList, "AND");
            			tempMap.put(andMap, "OR");
            		}
    	    		
    	    		// Continue onto next term because we are finished with this one
    	    		continue;
        		}
        		
        		// Add the 'or' terms
    			if (term.length() > 0) {
    				tempList.add(_getSQLBuilder().getWhere(concat, "LIKE", "LOWER('%" + term + "%')"));
    				
    				tempMap.put(tempList, "OR");
    			}
        	}
        	
            WHERES.put(tempMap, "AND");
        }

        // Add where for the status
        if (_getSearchTermEntry("status") != null && !_getSearchTermEntry("status").equals("all")) {
        	LinkedHashMap<ArrayList<String>, String> tempMap = new LinkedHashMap<ArrayList<String>, String>();
        	ArrayList<String> tempList = new ArrayList<String>();
        	
            Iterator<String> statusIter = Arrays.asList(_getSearchTermEntry("status").split(",")).iterator();
            
            while (statusIter.hasNext()) {
                // WHERE status = xxxxx
            	tempList.add(_getSQLBuilder().getWhere("(SELECT Value FROM Status as s WHERE s.SubmissionID = sub.SubmissionID ORDER BY s.StatusTime DESC LIMIT 1)", "LIKE", "'%" + statusIter.next().trim() + "%'"));
            }
            
            tempMap.put(tempList, "OR");
            
            WHERES.put(tempMap, "AND");
        }
        
        // We are at: SELECT ... FROM ... JOIN ... ON ... (WHERE ... OR ...)

        // Add order by date_started
        if (_getSearchTermEntry("date_started") != null) {
            if (_getSearchTermEntry("date_started").equals("descending")) {
                ORDERS.put("po.ProcessingDate", "DESC");
            } else if (_getSearchTermEntry("date_started").equals("ascending")) {
                ORDERS.put("po.ProcessingDate", "ASC");
            }
        }

        // We are at: SELECT ... FROM ... JOIN ... ON ... (WHERE ... OR ...) (ORDER BY ...)
    }
    
    /**
     * Check if we are trying to get one project or multiple
     * @return Boolean whether if this is a single project search or not
     */
    private boolean _isSingleProject() {
    	return SINGLE_PROJECT;
    }
}
