package org.lexevs.locator;

import org.lexevs.dao.database.operation.LexEvsDatabaseOperations;
import org.lexevs.dao.database.service.DatabaseServiceManager;
import org.lexevs.dao.index.service.IndexService;
import org.lexevs.registry.service.Registry;
import org.lexevs.system.ResourceManager;
import org.lexevs.system.service.SystemResourceService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LexEvsServiceLocator {
	
	private static LexEvsServiceLocator serviceLocator;
	
	private static String BEAN_NAME = "lexEvsServiceLocator";
	private static String CONTEXT_FILE = "lexevsDao.xml";

	private DatabaseServiceManager databaseServiceManager;
	private ResourceManager resourceManager;
	private Registry registry;
	private LexEvsDatabaseOperations lexEvsDatabaseOperations;
	private SystemResourceService systemResourceService;
	private IndexService indexService;
	
	public static synchronized LexEvsServiceLocator getInstance(){
		if(serviceLocator == null){
			serviceLocator = (LexEvsServiceLocator) new ClassPathXmlApplicationContext(CONTEXT_FILE).getBean(BEAN_NAME);
		}
		return serviceLocator;
	}
	
	
	
	@Deprecated
	public ResourceManager getResourceManager() {
		return resourceManager;
	}
	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public void setLexEvsDatabaseOperations(LexEvsDatabaseOperations lexEvsDatabaseOperations) {
		this.lexEvsDatabaseOperations = lexEvsDatabaseOperations;
	}

	public LexEvsDatabaseOperations getLexEvsDatabaseOperations() {
		return lexEvsDatabaseOperations;
	}

	
	public void setDatabaseServiceManager(DatabaseServiceManager databaseServiceManager) {
		this.databaseServiceManager = databaseServiceManager;
	}

	public DatabaseServiceManager getDatabaseServiceManager() {
		return databaseServiceManager;
	}

	public void setSystemResourceService(SystemResourceService systemResourceService) {
		this.systemResourceService = systemResourceService;
	}

	public SystemResourceService getSystemResourceService() {
		return systemResourceService;
	}


	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {

		serviceLocator = (LexEvsServiceLocator) applicationContext.getBean(BEAN_NAME);	
	}



	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}



	public IndexService getIndexService() {
		return indexService;
	}
}
