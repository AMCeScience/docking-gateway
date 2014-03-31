package nl.amc.biolab.autodock.output.tools;

import java.util.LinkedHashMap;
import nl.amc.biolab.autodock.output.objects.EnergyMap;

/**
 *
 * @author Allard van Altena
 */
public class ProjectOutput {
    private final LinkedHashMap OUTPUT_MAP = new LinkedHashMap();
    
    public ProjectOutput() {}
    
    public void initOutput(String projectName) {
        _addToMap("CSV", _getCSV(projectName));
    }
    
    public LinkedHashMap getMap() {
        return OUTPUT_MAP;
    }
    
    private LinkedHashMap _getCSV(String name) {
        EnergyMap energyMap = new EnergyMap();
            
        energyMap.initEnergyMap(name);
        
        return energyMap.getEnergyMap();
    }
    
    private void _addToMap(String name, Object object) {
        OUTPUT_MAP.put(name, object);
    }
}
