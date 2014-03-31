package nl.amc.biolab.autodock.input.tools;

import java.io.FileInputStream;
import crappy.logger.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.amc.biolab.autodock.input.objects.Ligands;
import nl.amc.biolab.autodock.constants.VarConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Allard van Altena
 */
public class LigandZipper extends Logger {
    public LigandZipper() {}
    
    public Ligands prepareLigandFile(HashMap formMap) {
        JSONParser parser = new JSONParser();
        
        Ligands ligandsObj = new Ligands();
        
        ligandsObj.setValid(false);
        
        try {
            JSONObject ligandJSON = (JSONObject) parser.parse(formMap.get("compound_list").toString());
            JSONArray ligandArray = (JSONArray) ligandJSON.get("compound_array");
            
            VarConfig config = new VarConfig();
            
            String zipFilePath = config.getProjectFilePath(formMap.get("project_name").toString()) + config.getLigandsZipFileName();
            
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFilePath));

            for (Object obj : ligandArray) {
                JSONObject ligand = (JSONObject) obj;
                
                Iterator ligandIter = ligand.keySet().iterator();
                
                String folder = ligandIter.next().toString();
                String fileName = ligand.get(folder).toString();
                
                FileInputStream fin = new FileInputStream(new File(config.getLigandFileName(folder, fileName)));
                
                zip.putNextEntry(new ZipEntry(fileName));
                int length;

                byte[] b = new byte[1024];

                while((length = fin.read(b)) > 0) {
                    zip.write(b, 0, length);
                }
                
                ligandsObj.addCount();
                
                zip.closeEntry();
                fin.close();
            }

            zip.close();
            
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
