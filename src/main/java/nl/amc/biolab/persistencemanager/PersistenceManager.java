package nl.amc.biolab.persistencemanager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.nsgdm.Application;
import nl.amc.biolab.nsgdm.DataElement;
import nl.amc.biolab.nsgdm.IOPort;
import nl.amc.biolab.nsgdm.Processing;
import nl.amc.biolab.nsgdm.Project;
import nl.amc.biolab.nsgdm.Resource;
import nl.amc.biolab.nsgdm.Submission;
import nl.amc.biolab.nsgdm.SubmissionIO;
import nl.amc.biolab.nsgdm.User;

import org.hibernate.Session;

import com.google.common.base.Joiner;

/**
 * @author Allard van Altena
 */
public class PersistenceManager extends nl.amc.biolab.Tools.PersistenceManager {

    private String QUERY = "";

    // TESTING PURPOSES
    public void initStuff(String liferayId) {
        User catalogUser = getUser(liferayId);
        
        System.out.println("init stuff");
        System.out.println(liferayId);
        
        if (catalogUser == null) {
            System.out.println("in here");
            // Create user
            User user = _storeUser(liferayId);
            
            // Create vlemed resource
            Resource resource1 = _storeResource(); // local
            Resource resource2 = _storeResource2(); // grid
            
            // Bind user to resource            
            setUserPassword(user.getDbId(), "webdavuser", "key.webdav", resource1.getDbId());

            // Create application
            Long appId = storeApplication("Autodock", "Autodock description test", "121;10196", 1);

            // Create IOPorts
            storeIOPort(1, "3@Generator", "ligands.zip", "Input", "File", null, appId, "glite;vlemed", true);
            storeIOPort(2, "4@Generator", "config.txt", "Input", "File", null, appId, "glite;vlemed", true);
            storeIOPort(3, "5@Generator", "receptor.pdbqt", "Input", "File", null, appId, "glite;vlemed", true);
            storeIOPort(4, "0@collector.sh", "output.tar.gz", "Output", "File", "ZIP", appId, "glite;vlemed", true);
            
            /*VarConfig config = new VarConfig();
            
            Project project = storeProject("testProject1", "test project description", user);
        
            Collection<Project> projects = new ArrayList<Project>();
            
            // Add created project to collection
            projects.add(project);
            
            DataElement ligands = storeDataElement(config.getLigandsZipExt(), 
                    config.getLigandsZipFileName(), 
                    config.getUri("testProj1", config.getLigandsZipFileName()), 
                    "1234", null, null, projects, resource1);
            DataElement receptor = storeDataElement(config.getReceptorExt(), 
                    config.getReceptorFileName(), 
                    config.getUri("testProj1", config.getReceptorFileName()),
                    null, null, null, projects, resource1);
            DataElement configuration = storeDataElement(config.getConfigExt(), 
                    config.getConfigFileName(), 
                    config.getUri("testProj1", config.getConfigFileName()),
                    null, null, null, projects, resource1);
            DataElement output = storeDataElement(config.getLigandsZipExt(), 
                    "output", 
                    config.getOutputFileName("testProj1"),
                    null, null, null, projects, resource2);
            
            Long procID = getNewProcessingID(project.getDbId(), appId);
            storeProcessing(procID, "test", "desc", user.getDbId(), "done");
            
            List items = new ArrayList();
            
            items.add(ligands.getDbId());
            items.add(receptor.getDbId());
            items.add(configuration.getDbId());
            
            List bam = new ArrayList();
            bam.add(items);
            
            Long subm = storeSubmission(procID, "test", "done", bam);
            _storeSubmissionIO(getSubmission(subm), output, _getIOPort(4), "Output");*/
        }
    }

    // TESTING PURPOSES, get private field 'session'
    private Session _getSession() {
        try {
            Field field = nl.amc.biolab.Tools.PersistenceManager.class.getDeclaredField("session");
            field.setAccessible(true);
            Session value = (Session) field.get(this);
            field.setAccessible(false);

            return value;
        } catch (NoSuchFieldException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        }

        return null;
    }

    // TESTING PURPOSES
    private User _storeUser(String liferayId) {
        User user = new User();

        user.setLiferayID(liferayId);
        user.setFirstName("Allard");
        user.setLastName("van Altena");

        persist(user);

        return user;
    }
    
    private SubmissionIO _storeSubmissionIO(Submission sub, DataElement de, IOPort port, String io) {
        SubmissionIO subm = new SubmissionIO();
        
        subm.setSubmission(sub);
        subm.setDataElement(de);
        subm.setPort(port);
        subm.setType(io);
        
        persist(subm);
        
        return subm;
    }

    // TESTING PURPOSES
    private Resource _storeResource() {
        Resource resource = new Resource();

        VarConfig config = new VarConfig();
        
        resource.setName("webdav");
        resource.setBaseURI(config.getWebDavUri());
        resource.setDescription("localhost resource");
        resource.setProtocol("http");

        persist(resource);

        return resource;
    }

    // TESTING PURPOSES
    private Resource _storeResource2() {
        Resource resource = new Resource();

        resource.setName("glite;vlemed");
        resource.setDescription("glite;vlemed");
        resource.setBaseURI("lfn:/grid/vlemed/AutodockVinaGateway/autodock");
        resource.setProtocol("lcg");
        resource.setRobot(true);

        persist(resource);

        return resource;
    }

    @Override
    public User getUser(String UserID) {
        List<User> results = null;
        
        try {
            results = _getSession().createQuery("from User where LiferayID ='" + UserID + "'").list();
            
            for (User u : results) {
                return u;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }

    public Resource getResource(String name) {
        Resource resource = new Resource();

        try {
	        List<Object> resources = executeSQL("SELECT * FROM Resource WHERE Name = ?", resource.getClass(), name);
	        
	        Iterator<Object> iter = resources.iterator();
	        
	        while (iter.hasNext()) {
	        	return (Resource) iter.next();
	        }
        } catch (Exception e) {
        	System.out.println(e);
        }

        return null;
    }

    public List<Project> getProjectsDataById(List<Long> projectIds) {
        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        String sql = "SELECT * FROM Project as p WHERE p.ProjectID IN (" + commaJoiner.join(projectIds) + ")";

        List<Project> projects = _getSession().createSQLQuery(sql).addEntity(Project.class).list();

        return projects;
    }

    public List<Object[]> getProjectsBySQL(String sql) {
        List<Object[]> projects = _getSession().createSQLQuery(sql).addEntity("p", Project.class).addEntity("po", Processing.class).list();

        return projects;
    }

    public boolean checkProjectNameUnique(String projectName) {
        List projects = executeSQL("SELECT ProjectName FROM Project WHERE ProjectName = '" + projectName + "'");

        return projects.isEmpty();
    }

    public Project storeProject(String projectName, String projectDescription, User projectOwner) {
        Project project = new Project();

        project.setName(projectName);
        project.setDescription(projectDescription);
        project.setOwner(projectOwner.getFirstName() + " " + projectOwner.getLastName());

        project.getUsers().add(projectOwner);

        persist(project);

        return project;
    }

    public DataElement storeDataElement(String format, String name, String uri, String scanID, String type, String subject, Collection<Project> projects, Resource resource) {
        Date dateNow = new Date();

        DataElement dataElement = new DataElement();
        dataElement.setDate(dateNow);
        dataElement.setFormat(format);
        dataElement.setName(name);
        dataElement.setScanID(scanID);
        dataElement.setType(type);
        dataElement.setSubject(subject);
        dataElement.setResource(resource);
        dataElement.setURI(uri);
        dataElement.setSize(1);

        dataElement.getProjects().add((Project) projects.toArray()[0]);

        persist(dataElement);

        return dataElement;
    }

    public void setSelect(LinkedHashMap<String, String> selections) {
        _setQuery("SELECT");

        ArrayList<String> selectSql = new ArrayList<String>();

        for (Entry entry : selections.entrySet()) {
            if (entry.getValue() != null && entry.getValue().toString().length() != 0) {
                selectSql.add(entry.getKey().toString() + " as " + entry.getValue().toString());
            } else {
                selectSql.add(entry.getKey().toString());
            }
        }

        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        _setQuery(commaJoiner.join(selectSql));
    }

    public void setTables(LinkedHashMap<String, String> tables) {
        _setQuery("FROM");

        ArrayList<String> tableSql = new ArrayList<String>();

        for (Entry entry : tables.entrySet()) {
            if (entry.getValue() != null && entry.getValue().toString().length() != 0) {
                tableSql.add(entry.getKey().toString() + " as " + entry.getValue().toString());
            } else {
                tableSql.add(entry.getKey().toString());
            }
        }

        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        _setQuery(commaJoiner.join(tableSql));
    }

    public void setJoin(LinkedHashMap<String, String> tables) {
        ArrayList<String> joinSql = new ArrayList<String>();

        for (Entry entry : tables.entrySet()) {
            joinSql.add("JOIN " + entry.getKey().toString() + " ON " + entry.getValue().toString());
        }

        Joiner spaceJoiner = Joiner.on(" ").skipNulls();

        _setQuery(spaceJoiner.join(joinSql));
    }

    public void setWhere(String name, String modifier, String where) {
        _setQuery(name + " " + modifier + " " + where);
    }

    public void setWhereOpen() {
        _setQuery("(");
    }

    public void setWhereClose() {
        _setQuery(")");
    }

    public void startWhere() {
        _setQuery("WHERE");
    }

    public void setWhereAnd() {
        _setQuery("AND");
    }

    public void setWhereOr() {
        _setQuery("OR");
    }

    public void setSort(LinkedHashMap<String, String> order) {
        _setQuery("ORDER BY");

        ArrayList<String> orderSql = new ArrayList<String>();

        for (Entry entry : order.entrySet()) {
            orderSql.add(entry.getKey() + " " + entry.getValue());
        }

        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        _setQuery(commaJoiner.join(orderSql));
    }

    private void _setQuery(String query) {
        QUERY = QUERY + query + " ";
    }

    public String getQuery() {
        return QUERY;
    }

    private IOPort _getIOPort(int port) {
        IOPort ioport = new IOPort();
        
        List<Object> resources = _getSession().createSQLQuery("SELECT * FROM IOPort WHERE PortID = 4").addEntity(ioport.getClass()).list();

        ioport = (IOPort) resources.get(0);
        
        return ioport;
    }
    
    @Override
    public Long storeIOPort(int portNumber, String portName, String displayName, String ioType, String dataType, String dataFromat, Long applicationId, String resourceName, boolean visible) {
        //Resource resource = (Resource) get(Resource.class, resourceId);
        Resource resource = getResource(resourceName);
        Application application = (Application) get(Application.class, applicationId);

        IOPort port = new IOPort();
        port.setPortNumber(portNumber);
        port.setPortName(portName);
        port.setDisplayName(displayName);
        port.setIOType(ioType);
        port.setDataType(dataType);
        port.setDataFormat(dataFromat);
        port.setResource(resource);
        port.setApplication(application);
        port.setVisible(visible);
        persist(port);

        application.getIOPorts().add(port);

        update(application);

        return port.getDbId();
    }
}