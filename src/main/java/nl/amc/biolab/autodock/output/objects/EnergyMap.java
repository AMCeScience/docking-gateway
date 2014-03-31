package nl.amc.biolab.autodock.output.objects;

import crappy.logger.Logger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import nl.amc.biolab.autodock.constants.VarConfig;

/**
 *
 * @author Allard van Altena
 */
public class EnergyMap extends Logger {
    private final LinkedHashMap ENERGY_MAP = new LinkedHashMap();
    
    public EnergyMap() {}
    
    public void initEnergyMap(String projectName) {
        VarConfig config = new VarConfig();
        
        try {
            BufferedReader csvFile = new BufferedReader(new FileReader(config.getOutputFilePath(projectName)));
            
            String line;
            
            while((line = csvFile.readLine()) != null) {
                // Get number of parts in name
                int nameParts = getNameParts(line);
                
                // Split name from row
                String[] row = line.split(" ", nameParts + 1);
                
                // Add row to map
                _addRow(getName(line), row[nameParts].split(" "));
            }
        } catch(FileNotFoundException e) {
            log.log(e);
        } catch(IOException ex) {
            log.log(ex);
        }
        
    }
    
    public LinkedHashMap getEnergyMap() {
        return ENERGY_MAP;
    }
    
    private String getName(String line) {
        String[] testVal = line.split(" ");
        
        if (!testVal[1].contains("\\.")) {
            return testVal[0] + " " + testVal[1];
        }
        
        return testVal[0];
    }
    
    private int getNameParts(String line) {
        String testVal = line.split(" ")[1];
        
        if (!testVal.contains("\\.")) {
            return 2;
        }
        
        return 1;
    }
    
    private void _addRow(String name, String[] row) {
        ENERGY_MAP.put(name, row);
    }
}
