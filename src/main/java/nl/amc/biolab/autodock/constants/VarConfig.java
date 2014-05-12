package nl.amc.biolab.autodock.constants;

import crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class VarConfig extends Logger {
    private final String PROJECT_ROOT = "guse/apache-tomcat-6.0.36/webapps/autodock/projects/";
    private final String LIGAND_PATH = "guse/apache-tomcat-6.0.36/webapps/autodock/ligands/";
    private final String LIGAND_CACHE_PATH = "guse/apache-tomcat-6.0.36/webapps/autodock/ligandsCache.json";
    private final String WEBDAV = "http://localhost/webdav/";
    private final String PROCESSING_WSDL = "http://localhost:8080/processingmanagerPortlet/ProcessingManagerService?wsdl";
    
    private final String ZIP_FILE_NAME = "compounds_file";
    private final String ZIP_FILE_EXT = ".zip";
    
    private final String RECEPTOR_FILE_NAME = "receptor_file";
    private final String RECEPTOR_FILE_EXT = ".pdbqt";
    
    private final String CONFIG_FILE_NAME = "config_file";
    private final String CONFIG_FILE_EXT = ".txt";
    
    private final String OUTPUT_FILE_NAME = "output";
    private final String OUTPUT_FILE_EXT = ".tar.gz";
    
    private final String OUTPUT_FILE_CSV_EXT = ".csv";
    private final String OUTPUT_FILE_LIGANDS_EXT = ".tar.gz";
    
    private final String UNZIP_LOCATION = "unzipped";
    
    private final String AUTODOCK_NAME = "Autodock";
    
    public VarConfig config;
    
    public VarConfig() {
        config = this;
    }
    
    public String getAutodockName() {
        return AUTODOCK_NAME;
    }
    
    public String getWebDavUri() {
        return WEBDAV;
    }
    
    public String getUri(String folderName, String fileName) {
        return WEBDAV + folderName + "/" + fileName;
    }
    
    public String getFilePath() {
        return PROJECT_ROOT;
    }
    
    public String getProjectFilePath(String projectName) {
        return PROJECT_ROOT + projectName + "/";
    }
    
    public String getOutputCSVExt() {
        return OUTPUT_FILE_CSV_EXT;
    }
    
    public String getOuputLigandsZipExt() {
        return OUTPUT_FILE_LIGANDS_EXT;
    }
    
    public String getOutputUnzipLocation(String folderName) {
        return getOutputPath(folderName) + "/" + UNZIP_LOCATION + "/";
    }
    
    public String getOutputPath(String folderName) {
        return WEBDAV + folderName;
    }
    
    public String getOutputFileName(String folderName) {
        return WEBDAV + folderName + "/" + OUTPUT_FILE_NAME + OUTPUT_FILE_EXT;
    }
    
    public String getOutputFilePath(String folderName) {
        return PROJECT_ROOT + folderName + "/" + OUTPUT_FILE_NAME + OUTPUT_FILE_EXT;
    }
    
    public String getLigandPath() {
        return LIGAND_PATH;
    }
    
    public String getLigandFileName(String folderName, String fileName) {
        return LIGAND_PATH + folderName + "/" + fileName;
    }
    
    public String getLigandCache() {
        return LIGAND_CACHE_PATH;
    }
    
    public String getLigandsZipFileName() {
        return ZIP_FILE_NAME + ZIP_FILE_EXT;
    }
    
    public String getLigandsZipExt() {
        return ZIP_FILE_EXT;
    }
    
    public String getReceptorFileName() {
        return RECEPTOR_FILE_NAME + RECEPTOR_FILE_EXT;
    }
    
    public String getReceptorExt() {
        return RECEPTOR_FILE_EXT;
    }
    
    public String getConfigFileName() {
        return CONFIG_FILE_NAME + CONFIG_FILE_EXT;
    }
    
    public String getConfigExt() {
        return CONFIG_FILE_EXT;
    }
    
    public String getProcessingWSDL() {
        return PROCESSING_WSDL;
    }
}
