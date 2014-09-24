package nl.amc.biolab.persistencemanager;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.datamodel.manager.PersistenceManager;
import nl.amc.biolab.datamodel.objects.Application;
import nl.amc.biolab.datamodel.objects.IOPort;
import nl.amc.biolab.datamodel.objects.Processing;
import nl.amc.biolab.datamodel.objects.Project;
import nl.amc.biolab.datamodel.objects.Resource;
import nl.amc.biolab.datamodel.objects.User;
import nl.amc.biolab.datamodel.objects.UserAuthentication;
import nl.amc.biolab.tools.BlobHandler;

public class PersistenceManagerPlugin extends PersistenceManager {    
    Connection connect = null;

    /**
     * Only runs when the application is first deployed and the webdav resource is not stored in the database yet.
     */
    public void initApp() {
    	VarConfig config = new VarConfig();
    	
    	if (get.resourceByName("webdav") == null) {
            System.out.println("setting up appliciation and resources");
            
            Resource webdav = new Resource("webdav", "localhost resource", config.getWebDavUri(), "http", true, false, false);
            Resource vlemed = new Resource("glite;vlemed", "glite;vlemed", "lfn:/grid/vlemed/AutodockVinaGateway/autodock", "lcg", true, false, false);
            
            // Create resources
            insert.resource(webdav); // local
            insert.resource(vlemed); // grid

            // Create application
            Long appId = insert.application("Autodock", "Autodock description test", "121;10196", 1);

            // Create IOPorts
            insert.ioPort(new IOPort("3@Generator", 1, "ligands.zip", "Input", "File", null, true, null, get.application(appId), webdav, null, null));
            insert.ioPort(new IOPort("4@Generator", 2, "config.txt", "Input", "File", null, true, null, get.application(appId), webdav, null, null));
            insert.ioPort(new IOPort("5@Generator", 3, "receptor.pdbqt", "Input", "File", null, true, null, get.application(appId), webdav, null, null));
            insert.ioPort(new IOPort("0@collector.sh", 4, "output.tar.gz", "Output", "File", "ZIP", true, null, get.application(appId), vlemed, null, null));
    	}
    }
    
    // ##########################################################################################################################
    // #   												User setup functions													#
    // ##########################################################################################################################
    
    public boolean checkUserAuth(String liferayId) {
    	User catalogUser = get.user(liferayId);
    	
    	if (catalogUser != null) {
    		HashMap<String, String> liferayUser = _getLiferayCredentials(liferayId);
    		
    		UserAuthentication userAuth = _getUserAuthentication(catalogUser.getDbId());
    		
    		if (userAuth != null) {
    			BlobHandler handler = new BlobHandler();
    			
	    		if (handler.decryptString(userAuth.getAuthentication()).equals(liferayUser.get("password"))) {
	    			return true;
	    		}
    		}
    	}
    	
    	return false;
    }
    
    public boolean userSetup(String liferayId) {
    	User catalogUser = get.user(liferayId);
    	Long userId = null;
    	
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
            userId = insert.user(liferayId, liferayUser.get("first_name"), liferayUser.get("last_name"), liferayUser.get("email"));
            
            // Check if success
            if (userId != null) {
            	// Going to set a password for this user
            	setPass = true;
            }
        }
    	
    	if (setPass) {
			_setUserPassword(catalogUser, liferayUser.get("password"), get.resourceByName("webdav"));
			
			// Setup is done
    		setup = true;
    	}
    	
    	return setup;
    }
    
    private UserAuthentication _setUserPassword(User user, String userPass, Resource resource) {
    		UserAuthentication auth = null;
    		BlobHandler handler = new BlobHandler();
    		
    		auth = get.userAuthenticationByResourceId(user.getDbId(), resource.getDbId());
    		
	    	if (auth != null && auth.getUserLogin().equalsIgnoreCase(user.getEmail())) {
	    		auth.setAuthentication(handler.encryptString(userPass));
		    	
	    		crud.update(auth);
	    	} else {
	    	    auth = new UserAuthentication();
	    	    
	    	    auth.setUser(user);
	    	    auth.setResource(resource);
	    	    auth.setUserLogin(user.getEmail());
	    	    auth.setAuthentication(handler.encryptString(userPass));
	    		
	    	    insert.userAuthentication(auth);
	    	}
	    	
	    	return auth;
       }
    
    private HashMap<String, String> _getLiferayCredentials(String liferayId) {
    	HashMap<String, String> results = new HashMap<String, String>();
    	VarConfig config = new VarConfig();
    	
	    try {
	    	connect = DriverManager.getConnection(config.getLiferayDbConnectionUrl());
	
	    	Statement statement = connect.createStatement();
	    	ResultSet resultSet = statement.executeQuery("select * from User_ where userId = " + liferayId);
	    	
	    	while (resultSet.next()) {
	    		results.put("email", resultSet.getString("emailAddress"));
	    		results.put("password", resultSet.getString("password_"));
	    		results.put("first_name", resultSet.getString("firstName"));
	    		results.put("last_name", resultSet.getString("lastName"));
	    	}
	      
	    	// Close everything
	    	resultSet.close();
	    	statement.close();
	    	connect.close();
	      
	    	return results;
	    } catch(SQLException e) {
	    	System.out.println(e);
	    }
	    
	    return null;
    }
    
	private UserAuthentication _getUserAuthentication(Long userId) {
    	try {
            return (UserAuthentication) query.executeQuery("from UserAuthentication where UserID ='" + userId + "'", true);
        } catch (Exception e) {
            System.out.println(e);
        }
    	
    	return null;
    }
	
	public Application getApplicationByName(String name) {
    	try {
            return (Application) query.executeQuery("from Application where Name ='" + name + "'", true);
        } catch (Exception e) {
            System.out.println(e);
        }
    	
    	return null;
    }
    
    // ##########################################################################################################################
    // #   												Projects functions														#
    // ##########################################################################################################################

    /**
	 * Searches the database with the provided sql, the sql should contain a join of Project and Processing for this to work
	 * @param sql Sql by which we search for the projects, should contain a join of Project and Processing tables
	 * @return One project in a list of arrays, array position 0 contains the Project, array position 1 contains the Processing
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getSingleProjectBySQL(String sql) {
		List<Object[]> projects = session.createSQLQuery(sql).addEntity("p", Project.class).addEntity("po", Processing.class).setMaxResults(1).list();
		
		return projects;
	}

	/**
	 * Searches the database with the provided sql, the sql should contain a join of Project and Processing for this to work
	 * @param sql Sql by which we search for the projects, should contain a join of Project and Processing tables
	 * @return One or more projects in a list of arrays, array position 0 contains the Project, array position 1 contains the Processing
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getProjectsBySQL(String sql) {
		List<Object[]> projects = session.createSQLQuery(sql).addEntity("p", Project.class).addEntity("po", Processing.class).list();
		
		return projects;
	}
    
	/**
	 * Counts the amount of projects in the database by the provided sql
	 * @param sql Sql by which we search for projects, should contain a join of Project and Processing tables 
	 * @return Count of projects as an integer
	 */
    public int countProjectsBySQL(String sql) {
        return ((BigInteger) session.createSQLQuery(sql).uniqueResult()).intValue();
    }
}