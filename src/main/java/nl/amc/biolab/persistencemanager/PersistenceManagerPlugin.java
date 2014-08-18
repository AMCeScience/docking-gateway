package nl.amc.biolab.persistencemanager;

import java.math.BigInteger;
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
import java.util.List;

import nl.amc.biolab.Tools.PersistenceManager;
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
import nl.amc.biolab.nsgdm.UserAuthentication;

public class PersistenceManagerPlugin extends PersistenceManager {
	public Project storeProject(String projectName, boolean isPilot, String projectDescription, User projectOwner, Collection<Application> apps) {
        Project project = new Project();

        project.setName(projectName);
        project.setXnatID(isPilot ? "true" : "false");
        project.setDescription(projectDescription);
        project.setOwner(projectOwner.getFirstName() + " " + projectOwner.getLastName());
        project.setApplications(apps);
        
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
        dataElement.setURI(uri.replace(" ", "%20"));
        dataElement.setSize(1);

        dataElement.getProjects().add((Project) projects.toArray()[0]);

        persist(dataElement);

        return dataElement;
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
    
    private User _storeUser(String liferayId, String firstname, String lastname, String email) {
        User user = new User();
        
        user.setLiferayID(liferayId);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);

        persist(user);
        
        return user;
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
    
    /**
     * Only runs when the application is first deployed and the webdav resource is not stored in the database yet.
     */
    public void initApp() {
    	VarConfig config = new VarConfig();
    	
    	if (getResource("webdav") == null) {
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
    
    // ##########################################################################################################################
    // #   												User setup functions													#
    // ##########################################################################################################################
    
    public boolean checkUserAuth(String liferayId) {
    	User catalogUser = getUser(liferayId);
    	
    	if (catalogUser != null) {
    		HashMap<String, String> liferayUser = _getLiferayCredentials(liferayId);
    		
    		UserAuthentication userAuth = _getUserAuthentication(catalogUser.getDbId());
    		
    		if (userAuth != null) {
	    		if (decryptString(userAuth.getAuthentication()).equals(liferayUser.get("password"))) {
	    			return true;
	    		}
    		}
    	}
    	
    	return false;
    }
    
    public boolean userSetup(String liferayId) {
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
            
            // Check if success
            if (catalogUser != null) {
            	// Going to set a password for this user
            	setPass = true;
            }
        }
    	
    	if (setPass) {
			setUserPassword(catalogUser.getDbId(), catalogUser.getEmail(), liferayUser.get("password"), getResource("webdav").getDbId());
			
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
    
    @SuppressWarnings("unchecked")
	private UserAuthentication _getUserAuthentication(Long userId) {
    	List<UserAuthentication> results = null;
    	
    	try {
            results = session.createQuery("from UserAuthentication where UserKey ='" + userId + "'").list();
            
            for (UserAuthentication u : results) {
                return u;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    	
    	return null;
    }

    @SuppressWarnings("unchecked")
	@Override
    public User getUser(String UserID) {
        List<User> results = null;
        
        try {
            results = session.createQuery("from User where LiferayID ='" + UserID + "'").list();
            
            for (User u : results) {
                return u;
            }
        } catch (Exception e) {
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
	public List<Object[]> getSingleProjectBySQL(String sql) {
		return _executeProjectSQL(sql, true);
	}

	/**
	 * Searches the database with the provided sql, the sql should contain a join of Project and Processing for this to work
	 * @param sql Sql by which we search for the projects, should contain a join of Project and Processing tables
	 * @return One or more projects in a list of arrays, array position 0 contains the Project, array position 1 contains the Processing
	 */	
	public List<Object[]> getProjectsBySQL(String sql) {
		return _executeProjectSQL(sql, false);
	}
	
	/**
	 * Searches database with the provided sql, sets the Project, Processing, Submission, SubmissionIO, and DataElement objects
	 * @param sql Sql by which we search for projects, should contain a join of Project and Processing tables
	 * @param single_project Switch for getting additional data for project (i.e. Submission, SubmissionIO, and DataElement), this lowers the loading time when refreshing the browser window
	 * @return One or more projects in a list of arrays, array position 0 contains the Project, array position 1 contains the Processing
	 */
	private List<Object[]> _executeProjectSQL(String sql, boolean single_project) {
		List<Object[]> projects = new ArrayList<Object[]>();
		VarConfig config = new VarConfig();
		
		try {
			connect = DriverManager.getConnection(config.getDbConnectionUrl());
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			
			while (resultSet.next()) {
				Object[] holder = new Object[2];
				
				Project project = new Project();
				project.setDbId(resultSet.getLong("ProjectID"));
				project.setXnatID(resultSet.getString("XnatID"));
				project.setName(resultSet.getString("ProjectName"));
				project.setDescription(resultSet.getString("ProjectDescription"));
				project.setOwner(resultSet.getString("UserName"));
				//project.setApplications();
				//project.setDataElements();
				
				Processing processing = new Processing();
				processing.setDbId(resultSet.getLong("ProcessingID"));
				processing.setName(resultSet.getString("ProcessingName"));
				processing.setDescription(resultSet.getString("ProcessingDescription"));
				processing.setDate(resultSet.getDate("ProcessingDate"));
				processing.setStatus(resultSet.getString("ProcessingStatus"));
				processing.setLastUpdate(resultSet.getDate("ProcessingLastUpdate"));
				
				Application app = new Application();
				app.setDbId(resultSet.getLong("ApplicationID"));
				app.setName(resultSet.getString("Name"));
				
				processing.setApplication(app);
				
				if (single_project) {
					processing.setSubmissions(_getSubmissions(resultSet.getLong("ProcessingID")));
				}
				
				holder[0] = project;
				holder[1] = processing;
				
				projects.add(holder);
			}
			
			resultSet.close();
			statement.close();
			connect.close();
		} catch (SQLException e) {
			System.out.println("Error in project");
			e.printStackTrace();
		}
		
		return projects;
	}
	
	/**
	 * Gets submissions for specific processing id, function is used when searching for a single project
	 * @param processingId Processing id of project we want the Submissions, SubmissionIOs, and DataElements for.
	 * @return Collection of Submissions which contain SubmissionIOs and DataElements as well
	 */
	private Collection<Submission> _getSubmissions(Long processingId) {
		ArrayList<Submission> submissions = new ArrayList<Submission>();
		
		try {
			Statement statement = connect.createStatement();
			
			String sql = "SELECT s.* FROM Submission as s WHERE s.ProcessingID = " + processingId;
			ResultSet submissionSet = statement.executeQuery(sql);
			
			while (submissionSet.next()) {
				ArrayList<SubmissionIO> subIOs = new ArrayList<SubmissionIO>();
				
				Submission sub = new Submission();
				sub.setDbId(submissionSet.getLong("SubmissionID"));
				sub.setName(submissionSet.getString("Name"));
				sub.setStatus(submissionSet.getString("Status"));
				sub.setResults(submissionSet.getBoolean("Results"));
				
				String sqlIO = "SELECT sio.*, de.*, sio.Type as SubIOType, de.Type as DataType "
						+ "FROM SubmissionIO as sio "
						+ "JOIN DataElement as de ON sio.DataID = de.DataID "
						+ "WHERE sio.SubmissionID = " + submissionSet.getLong("SubmissionID");
				
				Statement statementIO = connect.createStatement();
				ResultSet subIOSet = statementIO.executeQuery(sqlIO);
				
				while (subIOSet.next()) {
					SubmissionIO subIO = new SubmissionIO();
					subIO.setDbId(subIOSet.getLong("dbId"));
					subIO.setType(subIOSet.getString("SubIOType"));
					
					DataElement data = new DataElement();
					data.setDbId(subIOSet.getLong("DataID"));
					data.setName(subIOSet.getString("Name"));
					data.setScanID(subIOSet.getString("ScanID"));
					data.setURI(subIOSet.getString("URI"));
					data.setSubject(subIOSet.getString("Subject"));
					data.setType(subIOSet.getString("DataType"));
					data.setFormat(subIOSet.getString("Format"));
					data.setDate(subIOSet.getDate("Date"));
					data.setSize(subIOSet.getInt("Size"));
					
					subIO.setDataElement(data);
					
					subIOs.add(subIO);
				}
				
				subIOSet.close();
				statementIO.close();
				
				sub.setSubmissionIOs(subIOs);
				
				submissions.add(sub);
			}
			
			submissionSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("Error in submission");
			System.out.println(e);
		}
		
		return submissions;
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
		List<Project> projects = (List<Project>) executeSQL("SELECT ProjectName FROM Project WHERE ProjectName = '" + projectName + "'");

        return projects.isEmpty();
    }
}