package nl.amc.biolab.autodock.input.objects;

/**
 *
 * @author Allard van Altena
 */
public class Ligands {
    private String ERRORS = "";
    private boolean VALIDATES = false;
    private Long COUNT = 0L;
    private Long PILOT_COUNT = 0L;
    private String FILE_NAME = "";
    private String PILOT_FILE_NAME = "";
    
    public Ligands() {}
    
    public void setValid(boolean valid) {
        VALIDATES = valid;
    }
    
    public boolean validate() {
        return VALIDATES;
    }
    
    public void setError(String error) {
        setValid(false);
        
        ERRORS = ERRORS + error;
    }
    
    public String getErrors() {
        return ERRORS;
    }
    
    public void addCount() {
        COUNT = COUNT + 1;
    }
    
    public void addCount(int count) {
        COUNT = COUNT + count;
    }
    
    public Long getCount() {
        return COUNT;
    }
    
    public void addPilotCount() {
        PILOT_COUNT = PILOT_COUNT + 1;
    }
    
    public Long getPilotCount() {
        return PILOT_COUNT;
    }
    
    public void setFileName(String filename) {
    	FILE_NAME = filename;
    }
    
    public String getFileName() {
    	return FILE_NAME;
    }
    
    public void setPilotFileName(String pilot_filename) {
    	PILOT_FILE_NAME = pilot_filename;
    }
    
    public String getPilotFileName() {
    	return PILOT_FILE_NAME;
    }
}
