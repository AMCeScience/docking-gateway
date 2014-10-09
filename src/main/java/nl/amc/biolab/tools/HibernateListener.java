package nl.amc.biolab.tools;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import docking.crappy.logger.Logger;
import nl.amc.biolab.config.exceptions.ReaderException;
import nl.amc.biolab.config.manager.ConfigurationManager;
import nl.amc.biolab.datamodel.manager.HibernateUtil;

public class HibernateListener implements ServletContextListener {
	private final String file_path = "guse/apache-tomcat-6.0.36/webapps/config.json";

    public void contextInitialized(ServletContextEvent event) {
    	// Create database sessionfactory
        HibernateUtil.getSessionFactory();
        
        try {
            // Initialise configuration manager for this portlet
			new ConfigurationManager(file_path);
			
	        // Initialise logger for this portlet
	        new Logger();
		} catch (ReaderException e) {
			e.printStackTrace();
		}
    }

    public void contextDestroyed(ServletContextEvent event) {
    	HibernateUtil.getSessionFactory().close();
    }
}