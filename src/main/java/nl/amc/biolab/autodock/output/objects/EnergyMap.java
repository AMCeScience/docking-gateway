package nl.amc.biolab.autodock.output.objects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import nl.amc.biolab.autodock.constants.VarConfig;

/**
 *
 * @author Allard van Altena
 */
public class EnergyMap extends VarConfig {
    private final LinkedHashMap<String, String> ENERGY_MAP = new LinkedHashMap<String, String>();
    private final ArrayList<ArrayList<String>> X_TICKS = new ArrayList<ArrayList<String>>();
    private final ArrayList<ArrayList<String>> Y_TICKS = new ArrayList<ArrayList<String>>();
    private final ArrayList<ArrayList<String>> ENERGY_LIST = new ArrayList<ArrayList<String>>();
    private int COUNT = 0;
    
    public EnergyMap() {}
    
    public void initEnergyMapping(String csvName) {
        log.log(csvName);
        
        try {
            BufferedReader csvFile = new BufferedReader(new FileReader(csvName));
            
            String line;
            
            while((line = csvFile.readLine()) != null) {                
                // Split name from row
                String[] row = line.split(",", 2);
                
                if(row.length > 1) { 
                    // Add row to map
                    _addRow(row[0], row[1].split(","));
                    _addLigandCount();
                }
            }
            
            csvFile.close();
        } catch(FileNotFoundException e) {
            log.log(e);
        } catch(IOException ex) {
            log.log(ex);
        }
        
    }
    
    public LinkedHashMap<String, String> getEnergyMap() {
        return ENERGY_MAP;
    }
    
    public LinkedHashMap<String, ArrayList<ArrayList<String>>> getEnergyMapForFlot() {
        LinkedHashMap<String, ArrayList<ArrayList<String>>> data = new LinkedHashMap<String, ArrayList<ArrayList<String>>>();
        
        data.put("xaxis", X_TICKS);
        data.put("yaxis", Y_TICKS);
        
        return data;
    }
    
    public ArrayList<ArrayList<String>> getEnergyListForFlot() {
        return ENERGY_LIST;
    }
    
    public int getLigandCount() {
        return COUNT;
    }
    
    private void _addLigandCount() {
        COUNT++;
    }
    
    private void _addRow(String name, String[] row) {
        //Joiner commaJoiner = Joiner.on(",").skipNulls();
        
        ENERGY_MAP.put(name, row[0]);
        
        ArrayList<String> tick = new ArrayList<String>();
        
        tick.add(name);
        tick.add(row[0]);
        
        ENERGY_LIST.add(tick);
        
//        ArrayList xPoint = new ArrayList();
//        
//        xPoint.add(getLigandCount());
//        xPoint.add(name);
//        
//        X_TICKS.add(xPoint);
//        
//        ArrayList yPoint = new ArrayList();
//        
//        yPoint.add(row[0]);
//        
//        Y_TICKS.add(yPoint);
    }
}
