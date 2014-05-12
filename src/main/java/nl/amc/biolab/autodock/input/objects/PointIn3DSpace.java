package nl.amc.biolab.autodock.input.objects;

import nl.amc.biolab.autodock.constants.VarConfig;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Allard van Altena
 */
public class PointIn3DSpace extends VarConfig {
    private String X = "";
    private String Y = "";
    private String Z = "";
    private String POINT_TYPE = "";
    private String ERRORS = "";
    
    /**
     * Initiate PointIn3DSpace, set point type with parameter.
     * 
     * @param point_type Set the type of this point.
     */
    public PointIn3DSpace(String point_type) {
        if (point_type.equals("center") || point_type.equals("size")) {
            POINT_TYPE = point_type;
        }
    }
    
    public boolean validate() {
        if (X.isEmpty()) {
            setError(POINT_TYPE + " X is not set.<br/>");
        }
        
        if (Y.isEmpty()) {
            setError(POINT_TYPE + " Y is not set.<br/>");
        }
        
        if (Z.isEmpty()) {
            setError(POINT_TYPE + " Z is not set.<br/>");
        }
        
        if (POINT_TYPE.isEmpty()) {
            setError("point type is not set.<br/>");
        }
        
        return !X.isEmpty() && !Y.isEmpty() && !Z.isEmpty() && !POINT_TYPE.isEmpty();
    }
    
    private boolean _isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        
        return true;
    }
    
    public void setError(String error) {
        ERRORS = ERRORS + error;
    }
    
    public String getErrors() {
        return ERRORS;
    }
    
    /**
     * Writes the point according to Autodock Vina configuration file layout.
     * 
     * @param out BufferedWriter that is used to write to the configuration file.
     */
    public void write_point(BufferedWriter out) {
        try {
            out.write(get_type() + "_x = " + X);
            out.newLine();
            out.write(get_type() + "_y = " + Y);
            out.newLine();
            out.write(get_type() + "_z = " + Z);
            out.newLine();
        } catch(IOException e) {
            log.log(e);
        }
    }
    
    /**
     * Set x coordinate of this point.
     * 
     * @param x String with x coordinate.
     */
    public void set_x(String x) {
        if (_isInt(x)) {
            X = x;
        } else {
            setError("Coordinate x is not an integer.<br/>");
        }
    }
    
    /**
     * Set x coordinate of this point.
     * 
     * @param x Integer with x coordinate.
     */
    public void set_x(int x) {
        X = Integer.toString(x);
    }
    
    /**
     * Set y coordinate of this point.
     * 
     * @param y String with x coordinate.
     */
    public void set_y(String y) {
        if (_isInt(y)) {
            Y = y;
        } else {
            setError("Coordinate y is not an integer.<br/>");
        }
    }
    
    /**
     * Set y coordinate of this point.
     * 
     * @param y Integer with x coordinate.
     */
    public void set_y(int y) {
        Y = Integer.toString(y);
    }
    
    /**
     * Set z coordinate of this point.
     * 
     * @param z String with x coordinate.
     */
    public void set_z(String z) {
        if (_isInt(z)) {
            Z = z;
        } else {
            setError("Coordinate z is not an integer.<br/>");
        }
    }
    
    /**
     * Set x coordinate of this point.
     * 
     * @param z Integer with x coordinate.
     */
    public void set_z(int z) {
        Z = Integer.toString(z);
    }
    
    /**
     * Get the x coordinate of this point.
     * 
     * @return String with the x coordinate formatted to Autodock Vina configuration file layout.
     */
    public String get_x() {
        return get_type() + "_x = " + X; 
    }
    
    /**
     * Get the y coordinate of this point.
     * 
     * @return String with the y coordinate formatted to Autodock Vina configuration file layout.
     */
    public String get_y() {
        return get_type() + "_y = " + Y; 
    }
    
    /**
     * Get the z coordinate of this point.
     * 
     * @return String with the z coordinate formatted to Autodock Vina configuration file layout.
     */
    public String get_z() {
        return get_type() + "_z = " + Z; 
    }
    
    /**
     * Get the type of this point.
     * 
     * @return String with type of this point.
     */
    public String get_type() {
        return POINT_TYPE;
    }
}
