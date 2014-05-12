/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.amc.biolab.tools;

import nl.amc.biolab.autodock.constants.VarConfig;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Allard
 */
public class Unzipper extends VarConfig {
    public Unzipper() {}
    
    public void unzipProjectOutput(String projectName) {
        unzip(config.getProjectFilePath(projectName), config.getOutputFilePath(projectName));
    }
    
    public boolean unzip(String outputPath, String zipPath) {    
        try {
            ZipFile outputZip = new ZipFile(zipPath);

            for (Enumeration zipEntries = outputZip.entries(); zipEntries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) zipEntries.nextElement();

                if (entry.isDirectory()) {
                    // zip inside zip with ligands
                    (new File(outputPath + "/" + entry.getName())).mkdir();
                    continue;
                }

                copyInputStream(outputZip.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(outputPath + "/" + entry.getName())));
            }

            outputZip.close();
        } catch(IOException e) {
            log.log(e);
            
            return false;
        }
        
        return true;
    }
    
    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        
        while((length = in.read(buffer)) >= 0) {
            out.write(buffer, 0, length);
        }
        
        in.close();
        out.close();
    }
}
