package nl.amc.biolab.autodock.input.objects;

/**
 *
 * @author Allard van Altena
 */
public class Ligands {
    private String ERRORS = "";
    private boolean VALIDATES = false;
    private Long COUNT = 0L;
    
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
    
    public Long getCount() {
        return COUNT;
    }
}
