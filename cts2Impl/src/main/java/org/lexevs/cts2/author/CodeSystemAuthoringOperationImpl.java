package org.lexevs.cts2.author;

import java.util.Date;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Properties;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.commonTypes.Text;
import org.LexGrid.concepts.Entity;
import org.LexGrid.naming.Mappings;
import org.LexGrid.versions.ChangedEntry;
import org.LexGrid.versions.Revision;
import org.LexGrid.versions.types.ChangeType;
import org.apache.commons.lang.StringUtils;
import org.lexevs.cts2.core.update.RevisionInfo;
import org.lexevs.cts2.exception.author.InvalidCodeSystemSupplementException;

public class CodeSystemAuthoringOperationImpl extends AuthoringCore implements
		CodeSystemAuthoringOperation {
	
	@Override
	public int commitChangeSet(Revision changeSet) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	protected void commitCodeSystemChangeSet(CodingScheme codeSystem, RevisionInfo revision, String previousRevisionID, ChangeType changeType, Boolean indexCodeSystem) throws LBException {
        
        // Create the changed entry for code system
       ChangedEntry changedEntry = new ChangedEntry();
       changedEntry.setChangedCodingSchemeEntry(codeSystem);
       
       // Create revision object
       Revision lgRevision = getLexGridRevisionObject(revision);
       
       // Add code system changed entry to revision
       lgRevision.addChangedEntry(changedEntry);
       
       // Assign the entry state
       codeSystem.setEntryState(populateEntryState(changeType, lgRevision.getRevisionId(), previousRevisionID, 0L));
       
       //load as revision
       this.getDatabaseServiceManager().getAuthoringService().loadRevision(lgRevision, null, indexCodeSystem);
       
	}

	

	@Override
	public void createAssociationType() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public CodingScheme createCodeSystem(RevisionInfo revision, String codingSchemeName, String codingSchemeURI, String formalName,
	        String defaultLanguage, long approxNumConcepts, String representsVersion, List<String> localNameList,
	        List<Source> sourceList, Text copyright, Mappings mappings) throws LBException {
	
	      if(codingSchemeName == null){
	            throw new LBException("Coding scheme name cannot be null");
	        }
	        if(codingSchemeURI == null){
	            throw new LBException("Coding scheme URI cannot be null");
	        }
	        if(representsVersion == null){
	            throw new LBException("Coding scheme version cannot be null");
	        }
	        if(mappings == null){
	            throw new LBException("Coding scheme mappings cannot be null");
	        }
	        
	        
	        CodingScheme scheme = new CodingScheme();
	        
	        scheme.setCodingSchemeName(codingSchemeName);
	        
	        scheme.setCodingSchemeURI(codingSchemeURI);
	
	        if (formalName != null)
	        	scheme.setFormalName(formalName);
	        
	        if (defaultLanguage != null)
	        	scheme.setDefaultLanguage(defaultLanguage);
	       
	       scheme.setApproxNumConcepts(approxNumConcepts);
	        
	        scheme.setRepresentsVersion(representsVersion);
	
	        if (localNameList != null)
	        	scheme.setLocalName(localNameList);
	
	        if (sourceList != null)
	        	scheme.setSource(sourceList);
	
	        if (copyright != null)
	        	scheme.setCopyright(copyright);
	
	        scheme.setMappings(mappings);
	        
		
	        // Ensure RevisionInfo is provided
	        validateRevisionInfo(revision);
	        
	        commitCodeSystemChangeSet(scheme, revision, null, ChangeType.NEW, true);
	        
	        return scheme;
	}

	/*
	 * (non-Javadoc)
	 * @see org.lexevs.cts2.author.CodeSystemAuthoringOperation#activateCodeSystemVersion(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean activateCodeSystemVersion(String codeSystemNameOrURI, String codeSystemVersion) throws LBException{
		this.activateCodingSchemeVersion(Constructors.createAbsoluteCodingSchemeVersionReference(
				this.getCodeSystemURI(codeSystemNameOrURI),codeSystemVersion));
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.lexevs.cts2.author.CodeSystemAuthoringOperation#deactivateCodeSystemVersion(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean deactivateCodeSystemVersion(String codeSystemNameOrURI, String codeSystemVersion) throws LBException{
		this.deactivateCodingSchemeVersion(Constructors.createAbsoluteCodingSchemeVersionReference(
				this.getCodeSystemURI(codeSystemNameOrURI),codeSystemVersion), new Date());
		return true;
	}

	@Override
	public boolean removeCodeSystem(RevisionInfo revision, String codingSchemeURI, String representsVersion) throws LBException {
		
        if(codingSchemeURI == null){
            throw new LBException("Coding scheme URI cannot be null");
        }
        
        CodingScheme codingScheme = 
        	this.getDatabaseServiceManager().
        		getCodingSchemeService().
        		getCompleteCodingScheme(codingSchemeURI, representsVersion);
        
        if (codingScheme == null)
			throw new LBException("No Coding Scheme found with URI : " + codingSchemeURI.toString());
        		
        // Ensure RevisionInfo is provided
        validateRevisionInfo(revision);
        
        commitCodeSystemChangeSet(codingScheme, revision, null, ChangeType.REMOVE, null);
        
        return true;
}

	
	@Override
	public CodingScheme updateCodeSystem(RevisionInfo revision, String codingSchemeName, String codingSchemeURI, String formalName,
	        String defaultLanguage, long approxNumConcepts, String representsVersion, List<String> localNameList,
	        List<Source> sourceList, Text copyright, Mappings mappings) throws LBException {
	
			if(codingSchemeURI == null){
				throw new LBException("Coding scheme URI cannot be null");
			}	
	     
	        CodingScheme codingScheme = 
	        	this.getDatabaseServiceManager().
	        		getCodingSchemeService().
	        		getCompleteCodingScheme(codingSchemeURI, representsVersion);
	        
	        String prevRevisionId = codingScheme.getEntryState() != null?codingScheme.getEntryState().getContainingRevision():null;
	         
	        if (codingScheme == null)
				throw new LBException("No Coding Scheme found with URI : " + codingSchemeURI.toString());
	        
	
	        if (StringUtils.isNotEmpty(codingSchemeName))
	        	codingScheme.setCodingSchemeName(codingSchemeName);
	        
	        if (StringUtils.isNotEmpty(codingSchemeURI))
	        	codingScheme.setCodingSchemeURI(codingSchemeURI);
	
	        if (StringUtils.isNotEmpty(formalName))
	        	codingScheme.setFormalName(formalName);
	
	        if (StringUtils.isNotEmpty(defaultLanguage))
	        	codingScheme.setDefaultLanguage(defaultLanguage);
	        
	        if (approxNumConcepts !=0L)
	        	codingScheme.setApproxNumConcepts(approxNumConcepts);
	        
	        if (representsVersion !=null)
	        	codingScheme.setRepresentsVersion(representsVersion);
	
	        if (localNameList != null)
	        	codingScheme.setLocalName(localNameList);
	
	        if (sourceList != null)
	        	codingScheme.setSource(sourceList);
	        
	        if (copyright !=null)
	        	codingScheme.setCopyright(copyright);
	        
	        if (mappings !=null)
	        	codingScheme.setMappings(mappings);

		
	        // Ensure RevisionInfo is provided
	        validateRevisionInfo(revision);
	        
	        commitCodeSystemChangeSet(codingScheme, revision, prevRevisionId, ChangeType.MODIFY, null);
	        
	        return codingScheme;
	}

	@Override
	public CodingScheme addCodeSystemProperties(RevisionInfo revision, String codingSchemeName, String codingSchemeURI, String representsVersion,
	        Properties properties) throws LBException {
	
			if(codingSchemeURI == null){
				throw new LBException("Coding scheme URI cannot be null");
			}	
	     
	        CodingScheme codingScheme = 
	        	this.getDatabaseServiceManager().
	        		getCodingSchemeService().
	        		getCompleteCodingScheme(codingSchemeURI, representsVersion);
	        
	        String prevRevisionId = codingScheme.getEntryState() != null?codingScheme.getEntryState().getContainingRevision():null;
	         
	        if (codingScheme == null)
				throw new LBException("No Coding Scheme found with URI : " + codingSchemeURI.toString());
	        
	        if (properties !=null)
	        	
	        	codingScheme.setProperties(processAddProperties(revision, properties));
		
	        // Ensure RevisionInfo is provided
	        validateRevisionInfo(revision);
	        
	        commitCodeSystemChangeSet(codingScheme, revision, prevRevisionId, ChangeType.MODIFY, null);
	        
	        return codingScheme;
	}

	@Override
	public CodingScheme updateCodeSystemProperties(RevisionInfo revision, String codingSchemeName, String codingSchemeURI, String representsVersion,
	        Properties properties) throws LBException {
	
			if(codingSchemeURI == null){
				throw new LBException("Coding scheme URI cannot be null");
			}	
	     
	        CodingScheme codingScheme = 
	        	this.getDatabaseServiceManager().
	        		getCodingSchemeService().
	        		getCompleteCodingScheme(codingSchemeURI, representsVersion);
	        
	        String prevRevisionId = codingScheme.getEntryState() != null?codingScheme.getEntryState().getContainingRevision():null;
	         
	        if (codingScheme == null)
				throw new LBException("No Coding Scheme found with URI : " + codingSchemeURI.toString());
	        
	        if (properties !=null)
	        	
	        	codingScheme.setProperties(processUpdateProperties(revision, properties));
		
	        // Ensure RevisionInfo is provided
	        validateRevisionInfo(revision);
	        
	        
	        commitCodeSystemChangeSet(codingScheme, revision, prevRevisionId, ChangeType.MODIFY, null);
	        
	        return codingScheme;
	}

	@Override
	public CodingScheme removeCodeSystemProperty(RevisionInfo revision, String codingSchemeURI, String representsVersion,
            String propertyId) throws LBException {

			if(codingSchemeURI == null){
				throw new LBException("Coding scheme URI cannot be null");
			}	
	     
	        CodingScheme codingScheme = 
	        	this.getDatabaseServiceManager().
	        		getCodingSchemeService().
	        		getCompleteCodingScheme(codingSchemeURI, representsVersion);
	        
	        String prevRevisionId = codingScheme.getEntryState() != null?codingScheme.getEntryState().getContainingRevision():null;
	         
	        if (codingScheme == null)
				throw new LBException("No Coding Scheme found with URI : " + codingSchemeURI.toString());
	        
			
			Property currentProperty = null;
			
			for (Property prop : codingScheme.getProperties().getPropertyAsReference())
			{
				if (prop.getPropertyId().equalsIgnoreCase(propertyId))
					currentProperty = prop;
			}
			
			if (currentProperty == null)
				throw new LBException("No property found with id : " + propertyId);
			
			// remove all other properties but the one that needs to be removed
			codingScheme.setProperties(null);		
			Properties props = new Properties();
			props.addProperty(currentProperty);
			codingScheme.setProperties(props);
			
			// setup entry state for coding scheme
			codingScheme.setEntryState(populateEntryState(ChangeType.DEPENDENT, 
					revision.getRevisionId(), prevRevisionId, 0L));
			
			// setup entry state for property to be removed
			String propPrevRevId = currentProperty.getEntryState() != null?currentProperty.getEntryState().getContainingRevision():null;
			currentProperty.setEntryState(populateEntryState(ChangeType.REMOVE, 
					revision.getRevisionId(), propPrevRevId, 0L));
			
	
	        // Ensure RevisionInfo is provided
	        validateRevisionInfo(revision);
	        
	        commitCodeSystemChangeSet(codingScheme, revision, prevRevisionId, ChangeType.DEPENDENT, null);
	        
	        return codingScheme;
	}
	

	protected Properties processAddProperties(RevisionInfo revision, Properties properties) {

		Property currentProperty = null;
		Properties updatedProps = new Properties();
		
		for (Property prop : properties.getPropertyAsReference())
		{
				currentProperty = prop;
	
				// setup entry state for property to be changed
				String propPrevRevId = currentProperty.getEntryState() != null?currentProperty.getEntryState().getContainingRevision():null;
				currentProperty.setEntryState(populateEntryState(ChangeType.NEW, 
						revision.getRevisionId(), propPrevRevId, 0L));
				updatedProps.addProperty(currentProperty);
		}
		
		return updatedProps;
		
	}

	protected Properties processUpdateProperties(RevisionInfo revision, Properties properties) {

		Property currentProperty = null;
		Properties updatedProps = new Properties();
		
		
		for (Property prop : properties.getPropertyAsReference())
		{
				currentProperty = prop;
	
				// setup entry state for property to be changed
				String propPrevRevId = currentProperty.getEntryState() != null?currentProperty.getEntryState().getContainingRevision():null;
				currentProperty.setEntryState(populateEntryState(ChangeType.MODIFY, 
						revision.getRevisionId(), propPrevRevId, 0L));
				updatedProps.addProperty(currentProperty);
		}
		
		return updatedProps;
		
	}



	@Override
	public Revision createCodeSystemChangeSet(String agent,
			String changeInstruction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createCodeSystemSuppliment(
			AbsoluteCodingSchemeVersionReference parent,
			AbsoluteCodingSchemeVersionReference supplement) throws InvalidCodeSystemSupplementException {
		try {
			LexBIGServiceImpl.defaultInstance().getServiceManager(null).
				registerCodingSchemeAsSupplement(parent, supplement);
		} catch (LBException e) {
			throw new InvalidCodeSystemSupplementException(
					parent, 
					supplement, 
					e);
		} 
	}

	@Override
	public void createConcept(
			String codingSchemeUri, 
			String codeSystemVersion, 
			String conceptCode, 
			String namespace, 
			RevisionInfo revisionInfo) throws LBException {

		Entity entity = new Entity();
		entity.setEntityCode(conceptCode);
		entity.setEntityCodeNamespace(namespace);
		
		this.doReviseConcept(
				codingSchemeUri, 
				codeSystemVersion, 
				entity, 
				ChangeType.NEW, 
				null, 
				0l, 
				revisionInfo);
	}

	@Override
	public void updateAssociationType() {
		// TODO Auto-generated method stub (IMPLEMENT!)
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCodeSystemSuppliment(CodingScheme codingScheme, RevisionInfo revisionInfo) {
		// TODO Auto-generated method stub (IMPLEMENT!)
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCodeSystemVersion(String codingScheme, RevisionInfo revisionInfo) {
		// TODO Auto-generated method stub (IMPLEMENT!)
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCodeSystemVersionStatus(
			String codingSchemeURI, 
			String codeSystemVersion, 
			String status,
			Boolean isActive,
			RevisionInfo revision) throws LBException {
		
		this.validatedCodingScheme(codingSchemeURI, codeSystemVersion);
		
		
		CodingScheme codingScheme = 
        	this.getDatabaseServiceManager().
        		getCodingSchemeService().
        		getCompleteCodingScheme(codingSchemeURI, codeSystemVersion);
		
		String prevRevisionId = codingScheme.getEntryState() != null?codingScheme.getEntryState().getContainingRevision():null;
		
		if(StringUtils.isNotBlank(status)) {
			codingScheme.setStatus(status);
		}
		
		if(isActive != null) {
			codingScheme.setIsActive(isActive);
		}
		
		
        // Ensure RevisionInfo is provided
        validateRevisionInfo(revision);
        
        commitCodeSystemChangeSet(codingScheme, revision, prevRevisionId, ChangeType.VERSIONABLE, null);
        
	}
	

	public void updateConcept(
			String codingSchemeUri, 
			String codeSystemVersion, 
			Entity entity,
			RevisionInfo revisionInfo) throws LBException {	
		this.doReviseConcept(
				codingSchemeUri, 
				codeSystemVersion, 
				entity, 
				ChangeType.MODIFY, 
				null,
				0l, 
				revisionInfo);
	}

	@Override
	public void addNewConceptProperty(
			String codingSchemeUri,
			String codeSystemVersion, 
			String conceptCode, 
			String namespace,
			Property property, 
			RevisionInfo revisionInfo) throws LBException {
		this.doReviseEntityProperty(
				codingSchemeUri, 
				codeSystemVersion, 
				conceptCode, 
				namespace, 
				property, 
				ChangeType.NEW, 
				null, 
				0l, 
				revisionInfo);
	}

	@Override
	public void deleteConcept(String codingSchemeUri, String codeSystemVersion,
			String conceptCode, String namespace, RevisionInfo revision)
			throws LBException {
		// TODO Auto-generated method stub (IMPLEMENT!)
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteConceptProperty(String codingSchemeUri,
			String codeSystemVersion, String conceptCode, String namespace,
			Property property, RevisionInfo revisionInfo) throws LBException {
		this.doReviseEntityProperty(
				codingSchemeUri, 
				codeSystemVersion, 
				conceptCode, 
				namespace, 
				property, 
				ChangeType.REMOVE, 
				null, 
				0l, 
				revisionInfo);
	}

	@Override
	public void updateConceptProperty(
			String codingSchemeUri,
			String codeSystemVersion, 
			String conceptCode, 
			String namespace,
			Property property, 
			RevisionInfo revisionInfo) throws LBException {
		this.doReviseEntityProperty(
				codingSchemeUri, 
				codeSystemVersion, 
				conceptCode, 
				namespace, 
				property, 
				ChangeType.MODIFY, 
				null, 
				0l, 
				revisionInfo);
	}

	protected void doReviseConcept(
			String codingSchemeUri, 
			String codingSchemeVersion, 
			Entity entity, 
			ChangeType changeType,
			String prevRevisionId,
			Long relativeOrder,
			RevisionInfo revisionInfo) throws LBException {
		
		this.validatedCodingScheme(codingSchemeUri, codingSchemeVersion);
		
		Revision revision = this.populateRevisionShell(
				codingSchemeUri, 
				codingSchemeVersion, 
				entity, 
				changeType, 
				prevRevisionId, 
				relativeOrder, 
				revisionInfo);
		
		this.getDatabaseServiceManager().getAuthoringService().loadRevision(revision, revisionInfo.getSystemReleaseURI(), null);
	}
	
	protected void doReviseEntityProperty(
			String codingSchemeUri, 
			String codingSchemeVersion, 
			String entityCode, 
			String entityCodeNamespace, 
			Property property,
			ChangeType changeType,
			String prevRevisionId,
			Long relativeOrder,
			RevisionInfo revisionInfo) throws LBException {
		
		this.validatedCodingScheme(codingSchemeUri, codingSchemeVersion);
		
		Revision revision = this.populateRevisionShell(
				codingSchemeUri, 
				codingSchemeVersion, 
				entityCode, 
				entityCodeNamespace,
				property,
				changeType, 
				prevRevisionId, 
				relativeOrder, 
				revisionInfo);
		
		this.getDatabaseServiceManager().getAuthoringService().loadRevision(revision, revisionInfo.getSystemReleaseURI(), null);
	}

	@Override
	public void updateConceptStatus(
			String codingSchemeUri, 
			String codeSystemVersion, 
			String conceptCode, 
			String namespace,
			String status,
			Boolean isActive,
			RevisionInfo revisionInfo) throws LBException {
		this.validatedCodingScheme(codingSchemeUri, codeSystemVersion);
		
		Entity entity = new Entity();
		entity.setEntityCode(conceptCode);
		entity.setEntityCodeNamespace(namespace);
		
		if(StringUtils.isNotBlank(status)) {
			entity.setStatus(status);
		}
		
		if(isActive != null) {
			entity.setIsActive(isActive);
		}
		
		Revision revision = this.populateRevisionShell(
				codingSchemeUri, 
				codeSystemVersion, 
				entity, 
				ChangeType.VERSIONABLE, 
				null, 
				0l, 
				revisionInfo);
		
		this.getDatabaseServiceManager().getAuthoringService().loadRevision(revision, revisionInfo.getSystemReleaseURI(), null);
	}
}
