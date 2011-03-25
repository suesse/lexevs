/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
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
package org.cts2.internal.model.uri.restrict;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.cts2.core.MatchAlgorithmReference;
import org.cts2.internal.match.AttributeResolver;
import org.cts2.internal.match.ResolvableModelAttributeReference;
import org.cts2.uri.ValueSetDefinitionDirectoryURI;

/**
 * The Class ValueSetDefinitionRestrictionHandler.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class ValueSetDefinitionRestrictionHandler extends AbstractIterableLexEvsBackedRestrictionHandler<ValueSetDefinition,ValueSetDefinitionDirectoryURI>{

	/* (non-Javadoc)
	 * @see org.cts2.internal.model.uri.restrict.AbstractIterableLexEvsBackedRestrictionHandler#registerSupportedModelAttributes()
	 */
	@Override
	public List<ResolvableModelAttributeReference<ValueSetDefinition>> registerSupportedModelAttributeReferences() {
		List<ResolvableModelAttributeReference<ValueSetDefinition>> returnList = 
			new ArrayList<ResolvableModelAttributeReference<ValueSetDefinition>>();
		
		
		
		AttributeResolver<ValueSetDefinition> valueSetDefinitionNameResolver = new AttributeResolver<ValueSetDefinition>(){

			@Override
			public String resolveAttribute(ValueSetDefinition modelObject) {
				return modelObject.getValueSetDefinitionName();
			}
		};
	
		//TODO: Example only
		ResolvableModelAttributeReference<ValueSetDefinition> valueSetDefinitionName = new ResolvableModelAttributeReference<ValueSetDefinition>(valueSetDefinitionNameResolver);
		valueSetDefinitionName.setContent("valueSetDefinitionName");
		
		returnList.add(valueSetDefinitionName);
		
		
		
		return returnList;
	}

	@Override
	public List<IterableRestriction<ValueSetDefinition>> processOtherRestictions(
			ValueSetDefinitionDirectoryURI directoryUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MatchAlgorithmReference> registerSupportedMatchAlgorithmReferences() {
		// TODO Auto-generated method stub
		return null;
	}
}
