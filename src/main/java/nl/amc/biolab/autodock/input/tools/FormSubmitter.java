package nl.amc.biolab.autodock.input.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.input.objects.Configuration;
import nl.amc.biolab.autodock.input.objects.JobSubmission;
import nl.amc.biolab.autodock.input.objects.Ligands;
import nl.amc.biolab.autodock.input.objects.Receptor;
import nl.amc.biolab.datamodel.objects.Application;
import nl.amc.biolab.datamodel.objects.Project;
import nl.amc.biolab.datamodel.objects.User;
import nl.amc.biolab.datamodel.objects.Value;
import nl.amc.biolab.persistencemanager.PersistenceManagerPlugin;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;

import docking.crappy.logger.Logger;

public class FormSubmitter {
	private String ERRORS = "";
	private PersistenceManagerPlugin PERSISTENCE;

	public FormSubmitter() {
		_setDb(new PersistenceManagerPlugin());

		_getDb().init();
	}

	public void close() {
		_getDb().shutdown();
	}

	private void _cleanUp(JobSubmission job) {
		File directory = new File(VarConfig.getProjectFilePath(job.getProjectFolder()));

		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			Logger.log(e, Logger.exception);
		}
	}

	public boolean saveForm(ActionRequest formParameters) {
		// Determines the success of this method
		boolean stored = false;

		HashMap<String, Object> formMap = _createFormMap(formParameters);

		if (formMap != null) {
			JobSubmission job = new JobSubmission();

			job.setProjectFolder(formMap.get("project_name").toString());
			job.setProjectName(formMap.get("project_name").toString());
			job.setProjectDescription(formMap.get("project_description").toString());

			// Get liferay user ID
			String liferayUserId = formParameters.getRemoteUser();

			// Get catalog user
			User catalogUser = _getDb().get.user(liferayUserId);

			// Check if user exists in catalog database
			if (catalogUser != null) {
				File directory = new File(VarConfig.getProjectFilePath(job.getProjectFolder()));

				directory.mkdirs();

				// Project name is not set, return immediately
				if (!directory.exists()) {
					_setError("Could not create project folder.<br/>");

					close();

					return false;
				}

				// Create configuration file
				ConfigFactory configFactory = new ConfigFactory();
				Configuration configuration = configFactory.setData(formMap, job.getProjectFolder());

				Logger.log("config done", Logger.debug);

				if (!configuration.getValid()) {
					_setError(configuration.getErrors());

					close();
					_cleanUp(job);

					return false;
				}

				// Write ligands to zip file
				LigandZipper ligandZipper = new LigandZipper();
				Ligands ligands = ligandZipper.prepareLigandFile(formMap, job.getProjectFolder());

				Logger.log("ligands done", Logger.debug);

				if (!ligands.validate()) {
					close();
					_cleanUp(job);

					return false;
				}

				// Write the receptor file to the system
				ReceptorFileUploader uploader = new ReceptorFileUploader();
				Receptor receptor = uploader.doUpload((FileItem) formMap.get("receptor_file"), job.getProjectFolder());

				Logger.log("receptor done", Logger.debug);

				if (!receptor.validate()) {
					_setError(receptor.getErrors());

					close();
					_cleanUp(job);

					return false;
				}
				
				configuration.setReceptor(receptor.getName());

				if (!configFactory.writeToDisk(configuration)) {
					_setError(configuration.getErrors());

					close();
					_cleanUp(job);

					return false;
				}

				job.setLigandsUri(VarConfig.getUri(job.getProjectFolder(), VarConfig.getLigandsZipFileName()));
				job.setLigandsCount(ligands.getCount());

				if (formMap.containsKey("run_pilot") && formMap.get("run_pilot").equals("1")) {
					job.setPilotLigandsUri(VarConfig.getUri(job.getProjectFolder(), VarConfig.getPilotLigandsZipFileName()));
					job.setPilotLigandsCount(ligands.getPilotCount());
					job.setPilot(true);
				}

				job.setReceptorUri(VarConfig.getUri(job.getProjectFolder(), receptor.getName()));
				job.setConfigurationUri(VarConfig.getUri(job.getProjectFolder(), VarConfig.getConfigFileName()));

				job.setOutputUri(VarConfig.getUri(job.getProjectFolder(), VarConfig.getOutputFileName()));

				job.setUser(catalogUser);

				// Store project
				Project project = _saveProject(job);
				job.setProject(project);

				// Submit the job
				_submit(job);

				stored = true;
			} else {
				// Liferay user not found in catalog
				_setError("Liferay user not found in catalog database. You probably don't have permission to save projects<br/>");

				stored = false;
			}
		} else {
			// FormMap is empty
			_setError("formMap error.<br/>");

			stored = false;
		}

		// Close session before returning
		close();

		return stored;
	}

	private Project _saveProject(JobSubmission job) {
		// Create lists
		ArrayList<Application> apps = new ArrayList<Application>();
		ArrayList<Value> values = new ArrayList<Value>();
		// Add items
		apps.add(_getDb().getApplicationByName(VarConfig.getAutodockName()));

		values.add(_getDb().get.value(_getDb().insert.value("is_pilot", String.valueOf(job.isPilot()))));
		values.add(_getDb().get.value(_getDb().insert.value("folder_name", job.getProjectFolder())));

		Project project = _getDb().get.project(_getDb().insert.project(job.getProjectName(), job.getProjectDescription(), job.getUserString(), apps, values));

		return project;
	}

	@SuppressWarnings("unchecked")
	private void _submit(JobSubmission job) {
		// Send to processing manager
		JSONObject submission = new JSONObject();
		JSONArray wrapper = new JSONArray();
		JSONArray submits = new JSONArray();

		if (job.isPilot()) {
			submits.add(_createSubmissionMap(1, "ligands", job.getPilotLigandsUri()));
		} else {
			submits.add(_createSubmissionMap(1, "ligands", job.getLigandsUri()));
		}

		submits.add(_createSubmissionMap(2, "configuration", job.getConfigurationUri()));
		
		// Get the actual receptor name to display in the portlet
		String receptor_uri = job.getReceptorUri();
		
		submits.add(_createSubmissionMap(3, receptor_uri.substring(receptor_uri.lastIndexOf("/")), receptor_uri));
		submits.add(_createSubmissionMap(4, "output", job.getOutputUri()));

		wrapper.add(submits);

		Long appId = _getDb().getApplicationByName(VarConfig.getAutodockName()).getDbId();

		submission.put("applicationId", appId);
		submission.put("description", job.getProjectDescription());
		submission.put("userId", job.getUser().getDbId());
		submission.put("projectId", job.getProject().getDbId());
		submission.put("submission", wrapper);

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);

		// Logging
		client.addFilter(new LoggingFilter(System.out));

		WebResource webResource = client.resource(VarConfig.getProcessingResource());

		ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class, submission);

		Logger.log(response, Logger.debug);
	}

	private HashMap<String, Object> _createSubmissionMap(int portId, String name, String dataUri) {
		HashMap<String, Object> submit_map = new HashMap<String, Object>();

		submit_map.put("portId", portId);
		submit_map.put("name", name);
		submit_map.put("data", dataUri);

		return submit_map;
	}

	private HashMap<String, Object> _createFormMap(ActionRequest formParameters) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		PortletFileUpload pfu = new PortletFileUpload(factory);

		HashMap<String, Object> formMap = new HashMap<String, Object>();

		try {
			@SuppressWarnings("rawtypes")
			Iterator iter = pfu.parseRequest(formParameters).iterator();

			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (item.getFieldName().equals("receptor_file")) {
					formMap.put(item.getFieldName(), item);
				} else {
					formMap.put(item.getFieldName(), item.getString());
				}
			}
		} catch (FileUploadException e) {
			Logger.log(e, Logger.exception);

			return null;
		}

		return formMap;
	}

	private void _setError(String error) {
		ERRORS = ERRORS + error;
	}

	public String getErrors() {
		return ERRORS;
	}

	private void _setDb(PersistenceManagerPlugin persistence) {
		PERSISTENCE = persistence;
	}

	private PersistenceManagerPlugin _getDb() {
		return PERSISTENCE;
	}
}
