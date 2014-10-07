package nl.amc.biolab.autodock.ajaxFunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
            log.log("ligand cache updating");
            
            _updateLigandCache();
        }
        
        log.log("setting ligands");
        
        _setLigandCache();
    }
    
    private boolean _cacheNeedsUpdate() {        
        File ligandCache = new File(config.getLigandCache());
        
        if (!ligandCache.exists()) {
            return true;
        }
        
        File folder = new File(config.getLigandPath());
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
        File folder = new File(config.getLigandPath());
        
        _fileLoop(folder);
        
        try {
            PrintWriter out = new PrintWriter(config.getLigandCache());
            
            out.println(_getJSONObj().getAsString());
            
            out.flush();
            out.close();
        } catch(FileNotFoundException e) {
            log.log(e);
            
            return false;
        }
        
        return true;
    }
    
    private void _fileLoop(File folder) {        
        ArrayList<String> fileNames = new ArrayList<String>();
        
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                _fileLoop(file);
            } else {
//                fileNames.add(file.getName());
            }
        }
        
        if (!folder.getName().equals("ligands")) {
            _getJSONObj().add(folder.getName(), fileNames);
        }
    }
    
    private void _setLigandCache() {
        log.log("setting items");
        
        JSONParser parser = new JSONParser();
        
        try {
            _getJSONObj().setWholeObj((JSONObject) parser.parse(new FileReader(config.getLigandCache())));
        } catch(FileNotFoundException e) {
            log.log(e);
        } catch(IOException e) {
            log.log(e);
        } catch (ParseException e) {
            log.log(e);
        }
    }
}
