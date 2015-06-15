package nl.amc.biolab.autodock.input.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.input.objects.Configuration;
import nl.amc.biolab.autodock.input.objects.PointIn3DSpace;
import docking.crappy.logger.Logger;

/**
 *
 * @author Allard van Altena
 */
public class ConfigFactory {
	private static final String ENERGY_RANGE = "20";
	
    public ConfigFactory() {}
    
    public Configuration setData(HashMap<String, Object> formMap, String project_folder) {
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
        
        // Set configuration items
        file.setFilePath(VarConfig.getProjectFilePath(project_folder) + VarConfig.getConfigFileName());
        
        //String energy_range = formMap.get("energy_range").toString();
        String number_of_runs = formMap.get("number_runs").toString();
        String exhaustiveness = formMap.get("exhaustiveness").toString();
        
        file.setEnergyRange(ENERGY_RANGE);
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
                
                Logger.log("Config file printed in: " + file.getFilePath(), Logger.debug);
                
                return true;
            } else {
                file.setError("Could not validate form.<br/>");
            }
        } catch(IOException e) {
            Logger.log(e, Logger.exception);
        }
        
        return false;
    }
}
