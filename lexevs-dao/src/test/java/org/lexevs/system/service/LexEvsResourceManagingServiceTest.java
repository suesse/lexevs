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
package org.lexevs.system.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.codingSchemes.CodingScheme;
import org.junit.Test;
import org.lexevs.dao.database.service.DatabaseServiceManager;
import org.lexevs.dao.database.service.codingscheme.CodingSchemeService;
import org.lexevs.dao.test.LexEvsDbUnitTestBase;
import org.lexevs.registry.model.RegistryEntry;
import org.lexevs.registry.service.Registry;
import org.lexevs.registry.service.Registry.ResourceType;
import org.lexevs.system.service.LexEvsResourceManagingService.CodingSchemeAliasHolder;

/**
 * The Class LexEvsResourceManagingServiceTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class LexEvsResourceManagingServiceTest extends LexEvsDbUnitTestBase {

	/** The lex evs resource managing service. */
	@Resource
	private LexEvsResourceManagingService lexEvsResourceManagingService;
	
	/**
	 * Test setup.
	 */
	@Test
	public void testSetup(){
		assertNotNull(this.lexEvsResourceManagingService);
	}
	
	/**
	 * Test get class loader.
	 */
	@Test
	public void testGetClassLoader(){
		assertNotNull(lexEvsResourceManagingService.getClassLoader());
	}
	
	/**
	 * Test get internal version string for tag error.
	 * 
	 * @throws LBParameterException the LB parameter exception
	 */
	@Test(expected=LBParameterException.class)
	public void testGetInternalVersionStringForTagError() throws LBParameterException{
		assertEquals(
				"expectedVersion", 
				lexEvsResourceManagingService.getInternalVersionStringForTag("csNameINVALID", "TEST_TAG"));
	}
	
	/**
	 * Test get internal coding scheme name for user coding scheme name error.
	 * 
	 * @throws LBParameterException the LB parameter exception
	 */
	@Test(expected=LBParameterException.class)
	public void testGetInternalCodingSchemeNameForUserCodingSchemeNameError() throws LBParameterException{
		assertEquals(
				"csName", 
				lexEvsResourceManagingService.getInternalCodingSchemeNameForUserCodingSchemeName("someCsLocalName", "version"));
	}
	
	/**
	 * Test get internal coding scheme name for user coding scheme name by local name.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testGetInternalCodingSchemeNameForUserCodingSchemeNameByLocalName() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("version");
		entry.setTag("");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		replay(registryMock);
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("version");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "version")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(csService);
		
		service.setDatabaseServiceManager(manager);
		service.setRegistry(registryMock);
		service.initialize();
		
		assertEquals(
				"csName", 
				service.getInternalCodingSchemeNameForUserCodingSchemeName("someLocalName", "version"));
	}
	
	/**
	 * Test get internal coding scheme name for user coding scheme name by uri.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testGetInternalCodingSchemeNameForUserCodingSchemeNameByUri() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("version");
		entry.setTag("");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		replay(registryMock);
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("version");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "version")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(csService);
		
		service.setRegistry(registryMock);
		service.setDatabaseServiceManager(manager);
		service.initialize();
		
		assertEquals(
				"csName", 
				service.getInternalCodingSchemeNameForUserCodingSchemeName("uri", "version"));
	}
	
	/**
	 * Test get internal coding scheme name for user coding scheme name by formal name.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testGetInternalCodingSchemeNameForUserCodingSchemeNameByFormalName() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("version");
		entry.setTag("");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("version");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "version")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(registryMock, csService);	
		
		service.setRegistry(registryMock);
		service.setDatabaseServiceManager(manager);
		service.initialize();
		
		assertEquals(
				"csName", 
				service.getInternalCodingSchemeNameForUserCodingSchemeName("fname", "version"));
	}
	
	/**
	 * Test get internal coding scheme name for user coding scheme name by coding scheme name.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testGetInternalCodingSchemeNameForUserCodingSchemeNameByCodingSchemeName() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("version");
		entry.setTag("");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("version");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "version")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(csService, registryMock);	
		
		service.setRegistry(registryMock);
		service.setDatabaseServiceManager(manager);
		service.initialize();
		
		assertEquals(
				"csName", 
				service.getInternalCodingSchemeNameForUserCodingSchemeName("csName", "version"));
	}
	
	/**
	 * Test get internal version string for tag.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testGetInternalVersionStringForTag() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("v123");
		entry.setTag("someTag");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		expect(registryMock.getEntriesForUri("uri")).andReturn(entries);
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("v123");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "v123")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(csService, registryMock);	
		
		service.setRegistry(registryMock);
		
		service.setDatabaseServiceManager(manager);
		service.initialize();
		
		assertEquals(
				"v123", 
				service.getInternalVersionStringForTag("csName", "someTag"));
	}
	
	/**
	 * Test get internal version string for tag no match.
	 * 
	 * @throws Exception the exception
	 */
	@Test(expected=LBParameterException.class)
	public void testGetInternalVersionStringForTagNoMatch() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("v123");
		entry.setTag("_INVALID_someTag");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		expect(registryMock.getEntriesForUri("uri")).andReturn(entries);
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("version");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "v123")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(csService, registryMock);	
		
		service.setRegistry(registryMock);
		service.setDatabaseServiceManager(manager);
		service.initialize();
		
		assertEquals(
				"v123", 
				service.getInternalVersionStringForTag("csName", "someTag"));
	}
	
	/**
	 * Test get internal version string for tag multiple match.
	 * 
	 * @throws Exception the exception
	 */
	@Test(expected=LBParameterException.class)
	public void testGetInternalVersionStringForTagMultipleMatch() throws Exception{
		Registry registryMock = createMock(Registry.class);
		
		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("uri");
		entry.setResourceVersion("v123");
		entry.setTag("someTag");
		
		RegistryEntry entry2 = new RegistryEntry();
		entry2.setResourceUri("uri");
		entry2.setResourceVersion("v456");
		entry2.setTag("someTag");
		
		List<RegistryEntry> entries = new ArrayList<RegistryEntry>();
		entries.add(entry);
		entries.add(entry2);
		
		expect(registryMock.getAllRegistryEntriesOfType(ResourceType.CODING_SCHEME)).andReturn(entries);
		expect(registryMock.getEntriesForUri("uri")).andReturn(entries).anyTimes();
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		DatabaseServiceManager manager = new DatabaseServiceManager();
		
		CodingSchemeService csService = createMock(CodingSchemeService.class);
		
		CodingScheme cs = new CodingScheme();
		cs.setCodingSchemeName("csName");
		cs.setFormalName("fname");
		cs.setRepresentsVersion("version");
		cs.setCodingSchemeURI("uri");
		cs.addLocalName("someLocalName");
		
		expect(csService.getCodingSchemeByUriAndVersion("uri", "v123")).andReturn(cs).once();
		expect(csService.getCodingSchemeByUriAndVersion("uri", "v456")).andReturn(cs).once();
		
		manager.setCodingSchemeService(csService);
		
		replay(csService, registryMock);	
		service.setRegistry(registryMock);
		
		service.setDatabaseServiceManager(manager);
		service.setRegistry(registryMock);
		service.initialize();
		
		assertEquals(
				"v123", 
				service.getInternalVersionStringForTag("csName", "someTag"));
	}
	
	/**
	 * Test search performance.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testSearchPerformance() throws Exception{
		int limit = 10000;
		int localNames = 6;
		
		List<CodingSchemeAliasHolder> list = new ArrayList<CodingSchemeAliasHolder>();
		
		for(int i=0;i<limit;i++){
			CodingSchemeAliasHolder holder = new CodingSchemeAliasHolder();
			holder.setCodingSchemeName(String.valueOf(i));
			holder.setCodingSchemeUri(String.valueOf(i));
			holder.setFormalName(String.valueOf(i));

			holder.setLocalNames(new ArrayList<String>());
			for(int j=0;j<localNames;j++){
				holder.getLocalNames().add(String.valueOf(i)+String.valueOf(j));
			}
			holder.setRepresentsVersion(String.valueOf(i));
			list.add(holder);
		}
		
		LexEvsResourceManagingService service = new LexEvsResourceManagingService();
		
		long start = System.currentTimeMillis();
		boolean found = false;
		int skipped = 0;
		for(CodingSchemeAliasHolder alias : list){
			skipped++;
			found = found || service.hasAlias(alias, "90023");
			if(found){
				break;
			}
		}
		long time = System.currentTimeMillis() - start;
		
		assertTrue(found);
		assertTrue("Actual time: " + time, time < 20l);
	}

}
