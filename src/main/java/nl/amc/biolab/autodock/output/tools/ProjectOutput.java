package nl.amc.biolab.autodock.output.tools;

import java.io.File;
import java.util.LinkedHashMap;
import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.output.objects.EnergyMap;
import nl.amc.biolab.tools.FileCheck;
import nl.amc.biolab.tools.Unzipper;

/**
 *
 * @author Allard van Altena
 */
public class ProjectOutput extends VarConfig {
    private final LinkedHashMap OUTPUT_MAP = new LinkedHashMap();
    
    public ProjectOutput() {}
    
    public void initOutput(String projectName) {
        _checkCSVFile(projectName);
        
        EnergyMap processed = _getCSV(projectName);
        
        _addToMap("table", processed.getEnergyMap());
        _addToMap("graph", processed.getEnergyListForFlot());
        _addToMap("compound_count", processed.getLigandCount());
    }
    
    public LinkedHashMap getMap() {
        return OUTPUT_MAP;
    }
    
    private EnergyMap _getCSV(String name) {
        FileCheck csvFile = new FileCheck();
        EnergyMap energyMap = new EnergyMap();
        
        File[] csvFiles = csvFile.getFilesWithExtension(config.getProjectFilePath(name), config.getOutputCSVExt());
        
        if(csvFiles != null && csvFiles.length > 0) {        
            energyMap.initEnergyMapping(csvFiles[0].getPath());
        }
        
        return energyMap;
    }
    
    private void _addToMap(String name, Object object) {
        OUTPUT_MAP.put(name, object);
    }
    
    private void _checkCSVFile(String name) {        
        FileCheck csvExists = new FileCheck();
        
        // if csv does not exist
        if(!csvExists.checkIfFilesWithExtensionExists(config.getProjectFilePath(name), config.getOutputCSVExt())) {
            Unzipper unzip = new Unzipper();
            
            // unzip project output folder
            unzip.unzipProjectOutput(name);
        }
    }
}
