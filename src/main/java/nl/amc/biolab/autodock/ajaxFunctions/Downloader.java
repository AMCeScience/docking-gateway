package nl.amc.biolab.autodock.ajaxFunctions;

import java.io.File;
import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.tools.Unzipper;

/**
 *
 * @author Allard van Altena
 */
public class Downloader extends AjaxInterface {
    public Downloader() {}
    
    @Override
    protected void _run() {
        _createDownloads();
    }
    
    private void _createDownloads() {
        Long project_id = new Long(_getSearchTermEntry("project_id"));
        String project_name = _getSearchTermEntry("project_name");
        int compound_count = new Integer(_getSearchTermEntry("compound_count"));
        
        _unpack(project_name);
        
        // Return the whole thing for the moment, do not take selection of ligands into account
        //_getJSONObj().add("redirect", config.getUnzippedOutputPath(project_name));
        _getJSONObj().add("redirect", config.getExternalOutputFileName(project_name));
    }
    
    private void _unpack(String project_name) {
        // check if output is already unzipped
        File outputZipFile = new File(config.getOutputFilePath(project_name));
        
        Unzipper unzipper = new Unzipper();
        
        if(!outputZipFile.exists()) {
            unzipper.untarProjectOutput(project_name);
        }
    }
}