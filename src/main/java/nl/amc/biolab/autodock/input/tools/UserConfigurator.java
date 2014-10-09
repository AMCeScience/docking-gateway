package nl.amc.biolab.autodock.input.tools;

import nl.amc.biolab.exceptions.PersistenceException;
import nl.amc.biolab.persistencemanager.PersistenceManagerPlugin;

public class UserConfigurator {
	private PersistenceManagerPlugin PERSISTENCE;
	
	public UserConfigurator() throws PersistenceException {
        _setDb(new PersistenceManagerPlugin());
        
        _getDb().init();
    }
    
    public void close() {
        _getDb().shutdown();
    }
    
    public boolean checkUser(String liferayUserId) {
    	boolean success = _getDb().checkUserAuth(liferayUserId);
    	
    	return success;
    }
    
    public boolean setupUser(String liferayUserId) {
    	// Setup app hook
    	_getDb().initApp();
    	
    	boolean success = _getDb().userSetup(liferayUserId);
    	
    	return success;
    }
	
	private void _setDb(PersistenceManagerPlugin persistence) {
        PERSISTENCE = persistence;
    }
    
    private PersistenceManagerPlugin _getDb() {
        return PERSISTENCE;
    }
}
