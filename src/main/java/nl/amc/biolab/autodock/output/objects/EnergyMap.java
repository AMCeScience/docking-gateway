package nl.amc.biolab.autodock.output.objects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import docking.crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class EnergyMap {
    private final LinkedHashMap<String, String> ENERGY_MAP = new LinkedHashMap<String, String>();
    private final ArrayList<ArrayList<String>> X_TICKS = new ArrayList<ArrayList<String>>();
    private final ArrayList<ArrayList<String>> Y_TICKS = new ArrayList<ArrayList<String>>();
    private final ArrayList<ArrayList<String>> ENERGY_LIST = new ArrayList<ArrayList<String>>();
    private int COUNT = 0;
    
    public EnergyMap() {}
    
    public void initEnergyMapping(String csvName) {
        Logger.log(csvName, Logger.debug);
        
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
            Logger.log(e, Logger.exception);
        } catch(IOException e) {
            Logger.log(e, Logger.exception);
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
        ENERGY_MAP.put(name, row[0]);
        
        ArrayList<String> tick = new ArrayList<String>();
        
        tick.add(name);
        tick.add(row[0]);
        
        ENERGY_LIST.add(tick);
    }
}
