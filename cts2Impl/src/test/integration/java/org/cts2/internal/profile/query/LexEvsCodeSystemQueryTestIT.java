package org.cts2.internal.profile.query;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.cts2.test.BaseCts2IntegrationTest;
import org.cts2.uri.CodeSystemDirectoryURI;
import org.junit.Test;

public class LexEvsCodeSystemQueryTestIT extends BaseCts2IntegrationTest {
	
	@Resource
	private LexEvsCodeSystemQuery lexEvsCodeSystemQuery;
	
	@Test
	public void testInit(){
		assertNotNull(lexEvsCodeSystemQuery);
	}
	
	@Test
	@LoadContent(contentPath="classpath:content/Automobiles.xml")
	public void testGetAllCodeSystemsNotNull(){
		assertNotNull(lexEvsCodeSystemQuery.getAllCodeSystems());
	}
	
	@Test
	@LoadContent(contentPath="classpath:content/Automobiles.xml")
	public void testGetCount(){
		CodeSystemDirectoryURI directoryURI = lexEvsCodeSystemQuery.getAllCodeSystems();
		assertEquals(1,lexEvsCodeSystemQuery.count(directoryURI, null));
	}
	
}