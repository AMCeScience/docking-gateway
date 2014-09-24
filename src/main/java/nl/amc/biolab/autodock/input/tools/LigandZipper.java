package nl.amc.biolab.autodock.input.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.input.objects.Ligands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LigandZipper extends VarConfig {
    public LigandZipper() {}
    
    public Ligands prepareLigandFile(HashMap<String, Object> formMap, String project_folder) {
        JSONParser parser = new JSONParser();
        
        Ligands ligandsObj = new Ligands();
        
        ligandsObj.setValid(false);
        
        try {
            JSONObject libraryJSON = (JSONObject) parser.parse(formMap.get("library_list").toString());
            JSONArray libraryArray = (JSONArray) libraryJSON.get("library_array");
            
            boolean isPilot = formMap.containsKey("run_pilot") && formMap.get("run_pilot").equals("1") ? true : false;
            int pilotLigandCount = config.getPilotLigandCount();
            
            // Create streams
            String zipFilePath = config.getProjectFilePath(project_folder) + config.getLigandsZipFileName();
            String pilotZipFilePath = config.getProjectFilePath(project_folder) + config.getPilotLigandsZipFileName();
            
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFilePath));
            ZipOutputStream pilotZip = new ZipOutputStream(new FileOutputStream(pilotZipFilePath));

            for (Object obj : libraryArray) {
            	String folder = obj.toString();
            	File library_folder = new File(config.getLigandPath() + folder);

            	if (library_folder.isDirectory()) {
            		for (File ligand : library_folder.listFiles()) {
	            		FileInputStream fin = new FileInputStream(ligand);
	            		
	            		zip.putNextEntry(new ZipEntry(ligand.getName()));
	            		
	            		if (isPilot && ligandsObj.getPilotCount() < pilotLigandCount) {
		            		pilotZip.putNextEntry(new ZipEntry(ligand.getName()));
	            		}
            		
	            		int length;
            	
	            		byte[] b = new byte[1024];
	            		
	            		while((length = fin.read(b)) > 0) {
	            			zip.write(b, 0, length);
	            			
	            			if (isPilot && ligandsObj.getPilotCount() < pilotLigandCount) {
	            				pilotZip.write(b, 0, length);
	            			}
            			}
	            			
            			ligandsObj.addCount();
            			
            			zip.closeEntry();
            			
            			if (isPilot && ligandsObj.getPilotCount() < pilotLigandCount) {
	            			ligandsObj.addPilotCount();
	            			
	            			pilotZip.closeEntry();
            			}
            			
            			fin.close();
        			}
                }
            }

            zip.close();
            
            if (isPilot) {
            	pilotZip.close();
            }
            
            log.log("Ligands zip created in: " + zipFilePath);
            
            ligandsObj.setValid(true);
        } catch (ParseException e) {
            log.log(e);
        } catch (FileNotFoundException e) {
            log.log(e);
        } catch (IOException e) {
            log.log(e);
        }
        
        return ligandsObj;
    }
}
