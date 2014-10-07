package nl.amc.biolab.autodock.input.objects;

public class Configuration {
    private String RECEPTOR = "";
    
    private PointIn3DSpace CENTER;
    private PointIn3DSpace SIZE;
    
    private String NUMBER_OF_RUNS = "";
    private String EXHAUSTIVENESS = "";
    private String ENERGY_RANGE = "";
    
    private String FILEPATH = "";
    
    private String ERRORS = "";
    private boolean VALIDATES = true;
    
    /**
     * Initiate this configuration file factory, creates temporary file.
     */
    public Configuration() {}
    
    public void setValid(boolean valid) {
        VALIDATES = valid;
    }
    
    public boolean validate() {
        if (getCenter().validate() && getSize().validate() && !getReceptor().isEmpty() && !getFilePath().isEmpty()) {
            setValid(true);
        } else {
            setValid(false);
        }
        
        return getValid();
    }
    
    public boolean getValid() {
        return VALIDATES;
    }
    
    public void setError(String error) {
        setValid(false);
        
        ERRORS = ERRORS + error;
    }
    
    public String getErrors() {
        return ERRORS;
    }
    
    public void setFilePath(String filePath) {
        FILEPATH = filePath;
    }
    
    public String getFilePath() {
        return FILEPATH;
    }
    
    /**
     * Set the receptor name for this configuration file.
     * 
     * @param receptor_name Name of the receptor file as a String.
     */
    public void setReceptor(String receptor_name) {
        RECEPTOR = receptor_name;
    }
    
    /**
     * Get the receptor name as a String.
     * 
     * @return Receptor name as a String.
     */
    public String getReceptor() {
        return RECEPTOR;
    }
    
    /**
     * Set the center for this configuration file, 
     * this center defines the center of the box of the area of interest for Autodock Vina.
     * 
     * @param center PointIn3DSpace as center of our area of interest.
     */
    public void setCenter(PointIn3DSpace center) {
        CENTER = center;
    }
    
    /**
     * Get the center for this configuration file.
     * 
     * @return Center as PointIn3DSpace.
     */
    public PointIn3DSpace getCenter() {
        return CENTER;
    }
    
    /**
     * Set the size for this configuration file,
     * this size defines the size of the box of the area of interest for Autodock Vina.
     * 
     * @param size PointIn3DSpace as size of our area of interest.
     */
    public void setSize(PointIn3DSpace size) {
        SIZE = size;
    }
    
    /**
     * Get the size for this configuration file.
     * 
     * @return Size as PointIn3DSpace.
     */
    public PointIn3DSpace getSize() {
        return SIZE;
    }
    
    /**
     * Set the number of runs for this configuration file,
     * the number of runs defines the amount of passes Autodock Vina will do per input compound.
     * 
     * @param number_of_runs String defining the number of runs.
     */
    public void setNumberOfRuns(String number_of_runs) {
        NUMBER_OF_RUNS = number_of_runs;
    }
    
    
    /**
     * Set the number of runs for this configuration file,
     * the number of runs defines the amount of passes Autodock Vina will do per input compound.
     * 
     * @param number_of_runs Float defining the number of runs.
     */
    public void setNumberOfRuns(Float number_of_runs) {
        NUMBER_OF_RUNS = Float.toString(number_of_runs);
    }
    
    /**
     * Get the number of runs for this configuration file.
     * 
     * @return number of runs as String.
     */
    public String getNumberOfRuns() {
        return NUMBER_OF_RUNS;
    }
    
    /**
     * Set the exhaustiveness for this configuration file.
     * 
     * @param exhaustiveness String defining the exhaustiveness.
     */
    public void setExhaustiveness(String exhaustiveness) {
        EXHAUSTIVENESS = exhaustiveness;
    }
    
    
    /**
     * Set the exhaustiveness for this configuration file.
     * 
     * @param exhaustiveness Float defining the exhaustiveness.
     */
    public void setExhaustivess(Float exhaustiveness) {
        EXHAUSTIVENESS = Float.toString(exhaustiveness);
    }
    
    /**
     * Get the exhaustiveness for this configuration file.
     * 
     * @return exhaustiveness as String.
     */
    public String getExhaustiveness() {
        return EXHAUSTIVENESS;
    }
    
    /**
     * Set the energy range for this configuration file.
     * 
     * @param energy_range String defining the energy range.
     */
    public void setEnergyRange(String energy_range) {
        ENERGY_RANGE = energy_range;
    }
    
    /**
     * Set the energy range for this configuration file.
     * 
     * @param energy_range Float defining the energy range.
     */
    public void setEnergyRange(Float energy_range) {
        ENERGY_RANGE = Float.toString(energy_range);
    }
    
    /**
     * Get the energy range for this configuration file.
     * 
     * @return energy range as String.
     */
    public String getEnergyRange() {
        return ENERGY_RANGE;
    }
}
