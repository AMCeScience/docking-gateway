package nl.amc.biolab.autodock.output.tools;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.amc.biolab.autodock.output.objects.*;
import nl.amc.biolab.nsgdm.Processing;
import nl.amc.biolab.nsgdm.Project;

/**
 *
 * @author Allard
 */
public class SearchProjects extends AjaxInterface {    
    @Override
    protected void _run() {
        log.log("Run searchProjects");
        
        // Start the initiating function
        getProjects();
    }
    
    private void getProjects() {
        log.log("getProjects called");

        // Perform search to get the projects
        // Returns map with Project object as key and corresponding Processing object as value (each Project object has one Processing object)
        LinkedHashMap<String, LinkedHashMap<String, Object>> projects = doSearch();
        
        // Create JSON object out of project map
        setProjectsData(projects);
    }

    private void setProjectsData(LinkedHashMap<String, LinkedHashMap<String, Object>> projects) {
        log.log("setProjectsData");
        log.log("Projects size: " + projects.size());
        
        // Set page type in return JSON
        _getJSONObj().add("page_type", _getSearchTermEntry("page_type"));
        
        LinkedHashMap<String, Map> projectData = new LinkedHashMap<String, Map>();

        if (projects.isEmpty()) {
            // Indicate that there are no projects and return
            _getJSONObj().add("no_projects", true);
            
            return;
        }
        
        // Indicate that there are projects
        _getJSONObj().add("no_projects", false);
        
        // Make data nice for display in front end
        for (Entry rawProject : projects.entrySet()) {
            LinkedHashMap<String, Object> thisProjectData = (LinkedHashMap<String, Object>) rawProject.getValue();
            
            // Create LocalProject object which contains more information than the nsgdm Project object
            LocalProject project = getSingleProjectData((Project) thisProjectData.get("project"), (Processing) thisProjectData.get("processing"));
            
            // Add project to data map
            projectData.put(project.getName(), project.getProjectMap());
        }
        
        // Add project data map to the JSON
        _getJSONObj().add("projects", projectData);
    }
    
    private LocalProject getSingleProjectData(Project projectData, Processing processing) {
        log.log("getSingleProjectData");
        
        // Create new LocalProject object
        LocalProject project = new LocalProject();
        
        // Fill LocalProject with data for one project
        project.initProject(projectData, processing);
        
        return project;
    }

    private LinkedHashMap<String, LinkedHashMap<String, Object>> doSearch() {
        log.log("doSearch");
        
        // Create the return object
        LinkedHashMap<String, LinkedHashMap<String, Object>> filteredProjects = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
        
        // Create hashmaps for the sql query building
        LinkedHashMap order_list = new LinkedHashMap();
        LinkedHashMap<String, String> tables = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> select = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> joins = new LinkedHashMap<String, String>();
        
        // Add selections
        select.put("{p.*}", null);
        select.put("{po.*}", null);
        
        // Add tables
        tables.put("Project", "p");
        
        // Add joins
        joins.put("Processing as po", "p.ProjectID = po.ProjectID");
        joins.put("UserProject as up", "p.ProjectID = up.ProjectID");
        joins.put("User as u", "up.UserKey = u.UserKey");
        
        // Add to query
        _getPersistence().setSelect(select);
        
        // We are at: SELECT ...
        
        // Add tables to query
        _getPersistence().setTables(tables);
        
        // We are at: SELECT ... FROM ...
        
        // Add joins to query
        _getPersistence().setJoin(joins);
        
        // We are at: SELECT ... FROM ... JOIN ... ON ...
        
        _getPersistence().startWhere();
        
        _getPersistence().setWhere("u.LiferayID", "=", _getSearchTermEntry("liferay_user"));
        
        if (_getSearchTermEntry("search_terms") != null || (_getSearchTermEntry("status") != null && !_getSearchTermEntry("status").equals("all"))) {
            _getPersistence().setWhereAnd();
        }
        
        // Add where parameters to query
        if (_getSearchTermEntry("search_terms") != null) {
            // WHERE project_name LIKE %xxxxx%
            _getPersistence().setWhere("(p.ProjectName", "LIKE", "'%" + _getSearchTermEntry("search_terms") + "%'");
            
            _getPersistence().setWhereOr();
            
            // WHERE project_description LIKE %xxxxx%
            _getPersistence().setWhere("p.ProjectDescription", "LIKE", "'%" + _getSearchTermEntry("search_terms") + "%')");
            
            if (_getSearchTermEntry("status") != null && !_getSearchTermEntry("status").equals("all")) {
                _getPersistence().setWhereAnd();
            }
        }

        // Add where for the status
        if (_getSearchTermEntry("status") != null && !_getSearchTermEntry("status").equals("all")) {
            Iterator statusIter = Arrays.asList(_getSearchTermEntry("status").split(",")).iterator();
            
            _getPersistence().setWhereOpen();
            
            while (statusIter.hasNext()) {
                // WHERE status = xxxxx
                _getPersistence().setWhere("po.ProcessingStatus", "LIKE", "'%" + (String) statusIter.next() + "'");
                
                if (statusIter.hasNext()) {
                    _getPersistence().setWhereOr();
                }
            }
            
            _getPersistence().setWhereClose();
        }
        
        // We are at: SELECT ... FROM ... JOIN ... ON ... (WHERE ... OR ...)

        // Add order by date_started
        if (_getSearchTermEntry("date_started") != null) {
            if (_getSearchTermEntry("date_started").equals("descending") || _getSearchTermEntry("date_started").equals("default")) {
                order_list.put("po.ProcessingDate", "DESC");
            } else if (_getSearchTermEntry("date_started").equals("ascending")) {
                order_list.put("po.ProcessingDate", "ASC");
            }
        }

        // Add order by project_name
        if (_getSearchTermEntry("project_name") != null) {
            if (_getSearchTermEntry("project_name").equals("descending")) {
                order_list.put("p.ProjectName", "DESC");
            } else if (_getSearchTermEntry("project_name").equals("ascending")) {
                order_list.put("p.ProjectName", "ASC");
            }
        }
        
        _getPersistence().setSort(order_list);
        
        // We are at: SELECT ... FROM ... JOIN ... ON ... (WHERE ... OR ...) (ORDER BY ...)

        log.log("SQL as string: " + _getPersistence().getQuery());

        // Execute query, entities Project and Processing are added to query
        // Array entry 0 = Project, 1 = Processing
        List<Object[]> projectList = _getPersistence().getProjectsBySQL(_getPersistence().getQuery());
        
        // Add each row to the filteredProjects map, so we have the objects split into key/value
        for (Object[] row : projectList) {
            LinkedHashMap thisProject = new LinkedHashMap();
            
            Project thisProjectObj = (Project) row[0];
            
            thisProject.put("project", thisProjectObj);
            thisProject.put("processing", (Processing) row[1]);
            
            filteredProjects.put(thisProjectObj.getName(), thisProject);
        }
        
        return filteredProjects;
    }
}
