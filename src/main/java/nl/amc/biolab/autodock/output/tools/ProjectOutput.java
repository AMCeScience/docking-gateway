package nl.amc.biolab.autodock.output.tools;

import java.io.File;
import java.util.LinkedHashMap;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.output.objects.EnergyMap;
import nl.amc.biolab.tools.FileCheck;
import nl.amc.biolab.tools.Unzipper;
import docking.crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class ProjectOutput {
    private final LinkedHashMap<String, Object> OUTPUT_MAP = new LinkedHashMap<String, Object>();
    
    public ProjectOutput() {}
    
    public void initOutput(String projectName) {
    	Logger.log("initOutput", Logger.debug);
    	
        _checkCSVFile(projectName);
        
        EnergyMap processed = _getCSV(projectName);
        
//        _addToMap("table", processed.getEnergyMap());
        _addToMap("graph", processed.getEnergyListForFlot());
        _addToMap("compound_count", processed.getLigandCount());
    }
    
    public LinkedHashMap<String, Object> getMap() {
        return OUTPUT_MAP;
    }
    
    private EnergyMap _getCSV(String name) {
        FileCheck csvFile = new FileCheck();
        EnergyMap energyMap = new EnergyMap();
        
        File[] csvFiles = csvFile.getFilesWithExtension(VarConfig.getOutputUnzipLocation(name), VarConfig.getOutputCSVExt());
        
        if(csvFiles != null && csvFiles.length > 0) {        
            energyMap.initEnergyMapping(csvFiles[0].getPath());
        }
        
        return energyMap;
    }
    
    private void _addToMap(String name, Object object) {
        OUTPUT_MAP.put(name, object);
    }
    
    private void _checkCSVFile(String projectName) {        
        FileCheck csvExists = new FileCheck();
        
        // If csv does not exist
        if(!csvExists.checkIfFilesWithExtensionExists(VarConfig.getOutputUnzipLocation(projectName), VarConfig.getOutputCSVExt())) {
        	Logger.log("untarring", Logger.debug);
        	
            Unzipper unzip = new Unzipper();
            
            // Unzip CSV file
            unzip.untarSpecificFile(VarConfig.getOutputUnzipLocation(projectName), VarConfig.getOutputFilePath(projectName), VarConfig.getOutputCSVName());
        }
    }
}
