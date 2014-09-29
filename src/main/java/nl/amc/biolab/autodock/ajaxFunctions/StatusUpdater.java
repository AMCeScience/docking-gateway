package nl.amc.biolab.autodock.ajaxFunctions;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import nl.amc.biolab.autodock.constants.VarConfig;
import nl.amc.biolab.persistencemanager.PersistenceManagerPlugin;

/**
 * Calls the processingmanager update status function and returns the new status
 *
 * @author Allard van Altena
 */
public class StatusUpdater extends VarConfig {
	public StatusUpdater() {
	}

	public String updateStatus(Long processId) {

		log("updating status...");

		// Update the status through the processingmanager webservice
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);

		WebResource webResource = client.resource(config.getProcessingResource() + "/submission/" + processId);

//		ClientResponse response = webResource.accept("application/json").type("application/json").get();

//		log(response);

		log("done");

		// Open a session
		PersistenceManagerPlugin db = new PersistenceManagerPlugin();
		db.init();

		// Get the updated status from the database
		String newStatus = db.get.processing(processId).getSubmissions().iterator().next().getLastStatus().getValue();

		db.shutdown();

		return newStatus;
	}
}
