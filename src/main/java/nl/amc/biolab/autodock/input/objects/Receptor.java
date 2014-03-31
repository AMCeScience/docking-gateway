package nl.amc.biolab.autodock.input.objects;

/**
 *
 * @author Allard van Altena
 */
public class Receptor {
    private String ERRORS = "";
    private boolean VALIDATES = false;
    
    public Receptor() {}
    
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
}
