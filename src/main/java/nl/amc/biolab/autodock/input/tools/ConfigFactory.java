package nl.amc.biolab.autodock.input.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import nl.amc.biolab.autodock.input.objects.Configuration;
import nl.amc.biolab.autodock.input.objects.PointIn3DSpace;
import nl.amc.biolab.autodock.constants.VarConfig;

/**
 *
 * @author Allard van Altena
 */
public class ConfigFactory extends VarConfig {    
    public ConfigFactory() {}
    
    public Configuration setData(HashMap<String, String> formMap) {
        Configuration file = new Configuration();
        
        String project_name = formMap.get("project_name").toString();
        
        // Validate project_name string
        if (project_name.isEmpty()) {
            file.setError("Project name can not be empty.<br/>");
            
            return file;
        }
        
        // Create point object for center
        PointIn3DSpace center = new PointIn3DSpace("center");
        center.set_x(formMap.get("center_x").toString());
        center.set_y(formMap.get("center_y").toString());
        center.set_z(formMap.get("center_z").toString());
        
        // Validate center object
        if (!center.validate()) {
            file.setError(center.getErrors() + "<br/>");
        }
        
        file.setCenter(center);
        
        // Create point object for size
        PointIn3DSpace size = new PointIn3DSpace("size");
        size.set_x(formMap.get("size_x").toString());
        size.set_y(formMap.get("size_y").toString());
        size.set_z(formMap.get("size_z").toString());

        // Validate size object
        if (!size.validate()) {
            file.setError(size.getErrors() + "<br/>");
        }
        
        file.setSize(size);
        
        // Get filenames for configuration file and receptor file
        String configFilePath = config.getProjectFilePath(project_name) + config.getConfigFileName();
        String receptor_filename = config.getReceptorFileName();
        
        file.setFilePath(configFilePath);
        
        // Set configuration items
        file.setReceptor(receptor_filename);
        
        String energy_range = formMap.get("energy_range").toString();
        String number_of_runs = formMap.get("number_runs").toString();
        String exhaustiveness = formMap.get("exhaustiveness").toString();
        
        file.setEnergyRange(energy_range);
        file.setNumberOfRuns(number_of_runs);
        file.setExhaustiveness(exhaustiveness);
        
        return file;
    }
    
    public boolean writeToDisk(Configuration file) {
        try {
            // Check if errors exist
            if (file.validate()) {                
                // Create file on disk
                File destination_file = new File(file.getFilePath());
                
                // Create bufferedwriter
                BufferedWriter out = new BufferedWriter(new FileWriter(destination_file));
                
                out.write("receptor = " + file.getReceptor());
                out.newLine();
                out.newLine();

                file.getCenter().write_point(out);
                out.newLine();

                file.getSize().write_point(out);
                out.newLine();
                
                if (!file.getNumberOfRuns().isEmpty()) {
                    out.write("num_modes = " + file.getNumberOfRuns());
                    out.newLine();
                }
                
                if (!file.getExhaustiveness().isEmpty()) {
                    out.write("exhaustiveness = " + file.getExhaustiveness());
                    out.newLine();
                }
                
                if (!file.getEnergyRange().isEmpty()) {
                    out.write("energy_range = " + file.getEnergyRange());
                }
                
                out.flush();
                out.close();
                
                log.log("Config file printed in: " + file.getFilePath());
                
                return true;
            } else {
                file.setError("Could not validate form.<br/>");
            }
        } catch(IOException e) {
            log.log(e);
        }
        
        return false;
    }
}
