package nl.amc.biolab.tools;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import docking.crappy.logger.Logger;
import nl.amc.biolab.config.exceptions.ReaderException;
import nl.amc.biolab.config.manager.ConfigurationManager;
import nl.amc.biolab.datamodel.manager.HibernateUtil;

public class HibernateListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        try {
        	System.out.println("################################### HERE");
            // Initialise configuration manager for this portlet
			ConfigurationManager.init();
			
			System.out.println("################################### HERE 2");
	        // Initialise logger for this portlet
	        new Logger();
	        
	        System.out.println("################################### HERE 3");
	    	// Create database sessionfactory
	        HibernateUtil.getSessionFactory();
		} catch (ReaderException e) {
			e.printStackTrace();
		}
    }

    public void contextDestroyed(ServletContextEvent event) {
    	HibernateUtil.getSessionFactory().close();
    }
}