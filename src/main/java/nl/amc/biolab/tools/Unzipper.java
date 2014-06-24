package nl.amc.biolab.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.amc.biolab.autodock.constants.VarConfig;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Allard
 */
public class Unzipper extends VarConfig {
    public Unzipper() {}
    
    /**
     * Untar a specific project that is on the server
     * 
     * @param projectName name of the project as String
     */
    public void untarProjectOutput(String projectName) {
    	try {
    		log.log("looking in: " + config.getOutputFilePath(projectName));
    		
	    	TarArchiveInputStream tin = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(new File(config.getOutputFilePath(projectName)))));
	
	    	log.log("got stream");
	    	
	        TarArchiveEntry entry;
	        
	        log.log("creating unzip location at: " + config.getOutputUnzipLocation(projectName));
	        
	        log.log("folder created: " + new File(config.getOutputUnzipLocation(projectName)).mkdirs());
	        
	        while ((entry = tin.getNextTarEntry()) != null) {
	        	// create a file with the same name as the entry
	            File destPath = new File(config.getOutputUnzipLocation(projectName), entry.getName());
	            
	            if (entry.isDirectory()) {
		            // entry is directory, create the folders
	                destPath.mkdirs();
	            } else {
	            	// entry is file, write the file
	                destPath.createNewFile();
	                
	                byte [] bytes = new byte[1024];
	                
	                BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
	                int len = 0;
	
	                while((len = tin.read(bytes)) != -1) {
	                    bout.write(bytes, 0, len);
	                }
	                
	                bout.close();
	                bytes = null;
	            }
	        }
	        
	        tin.close();
    	} catch (IOException e) {
    		log.log(e);
    	}
    }
    
    /**
     * Unzip specific path to output path
     * 
     * @param outputPath path to write the file to
     * @param zipPath path where the zip file exists
     * @return returns success as boolean
     */
    public boolean unzip(String outputPath, String zipPath) {    
        try {
            ZipFile outputZip = new ZipFile(zipPath);

            for (@SuppressWarnings("rawtypes")
			Enumeration zipEntries = outputZip.entries(); zipEntries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) zipEntries.nextElement();

                if (entry.isDirectory()) {
                    // zip inside zip with ligands
                    (new File(outputPath + "/" + entry.getName())).mkdir();
                    continue;
                }

                // write to file
                IOUtils.copy(outputZip.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(outputPath + "/" + entry.getName())));
            }

            outputZip.close();
        } catch(IOException e) {
            log.log(e);
            
            return false;
        }
        
        return true;
    }
    
    public void unzipLigands() {
    	
    }
}
