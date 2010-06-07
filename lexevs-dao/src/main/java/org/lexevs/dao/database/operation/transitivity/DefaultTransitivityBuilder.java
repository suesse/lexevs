package org.lexevs.dao.database.operation.transitivity;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Utility.logging.LgLoggerIF;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.relations.AssociationEntity;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.lexevs.dao.database.access.DaoManager;
import org.lexevs.dao.database.access.association.AssociationDao;
import org.lexevs.dao.database.access.association.batch.TransitiveClosureBatchInsertItem;
import org.lexevs.dao.database.access.association.model.Node;
import org.lexevs.dao.database.access.association.model.Triple;
import org.lexevs.dao.database.access.codingscheme.CodingSchemeDao;
import org.lexevs.dao.database.service.DatabaseServiceManager;
import org.lexevs.dao.database.service.daocallback.DaoCallbackService;
import org.lexevs.dao.database.service.daocallback.DaoCallbackService.DaoCallback;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexevs.logging.LoggerFactory;
import org.lexevs.paging.AbstractPageableIterator;
import org.lexevs.registry.model.RegistryEntry;
import org.lexevs.registry.service.Registry;
import org.lexevs.registry.service.Registry.KnownTags;
import org.lexevs.registry.service.Registry.ResourceType;
import org.lexevs.system.service.SystemResourceService;

public class DefaultTransitivityBuilder implements TransitivityBuilder {

	private DatabaseServiceManager databaseServiceManager;

	private SystemResourceService systemResourceService;

	private Registry registry;

	private LgLoggerIF logger;
	
	public static void main(String[] args) {
		DefaultTransitivityBuilder builder = new DefaultTransitivityBuilder();
		builder.setLogger(LoggerFactory.getLogger());
		builder.setRegistry(LexEvsServiceLocator.getInstance().getRegistry());
		builder.setDatabaseServiceManager(LexEvsServiceLocator.getInstance().getDatabaseServiceManager());
		builder.setSystemResourceService(LexEvsServiceLocator.getInstance().getSystemResourceService());
		
		builder.computeTransitivityTable("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl", "10.05d");
		
	}

	public void computeTransitivityTable(String codingSchemeUri, String version) {
		
		BatchInsertController batchController = new BatchInsertController(codingSchemeUri, version);

		List<String> transitiveAssociations = this.getTransitiveAssociationPredicateIds(codingSchemeUri, version);

		for (String associationPredicateId : transitiveAssociations) {
			// make a hashset the holds the entire current set of
			// relations.        

			String sourceECNS = null;
			String sourceEC = null;
			String targetECNS = null;
			String targetEC = null;

			LRUMap insertedCache = new LRUMap(10000000);

			TripleIterator tripleIterator = new TripleIterator(codingSchemeUri, version, associationPredicateId);

			for(Triple triple : tripleIterator) {
				sourceECNS = triple.getSourceEntityNamespace();
				sourceEC = triple.getSourceEntityCode();
				targetECNS = triple.getTargetEntityNamespace();
				targetEC = triple.getTargetEntityCode();

				if (!sourceEC.equals("@") && !targetEC.equals("@@"))
				{
					StringTuple sourceCode = new StringTuple();                            
					sourceCode.namespace = sourceECNS;
					sourceCode.code = sourceEC;
					StringTuple targetCode = new StringTuple();                            
					targetCode.namespace = targetECNS;
					targetCode.code = targetEC;

					insertIntoTransitiveClosure( 
							associationPredicateId, 
							sourceCode,
							targetCode,
							insertedCache,
							batchController);
				}
			}  

			// get the unique source codes for this relationship - and
			// get all of the codes.
			logger.info("ComputeTransitive - Processing " + associationPredicateId);     

			List<Node> distinctSourceTriples = getDistinctSourceTriples(codingSchemeUri, version, associationPredicateId);

			ArrayList<StringTuple> sourceCodes = new ArrayList<StringTuple>();
			sourceECNS = null;
			sourceEC = null;
			targetECNS = null;
			targetEC = null;
			for (Node sourceNode : distinctSourceTriples) {
				sourceECNS = sourceNode.getEntityCodeNamespace();
				sourceEC = sourceNode.getEntityCode();
				if (!sourceEC.equals("@"))
				{
					StringTuple temp = new StringTuple();

					temp.namespace = sourceECNS;
					temp.code = sourceEC;
					sourceCodes.add(temp);
				}
			}

			// Now I have all of the top source codes for this
			// relationship. Need to recurse down the
			// tree
			// adding nodes to the transitive table as necessary.

			for (int j = 0; j < sourceCodes.size(); j++) {

				List<Node> targetNodes = getTargetTriples(
						codingSchemeUri, 
						version, 
						associationPredicateId,
						sourceCodes.get(j).code,
						sourceCodes.get(j).namespace);

				ArrayList<StringTuple> targetCodes = new ArrayList<StringTuple>();
				sourceECNS = null;
				sourceEC = null;
				targetECNS = null;
				targetEC = null;
				for(Node targetNode : targetNodes) {
					targetECNS = targetNode.getEntityCodeNamespace();
					targetEC = targetNode.getEntityCode();
					if (!targetEC.equals("@@"))
					{
						StringTuple temp = new StringTuple();
						temp.namespace = targetECNS;
						temp.code = targetEC;    
						targetCodes.add(temp);
					}
				}

				processTransitive(codingSchemeUri, version, associationPredicateId,
						sourceCodes.get(j), targetCodes, insertedCache, batchController);
			}
		}


		batchController.flush();
	}

	private List<Node> getTargetTriples(
			final String codingSchemeUri, 
			final String version,
			final String associationPredicateUid, 
			final String sourceEntityCode,
			final String sourceEntityCodeNamespace) {
		return databaseServiceManager.getDaoCallbackService().executeInDaoLayer(new DaoCallback<List<Node>>(){

			@Override
			public List<Node> execute(DaoManager daoManager) {
				String codingSchemeUid = daoManager.getCurrentCodingSchemeDao().
				getCodingSchemeUIdByUriAndVersion(codingSchemeUri, version);

				return daoManager.getCurrentCodedNodeGraphDao().
				getTargetNodesForSource(codingSchemeUid, associationPredicateUid, sourceEntityCode, sourceEntityCodeNamespace);
			}
		});
	}

	protected List<Node> getDistinctSourceTriples(
			final String codingSchemeUri, 
			final String version, 
			final String associationPredicateUid) {
		return databaseServiceManager.getDaoCallbackService().executeInDaoLayer(new DaoCallback<List<Node>>(){

			@Override
			public List<Node> execute(DaoManager daoManager) {
				String codingSchemeUid = daoManager.getCurrentCodingSchemeDao().
				getCodingSchemeUIdByUriAndVersion(codingSchemeUri, version);

				return daoManager.getCurrentCodedNodeGraphDao().
					getDistinctSourceNodesForAssociationPredicate(codingSchemeUid, associationPredicateUid);
			}

		});
	}

	private void processTransitive(
			String codingSchemeUri, 
			String codingSchemeVersion, 
			String associationPredicateUid, 
			StringTuple sourceCode,
			ArrayList<StringTuple> targetCodes, 
			LRUMap insertedCache,
			BatchInsertController batchInsertController) {
		// The next target of each of the passed in targetCodes needs to be
		// added to the transitive table.

		for (int i = 0; i < targetCodes.size(); i++) {

			ArrayList<StringTuple> targetTargets = new ArrayList<StringTuple>();
			String targetECNS = null;
			String targetEC = null;

			List<Node> targetNodes = getTargetTriples(
					codingSchemeUri, 
					codingSchemeVersion, 
					associationPredicateUid, 
					targetCodes.get(i).code, 
					targetCodes.get(i).namespace);
			for(Node targetNode : targetNodes) {
				targetECNS = targetNode.getEntityCodeNamespace();
				targetEC =  targetNode.getEntityCode();
				if (!targetEC.equals("@@"))
				{
					StringTuple temp = new StringTuple();
					temp.namespace = targetECNS;
					temp.code = targetEC;

					targetTargets.add(temp);
				}
			}

			// need to add an entry for the source code to each of these target
			// codes (if it doesn't already
			// exist, and if there isn't an entry in the regular table already

			for (int j = 0; j < targetTargets.size(); j++) {
				if (sourceCode.namespace.equals(targetTargets.get(j).namespace)
						&& sourceCode.code.equals(targetTargets.get(j).code)) {
					// if they equal each other, there is something wrong with
					// the code system. But I don't
					// want to fail.. so skip it.

					continue;
				}

				boolean iInserted = insertIntoTransitiveClosure(
						associationPredicateUid, 
						sourceCode, 
						targetTargets.get(j), 
						insertedCache,
						batchInsertController);               
				if (!iInserted) {
					// If I didn't insert it into the transitive table, it was
					// already there
					// or unnecessary. No need to do the recursion below, so
					// remove it.
					targetTargets.remove(j);
					j--;
				}
			}

			// Now, need to recurse.
			while (targetTargets.size() > 0) {
				if (sourceCode.namespace.equals( targetTargets.get(0).namespace)
						// && sourceCode.b.equals((
						// targetTargets.get(0)).b)
						&& sourceCode.code.equals( targetTargets.get(0).code)) {
					// if they equal each other, there is something wrong with
					// the code system. But I don't
					// want to fail.. so skip it.
					targetTargets.remove(0);
					continue;
				}

				// need to pass in an array list - put the current item in one.
				ArrayList<StringTuple> temp = new ArrayList<StringTuple>();
				temp.add(targetTargets.get(0));
				// remove it, since we will be done with it after this.
				targetTargets.remove(0);
				processTransitive(codingSchemeUri, codingSchemeVersion, associationPredicateUid, sourceCode, temp, insertedCache, batchInsertController);
			}
		}
	}

	private boolean insertIntoTransitiveClosure(
			String associationPredicateId, 
			StringTuple sourceCode, 
			StringTuple targetCode, 
			LRUMap insertedCache,
			BatchInsertController batchInsertController) {
		String key = sourceCode.code + ":" + sourceCode.namespace + ":" + targetCode.code + ":" + targetCode.namespace;

		boolean iInserted = false;
	
		if (!insertedCache.containsKey(key)) {
			// if it is not loaded in the main table, or already loaded
			// in the transitive table


			insertedCache.put(key, null);
			iInserted = true;
			batchInsertController.insertIntoTransitiveClosure( 
					sourceCode.code,
					sourceCode.namespace,
					targetCode.code, 
					targetCode.namespace, 
					associationPredicateId);

		}
		return iInserted;
	}

	private class StringTuple {
		String code;
		String namespace;
	}

	protected List<Triple> getTriples(
			final String codingSchemeUri,
			final String version,
			final String associationPredicateId, 
			final int start, 
			final int pageSize){
		return databaseServiceManager.getDaoCallbackService().executeInDaoLayer(new DaoCallback<List<Triple>>(){

			@Override
			public List<Triple> execute(DaoManager daoManager) {
				String codingSchemeId = daoManager.getCurrentCodingSchemeDao().
				getCodingSchemeUIdByUriAndVersion(codingSchemeUri, version);

				return daoManager.getCurrentAssociationDao().
				getAllTriplesOfCodingScheme(codingSchemeId, associationPredicateId, start, pageSize);
			}

		});
	}

	protected List<String> getTransitiveAssociationPredicateIds(
			final String codingSchemeUri, 
			final String version){

		return databaseServiceManager.getDaoCallbackService().executeInDaoLayer(new DaoCallback<List<String>>(){

			@Override
			public List<String> execute(DaoManager daoManager) {
				List<String> transitivePredicateIds = new ArrayList<String>();

				CodingSchemeDao codingSchemeDao = daoManager.getCurrentCodingSchemeDao();
				AssociationDao associationDao = daoManager.getCurrentAssociationDao();			

				String codingSchemeId = codingSchemeDao.
				getCodingSchemeUIdByUriAndVersion(codingSchemeUri, version);
				
				CodingScheme cs = codingSchemeDao.getCodingSchemeByUriAndVersion(codingSchemeUri, version);

				Mappings mappings = cs.getMappings();

				List<String> relationsIds = associationDao
				.getRelationsUIdsForCodingSchemeUId(codingSchemeId);

				for(String relationsId : relationsIds) {
					List<String> associatinPredicateIds = 
						associationDao.getAssociationPredicateUIdsForRelationsUId(codingSchemeId, relationsId);

					for(String associationPredicateId : associatinPredicateIds) {
						String associationName = associationDao.getAssociationPredicateNameForUId(codingSchemeId, associationPredicateId);

						SupportedAssociation supportedAssociation = getSupportedAssociationWithName(mappings, associationName);

						//if there's no supported association, skip.
						if(supportedAssociation == null) {
							continue;
						}
						
				        String containingCodingScheme = supportedAssociation.getCodingScheme();
				        String containingEntityCode = supportedAssociation.getEntityCode();
				        String containingEntityCodeNamespace = supportedAssociation.getEntityCodeNamespace();

				        //if there's no entityCode, assume the AssociationName
				        if(StringUtils.isBlank(containingEntityCode)){
				            containingEntityCode = supportedAssociation.getLocalId();
				        }

				        //if there's no codingSchemeName, assume local
				        if(StringUtils.isBlank(containingCodingScheme)){
				            containingCodingScheme = cs.getCodingSchemeName();
				        }

				        //if there's no entityCodeNamespace, assume default
				        if(StringUtils.isBlank(containingEntityCodeNamespace)){
				            containingEntityCodeNamespace = cs.getCodingSchemeName();
				        }
				        
				        RegistryEntry entry = getRegistryEntryForCodingSchemeName(containingCodingScheme);

						boolean isTransitive = isTransitive(entry.getResourceUri(), entry.getResourceVersion(), containingEntityCode, containingEntityCodeNamespace);

						if(isTransitive) {
							transitivePredicateIds.add(associationPredicateId);
						}
					}
				}

				return transitivePredicateIds;
			}
		});
	}

	protected SupportedAssociation getSupportedAssociationWithName(Mappings mappings, String associationName) {
		for(SupportedAssociation sa : mappings.getSupportedAssociation()) {
			if(sa.getLocalId().equals(associationName)) {
				return sa;
			}
		}
		return null;
	}

	protected boolean isTransitive(String codingSchemeUri, String version, String code, String namespace) {
		AssociationEntity associationEntity = 
			this.databaseServiceManager.getEntityService().
			getAssociationEntity(codingSchemeUri, version, code, namespace);
		return (associationEntity != null && BooleanUtils.toBoolean(associationEntity.getIsTransitive()));
	}

	protected RegistryEntry getRegistryEntryForCodingSchemeName(String codingSchemeName) {
		try {
			String uri = this.systemResourceService.getUriForUserCodingSchemeName(codingSchemeName);

			List<RegistryEntry> entries = 
				this.registry.getAllRegistryEntriesOfTypeAndURI(ResourceType.CODING_SCHEME, uri);

			if(entries.size() == 0) {
				return entries.get(0);
			} else {
				//try to find the production scheme
				for(RegistryEntry entry : entries) {
					if(StringUtils.isNotBlank(entry.getTag()) &&
							entry.getTag().equals(KnownTags.PRODUCTION.toString())) {
						return entry;
					}
				}

				//if there isn't one marked production, just pick the first one
				return entries.get(0);
			}
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setSystemResourceService(SystemResourceService systemResourceService) {
		this.systemResourceService = systemResourceService;
	}

	public SystemResourceService getSystemResourceService() {
		return systemResourceService;
	}

	public DatabaseServiceManager getDatabaseServiceManager() {
		return databaseServiceManager;
	}

	public void setDatabaseServiceManager(
			DatabaseServiceManager databaseServiceManager) {
		this.databaseServiceManager = databaseServiceManager;
	}

	public LgLoggerIF getLogger() {
		return logger;
	}

	public void setLogger(LgLoggerIF logger) {
		this.logger = logger;
	}

	private class TripleIterator extends AbstractPageableIterator<Triple>{

		private String codingSchemeUri; 
		private String version; 
		private String associationPredicateId;

		private TripleIterator(String codingSchemeUri, String version, String associationPredicateId) {
			super(1000);
			this.codingSchemeUri = codingSchemeUri;
			this.version = version;
			this.associationPredicateId = associationPredicateId;
		}
		@Override
		protected List<Triple> doPage(int currentPosition, int pageSize) {
			return getTriples(codingSchemeUri, version, associationPredicateId, currentPosition, pageSize);
		}

	}
	
	private class BatchInsertController {
		
		private int batchSize = 5000;
		
		private String codingSchemeUid;
		
		private List<TransitiveClosureBatchInsertItem> batch = new ArrayList<TransitiveClosureBatchInsertItem>();
		
		private BatchInsertController(final String codingSchemeUri, final String version) {
			codingSchemeUid = databaseServiceManager.getDaoCallbackService().executeInDaoLayer(new DaoCallback<String>(){

				@Override
				public String execute(DaoManager daoManager) {
					return daoManager.getCurrentCodingSchemeDao().
					getCodingSchemeUIdByUriAndVersion(codingSchemeUri, version);
				}
			});
		}
		
		private void insertIntoTransitiveClosure(
			final String sourceCode,
			final String sourceNamespace,
			final String targetCode, 
			final String targetNamespace, 
			final String associationPredicateId) {
			
			TransitiveClosureBatchInsertItem item = new TransitiveClosureBatchInsertItem();
			item.setSourceEntityCode(sourceCode);
			item.setSourceEntityCodeNamespace(sourceNamespace);
			item.setTargetEntityCode(targetCode);
			item.setTargetEntityCodeNamespace(targetNamespace);
			item.setAssociationPredicateId(associationPredicateId);
			
			batch.add(item);
			
			if(batch.size() >= batchSize) {
				flush();
			}
		}	
		
		private void flush() {
			DaoCallbackService callbackService = databaseServiceManager.getDaoCallbackService();
			try {
				callbackService.executeInDaoLayer(new DaoCallback<Object>(){

					@Override
					public String execute(DaoManager daoManager) {
						daoManager.getCurrentAssociationDao().
						insertBatchTransitiveClosure(codingSchemeUid, batch);
						return null;
					}
				});
			} catch (Exception e1) {
				logger.debug("Batch insert of Transitive closure times failed -- falling back to inserting one at a time.");
				
				for(final TransitiveClosureBatchInsertItem item : batch) {
					
					try {
						callbackService.executeInDaoLayer(new DaoCallback<Object>() {

							@Override
							public Object execute(DaoManager daoManager) {
								daoManager.getCurrentAssociationDao().insertIntoTransitiveClosure(
										codingSchemeUid, 
										item.getAssociationPredicateId(), 
										item.getSourceEntityCode(), 
										item.getSourceEntityCodeNamespace(), 
										item.getTargetEntityCode(), 
										item.getTargetEntityCodeNamespace());
								return null;
							}
						});
					} catch (Exception e2) {
						logger.warn("Error inserting Transitive closure item.", e2);
					}
				}
			}
			batch.clear();
		}
	}
}
