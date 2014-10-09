package nl.amc.biolab.autodock.output.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import nl.amc.biolab.autodock.output.tools.ProjectOutput;
import nl.amc.biolab.datamodel.objects.DataElement;
import nl.amc.biolab.datamodel.objects.Processing;
import nl.amc.biolab.datamodel.objects.Project;
import nl.amc.biolab.datamodel.objects.Submission;
import nl.amc.biolab.datamodel.objects.SubmissionIO;
import docking.crappy.logger.Logger;

/**
 * Takes the nsgdm objects and creates object with more complete data
 *
 * @author Allard
 */
public class LocalProject {
    // Project items
    private Long ID = null;
    private String NAME = "";
    private String DESCRIPTION = "";
    private String OWNER = null;
    private String APPLICATION = "";
    private String OVERALL_STATUS = "";
    
    // Processing items
    private Long PROCESSING_ID = null;
    private Date DATE = null;
    
    // Submission items
    private ArrayList<HashMap<String, Object>> SUBMISSIONS = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> SUBMISSIONIOS = new ArrayList<HashMap<String, Object>>();
    
    // Extra items
    private int COMPOUND_COUNT = 0;
    private int PROVENANCE_COUNT = 0;
    private DataElement OUTPUT;
    
    private boolean FULLREPORT = false;
    
    /**
     * Constructor takes Project and Processing as input and initiates the data formatting
     * @param project Project class from nsgdm
     * @param processing Processing class from nsgdm
     * @param full_report Whether or not if we want to get the Project and Processing classes with additionally the Submission, SubmissionIO, and DataElement class
     */
    public LocalProject(Project project, Processing processing, boolean full_report) {
    	_setFullReport(full_report);
    	
    	_initProject(project, processing);
    }
    
    /**
     * Sets the class variables for Project, Processing, Submission, SubmissionIO, and DataElement 
     * @param project Project class from nsgdm
     * @param processing Processing class from nsgdm
     */
    private void _initProject(Project project, Processing processing) {
        _setID(project.getDbId());
        _setName(project.getName());
        _setDescription(project.getDescription());
        _setOwner(project.getOwner());
        
        _setProcessingID(processing.getDbId());
        _setProjectStatus(processing.getStatus());
        _setDateStarted(processing.getDate());
        _setApplication(processing.getApplication().getName());
        
        if (_getFullReport()) {
	        // Get submissions for this project + processing
	        for(Submission sub : processing.getSubmissions()) {
	        	// Clear IO variable
	        	_resetSubmissionIOs();
	        	
	        	// Get submissionIOs
	        	for (SubmissionIO subIO : sub.getSubmissionIOs()) {
	        		// Set IOs
	        		_setSubmissionIO(subIO);
	            }
	        	
	        	// Set submission
	        	_setSubmission(sub);
	        }
        }
    }
         
    /**
     * Outputs the class variables as LinkedHashMap formatted for use at the client side
     * @return LinkedHashMap of project data
     */
    public LinkedHashMap<String, Object> getProjectMap() {
        LinkedHashMap<String, Object> project = new LinkedHashMap<String, Object>();
        
        project.put("project_id", _getID());
        project.put("project_name", _getName());
        project.put("description", _getDescription());
        project.put("user", _getOwner());
        project.put("application", _getApplication());
        
        project.put("processing_id", _getProcessingID());
        project.put("overall_status", _getProjectStatus());
        project.put("date_started", _getDateStarted().toString());
        
        if (_getFullReport()) {
        	Logger.log("getting full report", Logger.debug);
        	
		    project.put("submissions", _getSubmissions());
		    
		    project.put("compound_count", _getCompoundCount());
		    project.put("provenance_count", _getProvenanceCount());
		    
		    if (_getOutput() != null) {
		    	Logger.log("getting output", Logger.debug);
		    	
		    	ProjectOutput output = new ProjectOutput();
		    	
		    	output.initOutput(_getName());
		    	
		    	project.put("output", output.getMap());
		    }
        }
        
        return project;
    }
    
    /**
     * Get the data map for DataElement objects
     * @param dataEl DataElement from nsgdm
     * @return HashMap of DataElement data
     */
    private HashMap<String, Object> _getDataElementMap(DataElement dataEl) {
    	HashMap<String, Object> dataMap = new HashMap<String, Object>();
    	
    	dataMap.put("name", dataEl.getName());
    	dataMap.put("ligand_count", dataEl.getValueByName("ligand_count"));
    	dataMap.put("uri", dataEl.getURI());
    	dataMap.put("type", dataEl.getType());
    	dataMap.put("format", dataEl.getFormat());
    	dataMap.put("date", (dataEl.getDate() != null ? dataEl.getDate().toString() : ""));
    	dataMap.put("size", dataEl.getSize());
    	
    	if (dataEl.getValueByName("ligand_count") != null) {
    		_setCompoundCount(Integer.parseInt(dataEl.getValueByName("ligand_count")));
    	}
    	
    	return dataMap;
    }
    
    /* ###################################################################### */
    /* # 				CLASS VARIABLE GETTERS AND SETTERS 					# */
    /* ###################################################################### */
    
    private void _setOutput(DataElement output) {
    	OUTPUT = output;
    }
    
    private DataElement _getOutput() {
    	return OUTPUT;
    }
    
    private Long _getID() {
        return ID;
    }
    
    private void _setID(Long id) {
        this.ID = id;
    }

    private Long _getProcessingID() {
        return PROCESSING_ID;
    }
    
    private void _setProcessingID(Long id) {
        this.PROCESSING_ID = id;
    }
    
    private String _getApplication() {
    	return APPLICATION;
    }
    
    private void _setApplication(String app) {
		APPLICATION += app.toString();
    }
    
    private String _getName() {
        return NAME;
    }

    private void _setName(String name) {
        this.NAME = name;
    }

    private Date _getDateStarted() {
        return DATE;
    }

    private void _setDateStarted(Date date) {
        this.DATE = date;
    }

    private String _getDescription() {
        return DESCRIPTION;
    }

    private void _setDescription(String description) {
        this.DESCRIPTION = description;
    }
    
    private String _getOwner() {
        return OWNER;
    }

    private void _setOwner(String owner) {
        this.OWNER = owner;
    }
    
    private String _getProjectStatus() {
    	return OVERALL_STATUS;
    }
    
    private void _setProjectStatus(String status) {
    	this.OVERALL_STATUS += " " + status;
    }
    
    private ArrayList<HashMap<String, Object>> _getSubmissions() {
    	return SUBMISSIONS;
    }
    
    private void _setSubmissionIO(SubmissionIO subIO) {
    	HashMap<String, Object> IOMap = new HashMap<String, Object>();
    	
    	IOMap.put("type", subIO.getType());
    	IOMap.put("data_element", _getDataElementMap(subIO.getDataElement()));
    	
    	if (subIO.getType().equals("Output")) {
    		_setOutput(subIO.getDataElement());
    	}
    	
    	SUBMISSIONIOS.add(IOMap);
    }
    
    private void _resetSubmissionIOs() {
    	SUBMISSIONIOS = new ArrayList<HashMap<String, Object>>();
    }
    
    private ArrayList<HashMap<String, Object>> _getSubmissionIOs() {
    	return SUBMISSIONIOS;
    }
    
    private void _setSubmission(Submission submission) {
    	HashMap<String, Object> local = new HashMap<String, Object>();
    	
    	local.put("submission_id", submission.getDbId());
    	local.put("status", submission.getLastStatus().getValue());
    	local.put("submission", submission.getName());
    	local.put("submissionIO", _getSubmissionIOs());
    	
    	SUBMISSIONS.add(local);
    }
    
    private void _setFullReport(boolean report) {
    	FULLREPORT = report;
    }
    
    private boolean _getFullReport() {
    	return FULLREPORT;
    }
    
    private void _setCompoundCount(int count) {
    	COMPOUND_COUNT = count;
    }
    
    private int _getCompoundCount() {
    	return COMPOUND_COUNT;
    }
    
    @SuppressWarnings("unused")
	private void _setProvenanceCount(int count) {
    	PROVENANCE_COUNT = count;
    }
    
    private int _getProvenanceCount() {
    	return PROVENANCE_COUNT;
    }
}
