package nl.amc.biolab.autodock.input.tools;

import java.io.File;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.input.objects.Receptor;

import org.apache.commons.fileupload.FileItem;

import docking.crappy.logger.Logger;

/**
 *
 * @author Allard
 */
public class ReceptorFileUploader {    
    public ReceptorFileUploader() {}
    
    public Receptor doUpload(FileItem receptorFile, String project_folder) {
        Receptor receptor = new Receptor();
        
        try {
            Logger.log(receptorFile, Logger.debug);
            
            if (receptorFile != null && receptorFile.getSize() > 0) {
                Logger.log("Receptor file size: " + receptorFile.getSize(), Logger.debug);
                
                File receptor_file = new File(VarConfig.getProjectFilePath(project_folder) + VarConfig.getReceptorFileName());

                // Create new file on the system
                receptor_file.createNewFile();
            
                // Write upload to created file on system
                receptorFile.write(receptor_file);
                
                Logger.log("Receptor file uploaded in: " + VarConfig.getProjectFilePath(project_folder) + VarConfig.getReceptorFileName(), Logger.debug);
                
                if (receptor_file.exists()) {
                    receptor.setValid(true);
                } else {                    
                    receptor.setError("Receptor file on server could not be created.");
                }
            } else {                
                receptor.setError("Error while uploading file, either file not found or filesize is 0.");
            }
        } catch (Exception e) {
            Logger.log(e, Logger.exception);
        }
        
        return receptor;
    }
}
