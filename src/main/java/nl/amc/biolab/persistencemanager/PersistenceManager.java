package nl.amc.biolab.persistencemanager;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import nl.amc.biolab.nsgdm.User;
import nl.amc.biolab.nsgdm.UserAuthentication;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Session;

import com.google.common.base.Joiner;

public class PersistenceManager extends nl.amc.biolab.Tools.PersistenceManager {

    private String QUERY = "";
    
    public boolean checkUserAuth(String liferayId) {
    	User catalogUser = getUser(liferayId);
    	
    	if (catalogUser != null) {
    		HashMap<String, String> liferayUser = _getLiferayCredentials(liferayId);
    		
    		UserAuthentication userAuth = _getUserAuthentication(catalogUser.getDbId());
    		
    		if (userAuth != null) {
	    		String hashPass = DigestUtils.shaHex(decryptString(userAuth.getAuthentication()));
	    		
	    		if (hashPass.equals(liferayUser.get("password"))) {
	    			return true;
	    		}
    		}
    	}
    	
    	return false;
    }
    
    public boolean userSetup(String liferayId, String password) {
    	User catalogUser = getUser(liferayId);
    	
    	HashMap<String, String> liferayUser = _getLiferayCredentials(liferayId);
    	
    	boolean setPass = false;
    	boolean setup = false;
    	
    	// User already exists in catalog, thus password has changed
    	if (catalogUser != null && liferayUser != null) {
    		setPass = true;
    	}
    	
    	// User does not exist in catalog yet, need to set up a new user and password
    	if (catalogUser == null && liferayUser != null) {
        	// Create user
            catalogUser = _storeUser(liferayId, liferayUser.get("first_name"), liferayUser.get("last_name"), liferayUser.get("email"));
            
            if (catalogUser != null) {
            	setPass = true;
            }
        }
    	
    	if (setPass) {
			// Test against liferay database
			String hashPass = DigestUtils.shaHex(password);
			
    		if (hashPass.equals(liferayUser.get("password"))) {
    			// Set password
    			setUserPassword(catalogUser.getDbId(), catalogUser.getEmail(), password, getResource("webdav").getDbId());
    			
    			// Setup is done
        		setup = true;
    		}
    	}
    	
    	return setup;
    }

    public void initApp() {
    	VarConfig config = new VarConfig();
    	
    	if (config.getIsDev() == true && getResource("webdav") == null) {
            System.out.println("setting up appliciation and resources");
            
            // Create resources
            _storeResource("webdav", config.getWebDavUri(), "localhost resource", "http", false); // local
            _storeResource("glite;vlemed", "lfn:/grid/vlemed/AutodockVinaGateway/autodock", "glite;vlemed", "lcg", true); // grid

            // Create application
            Long appId = storeApplication("Autodock", "Autodock description test", "121;10196", 1);

            // Create IOPorts
            storeIOPort(1, "3@Generator", "ligands.zip", "Input", "File", null, appId, "glite;vlemed", true);
            storeIOPort(2, "4@Generator", "config.txt", "Input", "File", null, appId, "glite;vlemed", true);
            storeIOPort(3, "5@Generator", "receptor.pdbqt", "Input", "File", null, appId, "glite;vlemed", true);
            storeIOPort(4, "0@collector.sh", "output.tar.gz", "Output", "File", "ZIP", appId, "glite;vlemed", true);
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

    private User _storeUser(String liferayId, String firstname, String lastname, String email) {
        User user = new User();
        
        user.setLiferayID(liferayId);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);

        persist(user);
        
        return user;
    }
    
    private HashMap<String, String> _getLiferayCredentials(String liferayId) {
    	HashMap<String, String> results = new HashMap<String, String>();
    	
	    try {
	      Class.forName("com.mysql.jdbc.Driver");
	      Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/liferay?user=root&password=guseroot");

	      Statement statement = connect.createStatement();
	      ResultSet resultSet = statement.executeQuery("select * from User_ where userId = " + liferayId);
	      
	      while (resultSet.next()) {
	    	  results.put("email", resultSet.getString("emailAddress"));
	    	  results.put("password", resultSet.getString("password_"));
	    	  results.put("first_name", resultSet.getString("firstName"));
	    	  results.put("last_name", resultSet.getString("lastName"));
	      }
	      
	      return results;
	    } catch(ClassNotFoundException e) {
	    	System.out.println(e);
	    } catch(SQLException e) {
	    	System.out.println(e);
	    }
	    
	    return null;
    }
    
    private UserAuthentication _getUserAuthentication(Long userId) {
    	List<UserAuthentication> results = null;
    	
    	try {
            results = _getSession().createQuery("from UserAuthentication where UserKey ='" + userId + "'").list();
            
            for (UserAuthentication u : results) {
                return u;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    	
    	return null;
    }

    private Resource _storeResource(String name, String uri, String description, String protocol, boolean robot) {
        Resource resource = new Resource();
        
        resource.setName(name);
        resource.setBaseURI(uri);
        resource.setDescription(description);
        resource.setProtocol(protocol);
        resource.setRobot(robot);

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