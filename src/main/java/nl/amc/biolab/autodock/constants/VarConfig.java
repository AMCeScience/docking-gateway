package nl.amc.biolab.autodock.constants;

import nl.amc.biolab.config.exceptions.ReaderException;
import nl.amc.biolab.config.manager.ConfigurationManager;
import docking.crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class VarConfig {
	private static final String app_name = "docking";
        
    /**
     * Get configuration item with certain name in String.
     * @param name Name of configuration item we are looking for
     * @return String of configuration item belonging to the input 'name'
     * @throws Exception Throws exception when name does not exist in file
     */
    public static String getItem(String name) {
    	try {
    		return ConfigurationManager.read.getStringItem(VarConfig.app_name, name);
    	} catch(ReaderException e) {
    		Logger.log(e, Logger.exception);
    	}
    	
    	return null;
    }
    
    /**
     * Get boolean whether if the site is in development mode
     * @return Boolean whether if the site is in development mode
     */
    public static boolean getIsDev() {
    	try {
			return ConfigurationManager.read.getBooleanItem("is_dev");
		} catch (ReaderException e) {
			Logger.log(e, Logger.exception);
		}
    	
    	return false;
    }
    
    public static String getAutodockName() {
        return getItem("autodock_name");
    }
    
    public static String getWebDavUri() {
        return getItem("webdav_internal");
    }
    
    public static String getExternalWebDavUri() {
        return getItem("webdav_external");
    }
    
    public static String getUri(String folderName, String fileName) {
        return getWebDavUri() + folderName + "/" + fileName;
    }
    
    public static String getFilePath() {
        return getItem("project_root");
    }
    
    public static String getProjectFilePath(String projectName) {
        return getFilePath() + projectName + "/";
    }
    
    public static String getOutputCSVName() {
    	return getItem("output_file_csv_name") + getItem("output_file_csv_ext");
    }
    
    public static String getOutputCSVExt() {
        return getItem("output_file_csv_ext");
    }
    
    public static String getOuputLigandsZipExt() {
        return getItem("output_file_ligands_ext");
    }
    
    public static String getOutputUnzipLocation(String folderName) {
        return getProjectFilePath(folderName) + getItem("unzip_location") + "/";
    }
    
    public static String getWebDavPath(String folderName) {
        return getWebDavUri() + folderName + "/";
    }
    
    public static String getExternalWebDavPath(String folderName) {
        return getExternalWebDavUri() + folderName + "/";
    }
    
    public static String getOutputFileName(String folderName) {
        return getWebDavUri() + folderName + "/" + getItem("output_file_name") + getItem("output_file_ext");
    }
    
    public static String getExternalOutputFileName(String folderName) {
        return getExternalWebDavUri() + folderName + "/" + getItem("output_file_name") + getItem("output_file_ext");
    }
    
    public static String getOutputFilePath(String folderName) {
        return getFilePath() + folderName + "/" + getItem("output_file_name") + getItem("output_file_ext");
    }
    
    public static String getExternalUnzippedOutputPath(String folderName) {
    	return getExternalWebDavPath(folderName) + getItem("unzip_location");
    }
    
    public static String getLigandPath() {
        return getItem("ligand_path");
    }
    
    public static String getLigandFileName(String folderName, String fileName) {
        return getLigandPath() + folderName + "/" + fileName;
    }
    
    public static String getLigandCache() {
        return getItem("ligand_cache_path");
    }
    
    public static String getLigandsZipFileName() {
        return getItem("zip_file_name") + getLigandsZipExt();
    }
    
    public static String getPilotLigandsZipFileName() {
        return getItem("pilot_zip_file_name") + getLigandsZipExt();
    }
    
    public static String getLigandsZipExt() {
        return getItem("zip_file_ext");
    }
    
    public static String getConfigFileName() {
        return getItem("config_file_name") + getConfigExt();
    }
    
    public static String getConfigExt() {
		return getItem("config_file_ext");
    }
    
    public static String getOutputFileName() {
    	return getItem("output_file_name") + getOutputExt();
    }
    
    public static String getOutputExt() {
    	return getItem("output_file_ext");
    }
    
    public static String getProcessingResource() {
    	return getItem("processing_resource");
    }
    
    public static Integer getPilotLigandCount() {
    	try {
			return ConfigurationManager.read.getIntegerItem(app_name, "pilot_ligand_count");
		} catch (ReaderException e) {
			Logger.log(e, Logger.exception);
		}
    	
    	return null;
    }
    
    public static Integer getItemsPerPage() {
    	try {
			return ConfigurationManager.read.getIntegerItem(app_name, "items_per_page");
		} catch (ReaderException e) {
			Logger.log(e, Logger.exception);
		}
    	
    	return null;
    }
    
    /**
     * Get formatted database connection url
     * @return Formatted database connection url
     */
    public static String getDbConnectionUrl() {
    	return getItem("neuro_db");
    }
    
    /**
     * Get formatted database connection url
     * @return Formatted database connection url
     */
    public static String getLiferayDbConnectionUrl() {
    	return getItem("liferay_db");
    }
}
