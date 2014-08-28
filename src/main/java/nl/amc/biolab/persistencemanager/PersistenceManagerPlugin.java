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
import nl.amc.biolab.datamodel.objects.Processing;
import nl.amc.biolab.datamodel.objects.Project;
import nl.amc.biolab.datamodel.objects.User;
import nl.amc.biolab.datamodel.objects.UserAuthentication;
import nl.amc.biolab.tools.BlobHandler;

public class PersistenceManagerPlugin extends PersistenceManager {    
    /**
     * Only runs when the application is first deployed and the webdav resource is not stored in the database yet.
     */
    public void initApp() {
    	VarConfig config = new VarConfig();
    	
    	if (get.resourceByName("webdav") == null) {
            System.out.println("setting up appliciation and resources");
            
            // Create resources
            insert.resource("webdav", "localhost resource", config.getWebDavUri(), false, false, false, "http"); // local
            insert.resource("glite;vlemed", "glite;vlemed",	"lfn:/grid/vlemed/AutodockVinaGateway/autodock", false, false, true, "lcg"); // grid

            // Create application
            Long appId = insert.application("Autodock", "Autodock description test", "121;10196", 1);
            
            // Create IOPorts
            insert.ioPort(1, "3@Generator", "ligands.zip", "Input", "File", "test", true, get.application(appId), get.resourceByName("glite;vlemed"));
            insert.ioPort(2, "4@Generator", "config.txt", "Input", "File", "test", true, get.application(appId), get.resourceByName("glite;vlemed"));
            insert.ioPort(3, "5@Generator", "receptor.pdbqt", "Input", "File", "test", true, get.application(appId), get.resourceByName("glite;vlemed"));
            insert.ioPort(4, "0@Generator", "output.tar.gz", "Output", "File", "ZIP", true, get.application(appId), get.resourceByName("glite;vlemed"));
    	}
    }
    
    // ##########################################################################################################################
    // #   												User setup functions													#
    // ##########################################################################################################################
    
    public boolean checkUserAuth(String liferayId) {
    	User catalogUser = get.user(liferayId);
    	
    	if (catalogUser != null) {
    		HashMap<String, String> liferayUser = _getLiferayCredentials(liferayId);
    		
    		if (!catalogUser.getUserAuthentication().isEmpty()) {
    			UserAuthentication userAuth = catalogUser.getUserAuthentication().iterator().next();
    			
    			BlobHandler blob = new BlobHandler();
        		
        		if (userAuth != null) {
    	    		if (blob.decryptString(userAuth.getAuthentication()).equals(liferayUser.get("password"))) {
    	    			return true;
    	    		}
        		}
    		}
    	}
    	
    	return false;
    }
    
    public boolean userSetup(String liferayId) {
    	User catalogUser = get.user(liferayId);
    	
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
            catalogUser = get.user(insert.user(liferayId, liferayUser.get("first_name"), liferayUser.get("last_name"), liferayUser.get("email")));
            
            // Check if success
            if (catalogUser != null) {
            	// Going to set a password for this user
            	setPass = true;
            }
        }
    	
    	if (setPass) {
			update.userPassword(catalogUser.getEmail(), liferayUser.get("password"), get.resourceByName("webdav"), catalogUser);
			
			// Setup is done
    		setup = true;
    	}
    	
    	return setup;
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
    
    // ##########################################################################################################################
    // #   												Projects functions														#
    // ##########################################################################################################################
    
    Connection connect = null;
	
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

    public boolean checkProjectNameUnique(String projectName) {
        @SuppressWarnings("unchecked")
		List<Project> projects = (List<Project>) query.executeSQL("SELECT ProjectName FROM Project WHERE ProjectName = '" + projectName + "'");

        return projects.isEmpty();
    }
}