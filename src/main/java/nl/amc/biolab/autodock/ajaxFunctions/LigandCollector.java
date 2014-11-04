package nl.amc.biolab.autodock.ajaxFunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.autodock.constants.VarConfig;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import docking.crappy.logger.Logger;

/**
 *
 * @author Allard
 */
public class LigandCollector extends AjaxInterface {
    public LigandCollector() {}
    
    @Override
    protected void _run() {
        _getLigandsString();
    }
    
    private void _getLigandsString() {
        if (_cacheNeedsUpdate()) {
        	Logger.log("ligand cache updating", Logger.debug);
            
            _updateLigandCache();
        }
        
        Logger.log("setting ligands", Logger.debug);
        
        _setLigandCache();
    }
    
    private boolean _cacheNeedsUpdate() {        
        File ligandCache = new File(VarConfig.getLigandCache());
        
        if (!ligandCache.exists()) {
            return true;
        }
        
        File folder = new File(VarConfig.getLigandPath());
        long latestFile = 0;
        
        for (File file : folder.listFiles()) {
            long modified = file.lastModified();
            
            if (modified > latestFile) {
                latestFile = modified;
            }
        }
        
        return ligandCache.lastModified() < latestFile;
    }
    
    private boolean _updateLigandCache() {        
        File folder = new File(VarConfig.getLigandPath());
        
        _fileLoop(folder);
        
        try {
            PrintWriter out = new PrintWriter(VarConfig.getLigandCache());
            
            out.println(_getJSONObj().getAsString());
            
            out.flush();
            out.close();
        } catch(FileNotFoundException e) {
        	Logger.log(e, Logger.exception);
            
            return false;
        }
        
        return true;
    }
    
    private void _fileLoop(File folder) {
        for (File file : folder.listFiles()) {
            _getJSONObj().add(FilenameUtils.removeExtension(file.getName()), "");
        }
    }
    
    private void _setLigandCache() {
    	Logger.log("setting items", Logger.debug);
        
        JSONParser parser = new JSONParser();
        
        try {
            _getJSONObj().setWholeObj((JSONObject) parser.parse(new FileReader(VarConfig.getLigandCache())));
        } catch(FileNotFoundException e) {
        	Logger.log(e, Logger.exception);
        } catch(IOException e) {
        	Logger.log(e, Logger.exception);
        } catch (ParseException e) {
        	Logger.log(e, Logger.exception);
        }
    }
}
