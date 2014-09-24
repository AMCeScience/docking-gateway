package docking.crappy.logger;

import java.util.Date;

/**
 *
 * @author Allard van Altena
 */
public class Logger {
    public Logger log;
    
    public Logger() {
        log = this;
    }
    
    public void log(Object message) {
        Date date = new Date();
        
        System.out.println("Autodockvina portlet, " + date.toString() + " MSG: " + message);
    }
}
