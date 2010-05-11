/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.lexevs.dao.database.service.valuesets;

import java.util.List;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.naming.Mappings;
import org.LexGrid.valueSets.PickListDefinition;
import org.LexGrid.valueSets.PickListDefinitions;

/**
 * The Interface PickListService.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface PickListDefinitionService {
	
	/**
	 * Gets the pick list definition by pick list id.
	 * 
	 * @param pickListId the pick list id
	 * 
	 * @return the pick list definition by pick list id
	 */
	public PickListDefinition getPickListDefinitionByPickListId(String pickListId);
	
	/**
	 * Gets the pick list definition id's by value set definition uri.
	 * 
	 * @param valueDomainUri the value set definition uri
	 * 
	 * @return list of pick list definition id that match value set definition uri
	 */
	public List<String> getPickListDefinitionIdForValueSetDefinitionUri(String valueSetDefUri);
	
	/**
	 * 
	 * @param entityCode
	 * @param entityCodeNameSpace
	 * @param propertyId
	 * @param extractPickListName
	 * @return
	 * @throws LBException
	 */
	public List<String> getPickListDefinitionIdForEntityReference(String entityCode, String entityCodeNameSpace, String propertyId);
	
	/**
	 * Returns list of pick list definition IDs that contains supplied Supported Attribute Tag and Value.
	 * 
	 * @param supportedTag SupportedAttribute tag like SupportedCodingScheme, SupportedAssociation etc.
	 * @param value value of the supportedAttribute
	 * @return list of picklistIds
	 */
	public List<String> getPickListDefinitionIdForSupportedTagAndValue(String supportedTag, String value);
	
	/**
	 * Removes the pick list definition by pick list id.
	 * 
	 * @param pickListId the pick list id
	 */
	public void removePickListDefinitionByPickListId(String pickListId);

	/**
	 * Insert pick list definition.
	 * 
	 * @param definition the definition
	 * @param systemReleaseUri the system release uri
	 * @param mappings SupportedAttribute mappings of pick list definition
	 */
	public void insertPickListDefinition(PickListDefinition definition, String systemReleaseUri, Mappings mappings) throws LBParameterException, LBException;
	
	/**
	 * Insert pick list definitions.
	 * 
	 * @param definitions the pick list definitions
	 * @param systemReleaseUri the system release uri
	 */
	public void insertPickListDefinitions(PickListDefinitions definitions, String systemReleaseUri);

	/**
	 * List pick list ids.
	 * 
	 * @return the list< string>
	 */
	public List<String> listPickListIds() ;	
	
	public void updatePickListDefinition(PickListDefinition definition) throws LBException;
	
	public void removePickListDefinition(PickListDefinition definition);
	
	public void updateVersionableAttributes(PickListDefinition definition) throws LBException;
	
	public void insertDependentChanges(PickListDefinition definition) throws LBException;
	
	public void revise(PickListDefinition pickListDefinition, String releaseURI) throws LBException;
	
}
