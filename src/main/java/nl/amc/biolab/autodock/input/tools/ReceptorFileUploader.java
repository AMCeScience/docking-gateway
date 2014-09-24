package nl.amc.biolab.autodock.input.tools;

import java.io.File;
import nl.amc.biolab.autodock.input.objects.Receptor;
import nl.amc.biolab.autodock.constants.VarConfig;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Allard
 */
public class ReceptorFileUploader extends VarConfig {    
    public ReceptorFileUploader() {}
    
    public Receptor doUpload(FileItem receptorFile, String project_folder) {
        Receptor receptor = new Receptor();
        
        try {
            log.log(receptorFile);
            
            if (receptorFile != null && receptorFile.getSize() > 0) {
                log.log("Receptor file size: " + receptorFile.getSize());
                
                File receptor_file = new File(config.getProjectFilePath(project_folder) + config.getReceptorFileName());

                // Create new file on the system
                receptor_file.createNewFile();
            
                // Write upload to created file on system
                receptorFile.write(receptor_file);
                
                log.log("Receptor file uploaded in: " + config.getProjectFilePath(project_folder) + config.getReceptorFileName());
                
                if (receptor_file.exists()) {
                    receptor.setValid(true);
                } else {                    
                    receptor.setError("Receptor file on server could not be created.");
                }
            } else {                
                receptor.setError("Error while uploading file, either file not found or filesize is 0.");
            }
        } catch (Exception e) {
            log.log(e);
        }
        
        return receptor;
    }
}
