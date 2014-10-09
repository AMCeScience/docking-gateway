package docking.crappy.logger;

import java.util.Date;

import nl.amc.biolab.config.exceptions.ReaderException;
import nl.amc.biolab.config.manager.ConfigurationManager;

/**
 * Simple logger function
 * 
 * @author Allard van Altena
 */
public class Logger {
	public static final int json = 5;
	public static final int sql = 4;
	public static final int debug = 3;
	public static final int error = 2;
	public static final int exception = 1;
	
    /**
     * Outputs a formatted message with the portlet name and the current datetime
     * @param message Message we want to output into the logs
     */    
    public static void log(Object message, int level) {
        Date date = new Date();
        
        try {
			if (level <= ConfigurationManager.read.getIntegerItem("logging", "level")) {
				if (message instanceof Exception) {
					System.out.println("Autodockvina portlet, " + date.toString() + " STACK: ");
					
		    		((Exception) message).printStackTrace();
		    	} else {
		    		System.out.println("Autodockvina portlet, " + date.toString() + " MSG: " + message);
		    	}
			}
		} catch (ReaderException e) {
			System.out.println("Autodockvina porlet, " + date.toString() + " MSG: " + e.getMessage());
		}
    }
}