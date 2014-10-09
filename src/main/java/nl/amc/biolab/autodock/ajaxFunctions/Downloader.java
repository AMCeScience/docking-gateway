package nl.amc.biolab.autodock.ajaxFunctions;

import nl.amc.biolab.autodock.ajaxHandlers.AjaxInterface;
import nl.amc.biolab.autodock.constants.VarConfig;

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
        String project_name = _getSearchTermEntry("project_name");
        
        //_unpack(project_name);
        
        // Return the whole thing for the moment, do not take selection of ligands into account
        _getJSONObj().add("redirect", VarConfig.getExternalOutputFileName(project_name));
    }
    
//    private void _unpack(String project_name) {
//        // check if output is already unzipped
//        File outputZipFile = new File(VarConfig.getOutputFilePath(project_name));
//        
//        Unzipper unzipper = new Unzipper();
//        
//        if(!outputZipFile.exists()) {
//            unzipper.untar(VarConfig.getOutputUnzipLocation(project_name), VarConfig.getOutputFilePath(project_name));
//        }
//    }
}