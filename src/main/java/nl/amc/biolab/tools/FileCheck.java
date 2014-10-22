package nl.amc.biolab.tools;

import java.io.File;
import java.io.FileFilter;
/**
 *
 * @author Allard
 */
public class FileCheck {
    public FileCheck() {}
    
    public File[] getFilesWithExtension(String folderPath, final String extension) {
        return _fileLoop(folderPath, extension);
    }
    
    public boolean checkIfFilesWithExtensionExists(String folderPath, final String extension) {        
        File[] projFiles = _fileLoop(folderPath, extension);
        
        return projFiles != null && projFiles.length > 0;
    }
    
    private File[] _fileLoop(String folderPath, final String extension) {
        // Check if file with extension exists
        File[] projFiles = new File(folderPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {                
                return pathname.getName().endsWith(extension);
            }
        });
        
        return projFiles;
    }
}
