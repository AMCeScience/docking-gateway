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
				String library_name = obj.toString();
				File library_file = new File(VarConfig.getLigandPath() + library_name + ".zip");

				// Count the ligands
				ZipFile zipCount = new ZipFile(library_file);
				ligandsObj.addCount(zipCount.size());
				zipCount.close();

				// Copy the library to the project folder
				Files.copy(library_file, new File(VarConfig.getProjectFilePath(project_folder) + VarConfig.getLigandsZipFileName()));

				if (isPilot) {
					// Create stream
					String pilotZipFilePath = VarConfig.getProjectFilePath(project_folder) + VarConfig.getPilotLigandsZipFileName();
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
				}
			}

			Logger.log("Ligands zip created in: " + VarConfig.getProjectFilePath(project_folder) + VarConfig.getLigandsZipFileName(), Logger.debug);

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

	// public Ligands prepareLigandFile(HashMap<String, Object> formMap, String
	// project_folder) {
	// JSONParser parser = new JSONParser();
	//
	// Ligands ligandsObj = new Ligands();
	//
	// ligandsObj.setValid(false);
	//
	// try {
	// JSONObject libraryJSON = (JSONObject)
	// parser.parse(formMap.get("library_list").toString());
	// JSONArray libraryArray = (JSONArray) libraryJSON.get("library_array");
	//
	// boolean isPilot = formMap.containsKey("run_pilot") &&
	// formMap.get("run_pilot").equals("1") ? true : false;
	// int pilotLigandCount = VarConfig.getPilotLigandCount();
	//
	// // Create streams
	// String zipFilePath = VarConfig.getProjectFilePath(project_folder) +
	// VarConfig.getLigandsZipFileName();
	// String pilotZipFilePath = VarConfig.getProjectFilePath(project_folder) +
	// VarConfig.getPilotLigandsZipFileName();
	//
	// ZipOutputStream zip = new ZipOutputStream(new
	// FileOutputStream(zipFilePath));
	// ZipOutputStream pilotZip = new ZipOutputStream(new
	// FileOutputStream(pilotZipFilePath));
	//
	// for (Object obj : libraryArray) {
	// String folder = obj.toString();
	// File library_folder = new File(VarConfig.getLigandPath() + folder);
	//
	// if (library_folder.isDirectory()) {
	// for (File ligand : library_folder.listFiles()) {
	// FileInputStream fin = new FileInputStream(ligand);
	//
	// zip.putNextEntry(new ZipEntry(ligand.getName()));
	//
	// if (isPilot && ligandsObj.getPilotCount() < pilotLigandCount) {
	// pilotZip.putNextEntry(new ZipEntry(ligand.getName()));
	// }
	//
	// int length;
	//
	// byte[] b = new byte[1024];
	//
	// while((length = fin.read(b)) > 0) {
	// zip.write(b, 0, length);
	//
	// if (isPilot && ligandsObj.getPilotCount() < pilotLigandCount) {
	// pilotZip.write(b, 0, length);
	// }
	// }
	//
	// ligandsObj.addCount();
	//
	// zip.closeEntry();
	//
	// if (isPilot && ligandsObj.getPilotCount() < pilotLigandCount) {
	// ligandsObj.addPilotCount();
	//
	// pilotZip.closeEntry();
	// }
	//
	// fin.close();
	// }
	// }
	// }
	//
	// zip.close();
	//
	// if (isPilot) {
	// pilotZip.close();
	// }
	//
	// Logger.log("Ligands zip created in: " + zipFilePath, Logger.debug);
	//
	// ligandsObj.setValid(true);
	// } catch (ParseException e) {
	// Logger.log(e, Logger.exception);
	// } catch (FileNotFoundException e) {
	// Logger.log(e, Logger.exception);
	// } catch (IOException e) {
	// Logger.log(e, Logger.exception);
	// }
	//
	// return ligandsObj;
	// }
}
