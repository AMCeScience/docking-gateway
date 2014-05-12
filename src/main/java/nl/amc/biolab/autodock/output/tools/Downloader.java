package nl.amc.biolab.autodock.output.tools;

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
        
        _getJSONObj().add("redirect", true);
    }
    
    private void _unpack(String project_name) {
        // check if output is already unzipped
        File outputZipFile = new File(config.getOutputFilePath(project_name));
        
        Unzipper unzipper = new Unzipper();
        
        if(!outputZipFile.exists()) {            
            unzipper.unzipProjectOutput(project_name);
        }
        
        // check if output zip is already unzipped into folder
        File unzipDir = new File(config.getOutputUnzipLocation(project_name));
        
        if(!unzipDir.exists() && !unzipDir.isDirectory()) {
            unzipper.unzip(config.getOutputFileName(project_name), config.getOutputUnzipLocation(project_name));
        }
    }
}