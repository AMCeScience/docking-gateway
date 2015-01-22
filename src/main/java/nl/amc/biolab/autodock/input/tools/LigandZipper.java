package nl.amc.biolab.autodock.input.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.input.objects.Ligands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.io.Files;

import docking.crappy.logger.Logger;

public class LigandZipper {
	public LigandZipper() {
	}

	public Ligands prepareLigandFile(HashMap<String, Object> formMap, String project_folder) {
		JSONParser parser = new JSONParser();

		Ligands ligandsObj = new Ligands();

		ligandsObj.setValid(false);

		try {
			JSONObject libraryJSON = (JSONObject) parser.parse(formMap.get("library_list").toString());
			JSONArray libraryArray = (JSONArray) libraryJSON.get("library_array");

			boolean isPilot = formMap.containsKey("run_pilot") && formMap.get("run_pilot").equals("1") ? true : false;

			if (libraryArray.size() > 1) {
				ligandsObj.setError("Can only select one library for now.");

				return ligandsObj;
			}

			for (Object obj : libraryArray) {
				String library_name = obj.toString() + ".zip";
				ligandsObj.setFileName(library_name);
				
				File library_file = new File(VarConfig.getLigandPath() + library_name);

				// Count the ligands
				ZipFile zipCount = new ZipFile(library_file);
				ligandsObj.addCount(zipCount.size());
				zipCount.close();

				// Copy the library to the project folder
				Files.copy(library_file, new File(VarConfig.getProjectFilePath(project_folder) + library_name));

				if (isPilot) {
					String pilot_library_name = "pilot_" + library_name;
					ligandsObj.setPilotFileName(pilot_library_name);
					
					// Create stream
					String pilotZipFilePath = VarConfig.getProjectFilePath(project_folder) + pilot_library_name;
					ZipOutputStream pilotZip = new ZipOutputStream(new FileOutputStream(pilotZipFilePath));
					
					// get the zip file content
					ZipInputStream library_stream = new ZipInputStream(new FileInputStream(library_file));
					ZipEntry ligand_zip_entry = library_stream.getNextEntry();
					
					// Loop ligand files and count the pilot library size
					while (ligand_zip_entry != null && ligandsObj.getPilotCount() < VarConfig.getPilotLigandCount()) {
						pilotZip.putNextEntry(ligand_zip_entry);

						int length;
						byte[] b = new byte[1024];

						while ((length = library_stream.read(b)) > 0) {
							pilotZip.write(b, 0, length);
						}

						ligandsObj.addPilotCount();

						pilotZip.closeEntry();
						
						// Go to next file
						ligand_zip_entry = library_stream.getNextEntry();
					}
					
					// Close the streams
					library_stream.closeEntry();
					library_stream.close();
					
					pilotZip.close();
					
					Logger.log("Pilot ligands zip created in: " + VarConfig.getProjectFilePath(project_folder) + pilot_library_name, Logger.debug);
				}
				
				Logger.log("Ligands zip created in: " + VarConfig.getProjectFilePath(project_folder) + library_name, Logger.debug);
			}

			ligandsObj.setValid(true);
		} catch (ParseException e) {
			Logger.log(e, Logger.exception);
		} catch (FileNotFoundException e) {
			Logger.log(e, Logger.exception);
		} catch (IOException e) {
			Logger.log(e, Logger.exception);
		}

		return ligandsObj;
	}
}
