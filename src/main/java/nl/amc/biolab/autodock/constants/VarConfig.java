package nl.amc.biolab.autodock.constants;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class VarConfig extends Logger {
	private final String FILE_PATH = "guse/apache-tomcat-6.0.36/webapps/autodock_files/config.json";
	private JSONObject OBJ;
    
    public VarConfig config;
    
    public VarConfig() {
    	JSONParser parser = new JSONParser();
    	
    	try {
    		_setJSON((JSONObject) parser.parse(new FileReader(FILE_PATH)));
    	} catch(FileNotFoundException e) {
    		log(e.toString());
    	} catch (IOException e) {
    		log(e.toString());
		} catch (ParseException e) {
			log(e.toString());
		}
    	
    	config = this;
    }
    
    private void _setJSON(JSONObject obj) {
    	OBJ = obj;
    }
    
    private JSONObject _getJSON() {
    	return OBJ;
    }
    
    public String getItem(String name) throws Exception {
    	if (_getJSON().containsKey(name)) {
    		return _getItem(name);
    	} else {
    		throw new Exception("key does not exist in configuration file.");
    	}
    }
    
    private String _getItem(String name) {
    	return _getJSON().get(name).toString();
    }
    
    public boolean getIsDev() {
    	if (_getItem("is_dev").equals("true")) {
    		return true;
    	}
    	
    	return false;
    }
    
    public String getAutodockName() {
        return _getItem("autodock_name");
    }
    
    public String getWebDavUri() {
        return _getItem("webdav_internal");
    }
    
    public String getExternalWebDavUri() {
        return _getItem("webdav_external");
    }
    
    public String getUri(String folderName, String fileName) {
        return getWebDavUri() + folderName + "/" + fileName;
    }
    
    public String getFilePath() {
        return _getItem("project_root");
    }
    
    public String getProjectFilePath(String projectName) {
        return getFilePath() + projectName + "/";
    }
    
    public String getOutputCSVExt() {
        return _getItem("output_file_csv_ext");
    }
    
    public String getOuputLigandsZipExt() {
        return _getItem("output_file_ligands_ext");
    }
    
    public String getOutputUnzipLocation(String folderName) {
        return getProjectFilePath(folderName) + _getItem("unzip_location") + "/";
    }
    
    public String getWebDavPath(String folderName) {
        return getWebDavUri() + folderName + "/";
    }
    
    public String getExternalWebDavPath(String folderName) {
        return getExternalWebDavUri() + folderName + "/";
    }
    
    public String getOutputFileName(String folderName) {
        return getWebDavUri() + folderName + "/" + _getItem("output_file_name") + _getItem("output_file_ext");
    }
    
    public String getExternalOutputFileName(String folderName) {
        return getExternalWebDavUri() + folderName + "/" + _getItem("output_file_name") + _getItem("output_file_ext");
    }
    
    public String getOutputFilePath(String folderName) {
        return getFilePath() + folderName + "/" + _getItem("output_file_name") + _getItem("output_file_ext");
    }
    
    public String getExternalUnzippedOutputPath(String folderName) {
    	return getExternalWebDavPath(folderName) + _getItem("unzip_location");
    }
    
    public String getLigandPath() {
        return _getItem("ligand_path");
    }
    
    public String getLigandFileName(String folderName, String fileName) {
        return getLigandPath() + folderName + "/" + fileName;
    }
    
    public String getLigandCache() {
        return _getItem("ligand_cache_path");
    }
    
    public String getLigandsZipFileName() {
        return _getItem("zip_file_name") + getLigandsZipExt();
    }
    
    public String getPilotLigandsZipFileName() {
        return _getItem("pilot_zip_file_name") + getLigandsZipExt();
    }
    
    public String getLigandsZipExt() {
        return _getItem("zip_file_ext");
    }
    
    public String getReceptorFileName() {
        return _getItem("receptor_file_name") + getReceptorExt();
    }
    
    public String getReceptorExt() {
        return _getItem("receptor_file_ext");
    }
    
    public String getConfigFileName() {
        return _getItem("config_file_name") + getConfigExt();
    }
    
    public String getConfigExt() {
        return _getItem("config_file_ext");
    }
    
    public String getProcessingWSDL() {
        return _getItem("processing_wsdl");
    }
    
    public int getPilotLigandCount() {
    	return new Integer(_getItem("pilot_ligand_count"));
    }
}
