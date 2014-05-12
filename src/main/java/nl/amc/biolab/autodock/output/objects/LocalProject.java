package nl.amc.biolab.autodock.output.objects;

import nl.amc.biolab.autodock.constants.VarConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import nl.amc.biolab.autodock.output.tools.ProjectOutput;
import nl.amc.biolab.nsgdm.DataElement;
import nl.amc.biolab.nsgdm.Processing;
import nl.amc.biolab.nsgdm.Project;
import nl.amc.biolab.nsgdm.Submission;
import nl.amc.biolab.nsgdm.SubmissionIO;

/**
 *
 * @author Allard
 */
public class LocalProject extends VarConfig {
    // Project items
    private Long ID = null;
    private String NAME = "";
    private String DESCRIPTION = "";
    private String OWNER = "";
    
    // Processing items
    private Long PROCESSING_ID = null;
    private Date DATE = null;
    
    // TODO: provenance
    private int PROVENANCE_COUNT = 0;
    
    // Submission items
    private Submission SUBMISSION;
    private String STATUS = "";
    
    // SubmissionIO items
    private final Collection<SubmissionIO> SUBMISSIONIO;
    
    // DataElements
    private final Collection<DataElement> INPUT;
    private final Collection<DataElement> OUTPUT;
    
    public LocalProject() {
        SUBMISSIONIO = new ArrayList<SubmissionIO>();
        INPUT = new ArrayList<DataElement>();
        OUTPUT = new ArrayList<DataElement>();
    }
    
    public void initProject(Project project, Processing processing) {
        /*
            processing
            date
            
            project
            name, description, owner
        
            submission
            status
        
            submissionIO
            compound_count, collection of inputs, collection of outputs
        
            TODO: provenance
        */
        
        setID(project.getDbId());
        setName(project.getName());
        setDescription(project.getDescription());
        setOwner(project.getOwner());
        
        setProcessingID(processing.getDbId());
        setDateStarted(processing.getDate());
        // Get first submission, in our use case there is only one submission per processing
        setSubmission(processing.getSubmissions().iterator().next());
        setStatus(getSubmission().getStatus());
        
        for (SubmissionIO subIO : getSubmission().getSubmissionIOs()) {
            setSubmissionIO(subIO);
            
            log.log(subIO);
            
            if (subIO.getType().equals("Input")) {
                setInput(subIO.getDataElement());
            } else if (subIO.getType().equals("Output")) {
                setOutput(subIO.getDataElement());
            }
        }
    }
            
    public LinkedHashMap getProjectMap() {
        LinkedHashMap project = new LinkedHashMap();
        LinkedHashMap inputs;
        
        project.put("project_id", getID());
        project.put("project_name", getName());
        project.put("description", getDescription());
        project.put("user", getOwner());
        
        project.put("processing_id", getProcessingID());
        project.put("date_started", getDateStarted().toString());
        project.put("latest_status", getStatus());
        
        inputs = getDataElementMap(getInput(), "input");
        
        if (!inputs.isEmpty()) {
            project.put("inputs", inputs);
        }
        
        if (!getOutput().isEmpty()) {
            ProjectOutput output = new ProjectOutput();
            
            output.initOutput(getName());
            
            project.put("output", output.getMap());
        }
        
        project.put("provenance_count", getProvenanceCount());
        
        return project;
    }
    
    private LinkedHashMap getDataElementMap(Collection<DataElement> rawData, String type) {
        LinkedHashMap dataElement;
        LinkedHashMap dataElements = new LinkedHashMap();
        int count = 0;
        
        for (DataElement singleData : rawData) {
            dataElement = new LinkedHashMap();
            
            dataElement.put("name", singleData.getName());
            dataElement.put("scan_id", singleData.getScanID());
            dataElement.put("subject", singleData.getSubject());
            dataElement.put("type", singleData.getType());
            dataElement.put("format", singleData.getFormat());
            
            dataElements.put(++count, dataElement);
        }
        
        return dataElements;
    }
    
    public Long getID() {
        return ID;
    }
    
    public void setID(Long id) {
        this.ID = id;
    }

    public Long getProcessingID() {
        return PROCESSING_ID;
    }
    
    public void setProcessingID(Long id) {
        this.PROCESSING_ID = id;
    }
    
    public String getName() {
        return NAME;
    }

    public void setName(String name) {
        this.NAME = name;
    }

    public Date getDateStarted() {
        return DATE;
    }

    public void setDateStarted(Date date) {
        this.DATE = date;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void setDescription(String description) {
        this.DESCRIPTION = description;
    }

    public Collection<DataElement> getInput() {
        return INPUT;
    }

    public void setInput(DataElement input) {
        this.INPUT.add(input);
    }

    public String getStatus() {
        // Check if status contains a number at the beginning and cut it from the string
        if(STATUS.charAt(0) >= '0' && STATUS.charAt(0) <= '9') {
            return STATUS.substring(2, STATUS.length());
        }
        
        return STATUS;
    }

    public void setStatus(String status) {
        this.STATUS = status;
    }

    public Collection<DataElement> getOutput() {
        return OUTPUT;
    }

    public void setOutput(DataElement output) {
        this.OUTPUT.add(output);
    }
    
    public String getOwner() {
        return OWNER;
    }

    public void setOwner(String owner) {
        this.OWNER = owner;
    }

    public int getProvenanceCount() {
        return PROVENANCE_COUNT;
    }

    public void setProvenanceCount(int provenance_count) {
        this.PROVENANCE_COUNT = provenance_count;
    }
    
    public Collection<SubmissionIO> getSubmissionIO() {
        return SUBMISSIONIO;
    }
    
    public void setSubmissionIO(SubmissionIO submission) {
        this.SUBMISSIONIO.add(submission);
    }
    
    public Submission getSubmission() {
        return SUBMISSION;
    }
    
    public void setSubmission(Submission submission) {
        this.SUBMISSION = submission;
    }
}
