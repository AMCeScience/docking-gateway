package nl.amc.biolab.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.amc.biolab.autodock.constants.VarConfig;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author Allard
 */
public class Unzipper extends VarConfig {
    public Unzipper() {}
    
    /**
     * Unzip a specific project that is on the server
     * 
     * @param projectName name of the project as String
     */
    public void unzipProjectOutput(String projectName) {
        unzip(config.getProjectFilePath(projectName), config.getOutputFilePath(projectName));
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
