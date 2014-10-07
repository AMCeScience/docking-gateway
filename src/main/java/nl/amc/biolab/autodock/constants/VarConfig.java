package nl.amc.biolab.autodock.constants;

import nl.amc.biolab.config.exceptions.ReaderException;
import nl.amc.biolab.config.manager.ConfigurationManager;
import docking.crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class VarConfig extends Logger {
	private final String file_path = "guse/apache-tomcat-6.0.36/webapps/config.json";
	private final String app_name = "docking";
	private ConfigurationManager config_file;
    
    public VarConfig config;
    
    /**
     * Constructor which reads the configuration file, also exposes the config variable to all extending classes
     */
    public VarConfig() {
    	try {
			this.config_file = new ConfigurationManager(this.file_path);
		} catch (ReaderException e) {
			log(e.getMessage());
		}
    	
    	config = this;
    }
        
    /**
     * Get configuration item with certain name in String.
     * @param name Name of configuration item we are looking for
     * @return String of configuration item belonging to the input 'name'
     * @throws Exception Throws exception when name does not exist in file
     */
    public String getItem(String name) {
    	try {
    		return this.config_file.read.getStringItem(this.app_name, name);
    	} catch(ReaderException e) {
    		log(e.getMessage());
    	}
    	
    	return null;
    }
    
    /**
     * Get boolean whether if the site is in development mode
     * @return Boolean whether if the site is in development mode
     */
    public boolean getIsDev() {
    	try {
			return this.config_file.read.getBooleanItem("is_dev");
		} catch (ReaderException e) {
			log(e.getMessage());
		}
    	
    	return false;
    }
    
    public String getAutodockName() {
        return getItem("autodock_name");
    }
    
    public String getWebDavUri() {
        return getItem("webdav_internal");
    }
    
    public String getExternalWebDavUri() {
        return getItem("webdav_external");
    }
    
    public String getUri(String folderName, String fileName) {
        return getWebDavUri() + folderName + "/" + fileName;
    }
    
    public String getFilePath() {
        return getItem("project_root");
    }
    
    public String getProjectFilePath(String projectName) {
        return getFilePath() + projectName + "/";
    }
    
    public String getOutputCSVExt() {
        return getItem("output_file_csv_ext");
    }
    
    public String getOuputLigandsZipExt() {
        return getItem("output_file_ligands_ext");
    }
    
    public String getOutputUnzipLocation(String folderName) {
        return getProjectFilePath(folderName) + getItem("unzip_location") + "/";
    }
    
    public String getWebDavPath(String folderName) {
        return getWebDavUri() + folderName + "/";
    }
    
    public String getExternalWebDavPath(String folderName) {
        return getExternalWebDavUri() + folderName + "/";
    }
    
    public String getOutputFileName(String folderName) {
        return getWebDavUri() + folderName + "/" + getItem("output_file_name") + getItem("output_file_ext");
    }
    
    public String getExternalOutputFileName(String folderName) {
        return getExternalWebDavUri() + folderName + "/" + getItem("output_file_name") + getItem("output_file_ext");
    }
    
    public String getOutputFilePath(String folderName) {
        return getFilePath() + folderName + "/" + getItem("output_file_name") + getItem("output_file_ext");
    }
    
    public String getExternalUnzippedOutputPath(String folderName) {
    	return getExternalWebDavPath(folderName) + getItem("unzip_location");
    }
    
    public String getLigandPath() {
        return getItem("ligand_path");
    }
    
    public String getLigandFileName(String folderName, String fileName) {
        return getLigandPath() + folderName + "/" + fileName;
    }
    
    public String getLigandCache() {
        return getItem("ligand_cache_path");
    }
    
    public String getLigandsZipFileName() {
        return getItem("zip_file_name") + getLigandsZipExt();
    }
    
    public String getPilotLigandsZipFileName() {
        return getItem("pilot_zip_file_name") + getLigandsZipExt();
    }
    
    public String getLigandsZipExt() {
        return getItem("zip_file_ext");
    }
    
    public String getReceptorFileName() {
        return getItem("receptor_file_name") + getReceptorExt();
    }
    
    public String getReceptorExt() {
        return getItem("receptor_file_ext");
    }
    
    public String getConfigFileName() {
        return getItem("config_file_name") + getConfigExt();
    }
    
    public String getConfigExt() {
		return getItem("config_file_ext");
    }
    
    public String getOutputFileName() {
    	return getItem("output_file_name") + getOutputExt();
    }
    
    public String getOutputExt() {
    	return getItem("output_file_ext");
    }
    
    public String getProcessingResource() {
    	return getItem("processing_resource");
    }
    
    public Integer getPilotLigandCount() {
    	try {
			return this.config_file.read.getIntegerItem(app_name, "pilot_ligand_count");
		} catch (ReaderException e) {
			log(e.getMessage());
		}
    	
    	return null;
    }
    
    public Integer getItemsPerPage() {
    	try {
			return this.config_file.read.getIntegerItem(app_name, "items_per_page");
		} catch (ReaderException e) {
			log(e.getMessage());
		}
    	
    	return null;
    }
    
    /**
     * Get formatted database connection url
     * @return Formatted database connection url
     */
    public String getDbConnectionUrl() {
    	return getItem("neuro_db");
    }
    
    /**
     * Get formatted database connection url
     * @return Formatted database connection url
     */
    public String getLiferayDbConnectionUrl() {
    	return getItem("liferay_db");
    }
}
