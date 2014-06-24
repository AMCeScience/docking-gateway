package nl.amc.biolab.autodock.input.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.autodock.input.objects.Configuration;
import nl.amc.biolab.autodock.input.objects.Ligands;
import nl.amc.biolab.autodock.input.objects.Receptor;
import nl.amc.biolab.autodock.input.objects.JobSubmission;
import nl.amc.biolab.nsg.pm.ProcessingManagerClient;
import nl.amc.biolab.nsgdm.DataElement;
import nl.amc.biolab.nsgdm.Project;
import nl.amc.biolab.nsgdm.Resource;
import nl.amc.biolab.nsgdm.User;
import nl.amc.biolab.persistencemanager.PersistenceManager;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Allard van Altena
 */
public class FormSubmitter extends VarConfig {
    private String ERRORS = "";
    private PersistenceManager PERSISTENCE;
    
    public FormSubmitter(String liferayId) {
        _setDb(new PersistenceManager());
        
        _getDb().init();
    }
    
    public void close() {
        _getDb().shutdown();
    }
    
    private void _cleanUp(HashMap<String, Object> formMap) {        
        File directory = new File(config.getProjectFilePath(formMap.get("project_name").toString()));
        
        try {
            FileUtils.deleteDirectory(directory);
        } catch(IOException e) {
            log.log(e);
        }
    }
    
    public boolean saveForm(ActionRequest formParameters) {
        // Determains the success of this method
        boolean stored = false;
        
        HashMap<String, Object> formMap = _createFormMap(formParameters);
        
        if (formMap != null) {
        	JobSubmission job = new JobSubmission();
            
            job.setProjectName(formMap.get("project_name").toString());
            job.setProjectDescription(formMap.get("project_description").toString());
        	
            // Get liferay user ID
            String liferayUserId = formParameters.getRemoteUser();

            // Get catalog user
            User catalogUser = _getDb().getUser(liferayUserId);

            // Check if user exists in catalog database
            if (catalogUser != null) {                
                // Project name is set but is not unique
                if (!_getDb().checkProjectNameUnique(job.getProjectName())) {
                    _setError("Project name is not unique.<br/>");

                    close();

                    return false;
                }
                
                File directory = new File(config.getFilePath() + "/" + job.getProjectName());
                
                directory.mkdirs();
                
                // Project name is not set, return immediately                
                if (!directory.exists()) {
                    _setError("Could not create project folder.<br/>");
                    
                    close();
                    
                    return false;
                }
                
                // Create configuration file
                ConfigFactory configFactory = new ConfigFactory();
                Configuration configuration = configFactory.setData(formMap);

                if (!configuration.getValid()) {
                    _setError(configuration.getErrors());

                    close();
                    _cleanUp(formMap);

                    return false;
                }
                
                // Write ligands to zip file
                LigandZipper ligandZipper = new LigandZipper();
                Ligands ligands = ligandZipper.prepareLigandFile(formMap);
                
                if (!ligands.validate()) {
                    close();
                    _cleanUp(formMap);
                    
                    return false;
                }

                // Write the receptor file to the system
                ReceptorFileUploader uploader = new ReceptorFileUploader();
                Receptor receptor = uploader.doUpload((FileItem) formMap.get("receptor_file"), job.getProjectName());

                if (!receptor.validate()) {
                    _setError(receptor.getErrors());

                    close();
                    _cleanUp(formMap);

                    return false;
                }
                
                if (!configFactory.writeToDisk(configuration)) {
                    _setError(configuration.getErrors());
                    
                    close();
                    _cleanUp(formMap);
                    
                    return false;
                }
                
                job.setLigandsUri(config.getUri(job.getProjectName(), config.getLigandsZipFileName()));
                job.setLigandsCount(ligands.getCount());
                
                if (formMap.containsKey("run_pilot") && formMap.get("run_pilot").equals("1")) {
	                job.setPilotLigandsUri(config.getUri(job.getProjectName(), config.getPilotLigandsZipFileName()));
	                job.setPilotLigandsCount(config.getPilotLigandCount());
                }
                
                job.setReceptorUri(config.getUri(job.getProjectName(), config.getReceptorFileName()));
                job.setConfigurationUri(config.getUri(job.getProjectName(), config.getConfigFileName()));
                
                // Do this as the last step because from now on the JobSubmission will return the project name with "_pilot" addition
                if (formMap.containsKey("run_pilot") && formMap.get("run_pilot").equals("1")) {
                	job.setPilot(true);
                }
                
                job.setUser(catalogUser);
                
                // Store project
                Project project = _saveProject(job);
                job.setProject(project);
                
                // Store data elements
                HashMap<String, DataElement> data = _saveData(job);
                job.setLigands(data.get("ligands"));
                job.setConfiguration(data.get("configuration"));
                job.setReceptor(data.get("receptor"));
                job.setPilotLigands(data.get("pilot_ligands"));
                
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
        Project project = _getDb().storeProject(job.getProjectName(), job.getProjectDescription(), job.getUser());
        
        return project;
    }
        
    private HashMap<String, DataElement> _saveData(JobSubmission job) {
    	Resource resource = _getDb().getResource("webdav");
    	
    	Collection<Project> projects = new ArrayList<Project>();
    	
    	HashMap<String, DataElement> dataMap = new HashMap<String, DataElement>();
    	
        // Add created project to collection
        projects.add(job.getProject());
        
        // Store ligands zip
        String ligandsFormat = config.getLigandsZipExt();
        String ligandsName = config.getLigandsZipFileName();
        String ligandsUri = job.getLigandsUri();
        String ligandsCount = job.getLigandsCount().toString();
        
        DataElement ligands = _getDb().storeDataElement(ligandsFormat, ligandsName, ligandsUri, ligandsCount, "filler", "filler", projects, resource);
        
        DataElement pilotLigands = null;
        
        if (job.isPilot()) {
        	// Store ligands zip
            String pilotLigandsName = config.getPilotLigandsZipFileName();
            String pilotLigandsUri = job.getPilotLigandsUri();
            String pilotLigandsCount = String.valueOf(job.getPilotLigandsCount());
            
            pilotLigands = _getDb().storeDataElement(ligandsFormat, pilotLigandsName, pilotLigandsUri, pilotLigandsCount, "filler", "filler", projects, resource);
        }
        
        // Store receptor file
        String receptorFormat = config.getReceptorExt();
        String receptorName = config.getReceptorFileName();
        String receptorUri = job.getReceptorUri();
        
        DataElement receptor = _getDb().storeDataElement(receptorFormat, receptorName, receptorUri, null, "filler", "filler", projects, resource);
        
        // Store configuration file
        String configFormat = config.getConfigExt();
        String configName = config.getConfigFileName();
        String configUri = job.getConfigurationUri();
        
        DataElement configuration = _getDb().storeDataElement(configFormat, configName, configUri, null, "filler", "filler", projects, resource);
        
        dataMap.put("ligands", ligands);
        dataMap.put("pilot_ligands", pilotLigands);
        dataMap.put("receptor", receptor);
        dataMap.put("configuration", configuration);
        
        return dataMap;
    }
    
    private void _submit(JobSubmission job) {
    	// Send to processing manager
        List<Long> submits = new ArrayList<Long>();
        
        // Do not change order of adding ids, this is linked to the processingmanager which requires the input to be in the right order
        // Add either the pilot ligands to the submission or the full ligand set
        if (job.isPilot()) {
        	submits.add(job.getPilotLigands().getDbId());
        } else {
        	submits.add(job.getLigands().getDbId());
        }
        submits.add(job.getConfiguration().getDbId());
        submits.add(job.getReceptor().getDbId());
        
        // Get processingmanager webservice client
        ProcessingManagerClient client = new ProcessingManagerClient(config.getProcessingWSDL());
        
        // Submit the job through the processingmanager webservice
        client.submit(job.getProject().getDbId(), 
        		_getDb().getApplicationByName(config.getAutodockName()).getDbId(), 
        		submits, 
        		job.getUser().getDbId(), 
        		job.getUser().getLiferayID(), 
        		job.getProject().getDescription());
    }
    
    private HashMap<String, Object> _createFormMap(ActionRequest formParameters) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        PortletFileUpload pfu = new PortletFileUpload(factory);

        HashMap<String, Object> formMap = new HashMap<String, Object>();
        
        try {
            @SuppressWarnings("rawtypes")
			Iterator iter = pfu.parseRequest(formParameters).iterator();

            while(iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.getFieldName().equals("receptor_file")) {
                    formMap.put(item.getFieldName(), item);
                } else {
                    formMap.put(item.getFieldName(), item.getString());
                }
            }
        } catch (FileUploadException e) {
            log.log(e.toString());
            
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
    
    private void _setDb(PersistenceManager persistence) {
        PERSISTENCE = persistence;
    }
    
    private PersistenceManager _getDb() {
        return PERSISTENCE;
    }
    
}
