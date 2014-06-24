package nl.amc.biolab.autodock.input.objects;

import nl.amc.biolab.nsgdm.DataElement;
import nl.amc.biolab.nsgdm.Project;
import nl.amc.biolab.nsgdm.User;

public class JobSubmission {
	private String projectName = "";
	private String projectDescription = "";
	private boolean pilot = false;
	private DataElement ligands;
	private String ligandsUri = "";
	private Long ligandsCount = 0L;
	private DataElement pilotLigands;
	private String pilotLigandsUri = "";
	private int pilotLigandsCount = 0;
	private DataElement receptor;
	private String receptorUri = "";
	private DataElement configuration;
	private String configurationUri = "";
	private User user;
	private Project project;
	
	public String getProjectName() {
		if (this.isPilot()) {
			return projectName + "_pilot";
		}
		
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
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
	
	public DataElement getLigands() {
		return ligands;
	}
	
	public void setLigands(DataElement ligands) {
		this.ligands = ligands;
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
	
	public DataElement getPilotLigands() {
		return pilotLigands;
	}
	
	public void setPilotLigands(DataElement pilotLigands) {
		this.pilotLigands = pilotLigands;
	}
	
	public String getPilotLigandsUri() {
		return pilotLigandsUri;
	}
	
	public void setPilotLigandsUri(String pilotLigandsUri) {
		this.pilotLigandsUri = pilotLigandsUri;
	}
	
	public int getPilotLigandsCount() {
		return pilotLigandsCount;
	}
	
	public void setPilotLigandsCount(int pilotLigandsCount) {
		this.pilotLigandsCount = pilotLigandsCount;
	}
	
	public DataElement getReceptor() {
		return receptor;
	}
	
	public void setReceptor(DataElement receptor) {
		this.receptor = receptor;
	}
	
	public String getReceptorUri() {
		return receptorUri;
	}
	
	public void setReceptorUri(String receptorUri) {
		this.receptorUri = receptorUri;
	}
	
	public DataElement getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(DataElement configuration) {
		this.configuration = configuration;
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
	
	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
