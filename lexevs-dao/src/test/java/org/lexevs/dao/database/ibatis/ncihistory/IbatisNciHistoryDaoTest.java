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
package org.lexevs.dao.database.ibatis.ncihistory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.NCIHistory.NCIChangeEvent;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.versions.SystemRelease;
import org.LexGrid.versions.types.ChangeType;
import org.junit.Test;
import org.lexevs.dao.test.LexEvsDbUnitTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class IbatisEntityDaoTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Transactional
public class IbatisNciHistoryDaoTest extends LexEvsDbUnitTestBase {

	/** The ibatis entity dao. */
	@Autowired
	private IbatisNciHistoryDao ibatisNciHistoryDao;
	
	@Test
	@Transactional
	public void testGetBaseLinesNullDates() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date = new Timestamp(new Date().getTime());

		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
	
		List<SystemRelease> systemReleases = ibatisNciHistoryDao.getBaseLines("csguid", null, null);
		
		assertEquals(1,systemReleases.size());
		
		SystemRelease systemRelease = systemReleases.get(0);
		
		assertEquals("basedonsomerelease", systemRelease.getBasedOnRelease() );
		assertEquals("testsystemrelease", systemRelease.getEntityDescription().getContent() );
		assertEquals("testagency",  systemRelease.getReleaseAgency() );
		assertEquals(date, new Timestamp(systemRelease.getReleaseDate().getTime()));
		assertEquals("releaseid", systemRelease.getReleaseId() );
		assertEquals("releaseuri", systemRelease.getReleaseURI() );
	}
	
	@Test
	@Transactional
	public void testGetSystemReleaseForUri() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date = new Timestamp(new Date().getTime());
		
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
	
		SystemRelease systemRelease = ibatisNciHistoryDao.getSystemReleaseForReleaseUri("csguid", "releaseuri");

		assertEquals("basedonsomerelease", systemRelease.getBasedOnRelease() );
		assertEquals("testsystemrelease", systemRelease.getEntityDescription().getContent() );
		assertEquals("testagency",  systemRelease.getReleaseAgency() );
		assertEquals(date, new Timestamp(systemRelease.getReleaseDate().getTime()));
		assertEquals("releaseid", systemRelease.getReleaseId() );
		assertEquals("releaseuri", systemRelease.getReleaseURI() );
	}
	
	
	@Test
	@Transactional
	public void testGetBaseLinesNullDatesWithTwoResults() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date = new Timestamp(new Date().getTime());
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('1234', 'csguid', 'releaseuri', 'releaseid2',  ' " + date + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		List<SystemRelease> systemReleases = ibatisNciHistoryDao.getBaseLines("csguid", null, null);
		
		assertEquals(2,systemReleases.size());
	}
	
	@Test
	@Transactional
	public void testGetEarliestBaseline() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		Timestamp date2 = new Timestamp(10000l);
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('1234', 'csguid', 'releaseuri', 'releaseid2',  ' " + date2 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		SystemRelease systemRelease = ibatisNciHistoryDao.getEarliestBaseLine("csguid");
		
		assertEquals("releaseid" ,systemRelease.getReleaseId());
	}
	
	@Test
	@Transactional
	public void testGetLatestBaseline() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		Timestamp date2 = new Timestamp(10000l);
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('1234', 'csguid', 'releaseuri', 'releaseid2',  ' " + date2 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		SystemRelease systemRelease = ibatisNciHistoryDao.getLatestBaseLine("csguid");
		
		assertEquals("releaseid2" ,systemRelease.getReleaseId());
	}
	
	@Test
	@Transactional
	public void testInsertSystemRelease() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		final Timestamp date = new Timestamp(new Date().getTime());
		
		SystemRelease release = new SystemRelease();
		release.setReleaseAgency("testAgency");
		release.setReleaseURI("uri");
		release.setReleaseDate(date);
		release.setReleaseId("id");
		release.setBasedOnRelease("basedOn");
		
		EntityDescription ed = new EntityDescription();
		ed.setContent("desc");
		release.setEntityDescription(ed);
		
		ibatisNciHistoryDao.insertSystemRelease("csguid", release);
		
		template.queryForObject("Select * from ncihistsystemrelease", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				int i=1;
				
				assertNotNull(rs.getString(i++));
				assertEquals("csguid", rs.getString(i++));
				assertEquals("id", rs.getString(i++));
				assertEquals("uri", rs.getString(i++));
				assertEquals("basedOn", rs.getString(i++));
				assertEquals(date, rs.getTimestamp(i++));		
				assertEquals("testAgency", rs.getString(i++));
				assertEquals("desc", rs.getString(i++));
				
				return null;
			}
		});		
	}
	
	@Test
	@Transactional
	public void testGetDescendants() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date = new Timestamp(new Date().getTime());
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHist (ncitHistGuid, releaseGuid, entityCode, conceptName, editDate, editAction, referenceCode, referenceName)" +
				" values ('1', '123', 'C1234', 'name1', '" + date + "', 'merge', 'C3333', 'name2')");

		List<NCIChangeEvent> changeEvents = ibatisNciHistoryDao.getDescendants("csguid", "C1234");
		
		assertEquals(1,changeEvents.size());
		
		NCIChangeEvent event = changeEvents.get(0);

		assertEquals("C1234", event.getConceptcode());
		assertEquals("name1", event.getConceptName());
		assertEquals("C3333", event.getReferencecode());
		assertEquals(org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType.MERGE, event.getEditaction());
		assertEquals(date.getTime(), event.getEditDate().getTime());

	}
	
	@Test
	@Transactional
	public void testGetAncestors() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date = new Timestamp(new Date().getTime());
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHist (ncitHistGuid, releaseGuid, entityCode, conceptName, editDate, editAction, referenceCode, referenceName)" +
				" values ('1', '123', 'C1234', 'name1', '" + date + "', 'merge', 'C3333', 'name2')");

		List<NCIChangeEvent> changeEvents = ibatisNciHistoryDao.getAncestors("csguid", "C3333");
		
		assertEquals(1,changeEvents.size());
		
		NCIChangeEvent event = changeEvents.get(0);

		assertEquals("C1234", event.getConceptcode());
		assertEquals("name1", event.getConceptName());
		assertEquals("C3333", event.getReferencecode());
		assertEquals(org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType.MERGE, event.getEditaction());
		assertEquals(date.getTime(), event.getEditDate().getTime());

	}
	
	@Test
	@Transactional
	public void testInsertNciChangeEvent() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		final Timestamp date = new Timestamp(new Date().getTime());
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  NOW() , 'basedonsomerelease', 'testagency', 'testsystemrelease')");

		NCIChangeEvent event = new NCIChangeEvent();
		event.setConceptcode("C1");
		event.setConceptName("name1");
		event.setEditaction(org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType.MERGE);
		event.setEditDate(date);
		event.setReferencecode("C2");
		event.setReferencename("name2");
		
		ibatisNciHistoryDao.insertNciChangeEvent("123", event);
		
		template.queryForObject("Select * from ncihist", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				int i=1;
				
				assertNotNull(rs.getString(i++));
				assertEquals("123", rs.getString(i++));
				assertEquals("C1", rs.getString(i++));
				assertEquals("name1", rs.getString(i++));
				assertEquals("merge", rs.getString(i++));
				assertEquals(date, rs.getTimestamp(i++));		
				assertEquals("C2", rs.getString(i++));
				assertEquals("name2", rs.getString(i++));
				
				return null;
			}
		});		

	}
	
	@Test
	@Transactional
	public void testSystemReleaseForDateBetween() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		Timestamp date2 = new Timestamp(10000l);
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('1234', 'csguid', 'releaseuri', 'releaseid2',  ' " + date2 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		String systemReleaseUid = ibatisNciHistoryDao.getSystemReleaseUidForDate("csguid", new Timestamp(50l));
		
		assertEquals("1234", systemReleaseUid);
	}
	
	@Test
	@Transactional
	public void testSystemReleaseForDateEqualsFirst() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		Timestamp date2 = new Timestamp(10000l);
		
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('1234', 'csguid', 'releaseuri', 'releaseid2',  ' " + date2 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		String systemReleaseUid = ibatisNciHistoryDao.getSystemReleaseUidForDate("csguid", date1);
		
		assertEquals("123", systemReleaseUid);
	}
	
	@Test
	@Transactional
	public void testSystemReleaseForDateEqualsSecond() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		Timestamp date2 = new Timestamp(10000l);
		
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('123', 'csguid', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('1234', 'csguid', 'releaseuri', 'releaseid2',  ' " + date2 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		String systemReleaseUid = ibatisNciHistoryDao.getSystemReleaseUidForDate("csguid", date2);
		
		assertEquals("1234", systemReleaseUid);
	}
	
	@Test
	@Transactional
	public void testGetEditActionListWithUriSourceCode() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('sr1', 'csuri', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHist (ncithistguid, releaseguid, entitycode, conceptname, editaction, editdate, referencecode, referencename)" +
				" values ('h1', 'sr1', 'sc1', 'name1', 'merge', '" + date1 + "', 'rc1', 'name2')");
	
		List<NCIChangeEvent> event = ibatisNciHistoryDao.getEditActionList("csuri", "sc1", "releaseuri");
		
		assertEquals(1, event.size());
		
		assertEquals("sc1", event.get(0).getConceptcode());
	}
	
	@Test
	@Transactional
	public void testGetEditActionListWithUriReferenceCode() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('sr1', 'csuri', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHist (ncithistguid, releaseguid, entitycode, conceptname, editaction, editdate, referencecode, referencename)" +
				" values ('h1', 'sr1', 'sc1', 'name1', 'merge', '" + date1 + "', 'rc1', 'name2')");
	
		List<NCIChangeEvent> event = ibatisNciHistoryDao.getEditActionList("csuri", "rc1", "releaseuri");
		
		assertEquals(1, event.size());
		
		assertEquals("sc1", event.get(0).getConceptcode());
	}
	
	@Test
	@Transactional
	public void testGetEditActionListWithUriSourceAndReferenceCode() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('sr1', 'csuri', 'releaseuri', 'releaseid',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHist (ncithistguid, releaseguid, entitycode, conceptname, editaction, editdate, referencecode, referencename)" +
				" values ('h1', 'sr1', 'sc1', 'name1', 'merge', '" + date1 + "', 'rc1', 'name2')");
		
		template.execute("insert into nciHist (ncithistguid, releaseguid, entitycode, conceptname, editaction, editdate, referencecode, referencename)" +
				" values ('h2', 'sr1', 'sc2', 'name2', 'merge', '" + date1 + "', 'sc1', 'name2')");
	
		List<NCIChangeEvent> event = ibatisNciHistoryDao.getEditActionList("csuri", "sc1", "releaseuri");
		
		assertEquals(2, event.size());
	}
	
	@Test
	@Transactional
	public void testSystemReleaseForDateWithThree() throws InterruptedException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		Timestamp date1 = new Timestamp(1l);
		Timestamp date2 = new Timestamp(10000l);
		Timestamp date3 = new Timestamp(1000000l);
		
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('12', 'csguid', 'releaseuri1', 'releaseid1',  ' " + date1 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease')");
		
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('2', 'csguid', 'releaseuri2', 'releaseid2',  ' " + date2 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
	
		template.execute("insert into nciHistSystemRelease (releaseGuid, codingSchemeUri, releaseURI, releaseId, releaseDate, basedOnRelease, releaseAgency, description)" +
				" values ('3', 'csguid', 'releaseuri3', 'releaseid3',  ' " + date3 + "' , 'basedonsomerelease', 'testagency', 'testsystemrelease2')");
		
		Date searchDate = new Timestamp(100000l);
		
		String systemReleaseUid = ibatisNciHistoryDao.getSystemReleaseUidForDate("csguid", searchDate);
		
		assertEquals("3", systemReleaseUid);
	}
	
}