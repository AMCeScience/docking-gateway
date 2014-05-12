package nl.amc.biolab.autodock.input.tools;

import nl.amc.biolab.persistencemanager.PersistenceManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.portlet.ActionRequest;
import nl.amc.biolab.autodock.input.objects.Configuration;
import nl.amc.biolab.autodock.input.objects.Ligands;
import nl.amc.biolab.autodock.input.objects.Receptor;
import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.nsg.pm.ProcessingManagerClient;
import nl.amc.biolab.nsgdm.DataElement;
import nl.amc.biolab.nsgdm.Project;
import nl.amc.biolab.nsgdm.Resource;
import nl.amc.biolab.nsgdm.User;
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
    
    private void _cleanUp(HashMap formMap) {        
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
        
        HashMap formMap = _createFormMap(formParameters);
        
        if (formMap != null) {
            // Get liferay user ID
            String liferayUserId = formParameters.getRemoteUser();

            // Get catalog user
            User catalogUser = _getDb().getUser(liferayUserId);

            // Check if user exists in catalog database
            if (catalogUser != null) {                
                // Project name is set but is not unique
                if (!_getDb().checkProjectNameUnique(formMap.get("project_name").toString())) {
                    _setError("Project name is not unique.<br/>");

                    close();

                    return false;
                }
                
                File directory = new File(config.getFilePath() + "/" + formMap.get("project_name").toString());
                
                directory.mkdirs();
                
                if (!directory.exists()) {
                    _setError("Could not create project folder.<br/>");
                    
                    close();
                    
                    return false;
                }
                
                // Create configuration file
                ConfigFactory configFactory = new ConfigFactory();
                Configuration configuration = configFactory.setData(formMap);

                // Project name is not set, return immediately
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
                Receptor receptor = uploader.doUpload((FileItem) formMap.get("receptor_file"), formMap.get("project_name").toString());

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

                // Add file information to formMap
                formMap.put("ligands_uri", config.getUri(formMap.get("project_name").toString(), config.getLigandsZipFileName()));
                formMap.put("ligands_count", ligands.getCount());
                formMap.put("receptor_uri", config.getUri(formMap.get("project_name").toString(), config.getReceptorFileName()));
                formMap.put("config_uri", config.getUri(formMap.get("project_name").toString(), config.getConfigFileName()));
                
                // Store project
                _saveProject(formMap, catalogUser);

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
    
    private void _saveProject(HashMap formMap, User catalogUser) {
        Resource resource = _getDb().getResource("webdav");
        
        Collection<Project> projects = new ArrayList<Project>();
        
        String projectName = formMap.get("project_name").toString();
        String projectDescription = formMap.get("project_description").toString();
        
        Project project = _getDb().storeProject(projectName, projectDescription, catalogUser);
        
        // Add created project to collection
        projects.add(project);
        
        // Store ligands zip
        String ligandsFormat = config.getLigandsZipExt();
        String ligandsName = config.getLigandsZipFileName();
        String ligandsUri = formMap.get("ligands_uri").toString();
        String ligandsCount = formMap.get("ligands_count").toString();
        
        DataElement ligands = _getDb().storeDataElement(ligandsFormat, ligandsName, ligandsUri, ligandsCount, null, null, projects, resource);
        
        // Store receptor file
        String receptorFormat = config.getReceptorExt();
        String receptorName = config.getReceptorFileName();
        String receptorUri = formMap.get("receptor_uri").toString();
        
        DataElement receptor = _getDb().storeDataElement(receptorFormat, receptorName, receptorUri, null, null, null, projects, resource);
        
        // Store configuration file
        String configFormat = config.getConfigExt();
        String configName = config.getConfigFileName();
        String configUri = formMap.get("config_uri").toString();
        
        DataElement configuration = _getDb().storeDataElement(configFormat, configName, configUri, null, null, null, projects, resource);
        
        // Send to processing manager
        List<Long> submits = new ArrayList<Long>();
        
        // Do not change order of adding ids, this is linked to the processingmanager which requires the input to be in the right order
        submits.add(ligands.getDbId());
        submits.add(configuration.getDbId());
        submits.add(receptor.getDbId());
        
        // Get processingmanager webservice client
        ProcessingManagerClient client = new ProcessingManagerClient(config.getProcessingWSDL());
        
        // Submit the job through the processingmanager webservice
        client.submit(project.getDbId(), _getDb().getApplicationByName(config.getAutodockName()).getDbId(), submits, catalogUser.getDbId(), catalogUser.getLiferayID(), project.getDescription());
    }
    
    private HashMap _createFormMap(ActionRequest formParameters) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        PortletFileUpload pfu = new PortletFileUpload(factory);

        HashMap formMap = new HashMap();
        
        try {
            List fileItems = pfu.parseRequest(formParameters);
            Iterator iter = fileItems.iterator();

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
