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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import docking.crappy.logger.Logger;

/**
 *
 * @author Allard
 */
public class Unzipper {
    public Unzipper() {}
    
    public boolean untarSpecificFile(String outputPath, String tarPath, String fileName) {
    	try {
	    	TarArchiveInputStream tin = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(new File(tarPath))));
	
	        TarArchiveEntry entry;
	        
	        while ((entry = tin.getNextTarEntry()) != null) {
	        	if (entry.getName().equals(fileName)) {
	        		boolean created = new File(outputPath).mkdirs();
	        		
	        		if (created) {
		        		// create a file with the same name as the entry
			        	File destPath = new File(outputPath, entry.getName());
			        	
		        		destPath.createNewFile();
		                
		                byte [] bytes = new byte[1024];
		                
		                BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
		                int len = 0;
		
		                while((len = tin.read(bytes)) != -1) {
		                    bout.write(bytes, 0, len);
		                }
		                
		                bout.close();
		                tin.close();
		                
		                // Exit the function if the file was found and written
		                return true;
	        		} else {
	        			Logger.log("Failed to create folders for unzipping", 2);
	        		}
	        	}
	        }
	        
	        tin.close();
    	} catch (IOException e) {
    		Logger.log(e, Logger.exception);
    		
    		return false;
    	}
    	
    	return false;
    }
    
    /**
     * Untar specific path to output path
     * 
     * @param outputPath path to write the file to
     * @param tarPath path where the tar file exists
     * @return returns success as boolean
     */
    public boolean untar(String outputPath, String tarPath) {
    	try {
    		Logger.log("looking in: " + tarPath, Logger.debug);
    		
	    	TarArchiveInputStream tin = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(new File(tarPath))));
	    	
	    	Logger.log("got stream", Logger.debug);
	        Logger.log("creating unzip location at: " + outputPath, Logger.debug);
	        
	        boolean created = new File(outputPath).mkdirs();
	        
	        Logger.log("folder created: " + created, Logger.debug);
	        
	        TarArchiveEntry entry;
	        
	        while ((entry = tin.getNextTarEntry()) != null) {
	        	// create a file with the same name as the entry
	            File destPath = new File(outputPath, entry.getName());
	            
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
    		Logger.log(e, Logger.exception);
    		
    		return false;
    	}
    	
    	return true;
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
        	Logger.log(e, Logger.exception);
            
            return false;
        }
        
        return true;
    }
}
