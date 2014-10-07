package nl.amc.biolab.autodock.input.objects;

import java.util.UUID;

import nl.amc.biolab.datamodel.objects.Project;
import nl.amc.biolab.datamodel.objects.User;

public class JobSubmission {
	private String projectName = "";
	private String projectDescription = "";
	private boolean pilot = false;
	private String ligandsUri = "";
	private Long ligandsCount = 0L;
	private String pilotLigandsUri = "";
	private Long pilotLigandsCount = 0L;
	private String receptorUri = "";
	private String configurationUri = "";
	private User user;
	private Project project;
	private String outputUri = "";
	
	private String uriBase = "";
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public void setProjectFolder(String projectName) {
		this.uriBase = projectName + "-" + UUID.randomUUID().toString();
	}
	
	public String getProjectFolder() {
		return uriBase;
	}
	
	public boolean isPilot() {
		return pilot;
	}
	
	public void setPilot(boolean pilot) {
		this.pilot = pilot;
	}
	
	public String getProjectDescription() {
		return projectDescription;
	}
	
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	
	public String getLigandsUri() {
		return ligandsUri;
	}
	
	public void setLigandsUri(String ligandsUri) {
		this.ligandsUri = ligandsUri;
	}
	
	public Long getLigandsCount() {
		return ligandsCount;
	}
	
	public void setLigandsCount(Long ligandsCount) {
		this.ligandsCount = ligandsCount;
	}
	
	public String getPilotLigandsUri() {
		return pilotLigandsUri;
	}
	
	public void setPilotLigandsUri(String pilotLigandsUri) {
		this.pilotLigandsUri = pilotLigandsUri;
	}
	
	public Long getPilotLigandsCount() {
		return pilotLigandsCount;
	}
	
	public void setPilotLigandsCount(Long pilotLigandsCount) {
		this.pilotLigandsCount = pilotLigandsCount;
	}
	
	public String getReceptorUri() {
		return receptorUri;
	}
	
	public void setReceptorUri(String receptorUri) {
		this.receptorUri = receptorUri;
	}
	
	public String getConfigurationUri() {
		return configurationUri;
	}
	
	public void setConfigurationUri(String configurationUri) {
		this.configurationUri = configurationUri;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getUserString() {
		return user.getFirstName() + " " + user.getLastName();
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public String getOutputUri() {
		return outputUri;
	}
	
	public void setOutputUri(String outputUri) {
		this.outputUri = outputUri;
	}
}
