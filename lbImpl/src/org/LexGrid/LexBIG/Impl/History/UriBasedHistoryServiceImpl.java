package org.LexGrid.LexBIG.Impl.History;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeVersionList;
import org.LexGrid.LexBIG.DataModel.Collections.NCIChangeEventList;
import org.LexGrid.LexBIG.DataModel.Collections.SystemReleaseList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.SystemReleaseDetail;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.versions.CodingSchemeVersion;
import org.LexGrid.versions.SystemRelease;
import org.lexevs.dao.database.service.ncihistory.NciHistoryService;
import org.lexevs.locator.LexEvsServiceLocator;

public class UriBasedHistoryServiceImpl implements HistoryService {
    
    private static final long serialVersionUID = 524036401675315235L;
    
    private DateFormat dateFormat = NciHistoryService.dateFormat;

    private String uri;
    
    private NciHistoryService nciHistoryService;

    public UriBasedHistoryServiceImpl(String codingSchemeUri) throws LBParameterException {
        nciHistoryService = LexEvsServiceLocator.getInstance().getDatabaseServiceManager().getNciHistoryService();
        uri = codingSchemeUri;  
    }
    
    @Override
    public NCIChangeEventList getAncestors(ConceptReference conceptReference) throws LBParameterException,
            LBInvocationException {
       return nciHistoryService.getAncestors(uri, conceptReference);
    }

    @Override
    public SystemReleaseList getBaselines(Date releasedAfter, Date releasedBefore) throws LBParameterException,
            LBInvocationException {
        return this.nciHistoryService.getBaseLines(uri, releasedAfter, releasedBefore);
    }

    @Override
    public CodingSchemeVersionList getConceptChangeVersions(ConceptReference conceptReference, Date beginDate,
            Date endDate) throws LBParameterException, LBInvocationException {
        return this.nciHistoryService.getConceptChangeVersions(uri, conceptReference, beginDate, endDate);
    }

    @Override
    public CodingSchemeVersion getConceptCreationVersion(ConceptReference conceptReference)
            throws LBParameterException, LBInvocationException {
        CodingSchemeVersion creationVersion = this.nciHistoryService.getConceptCreationVersion(uri, conceptReference);
        if(creationVersion == null) {
            throw new LBParameterException("No results found for parameter:", "conceptReference");
        }
        
        return creationVersion;
    }

    @Override
    public NCIChangeEventList getDescendants(ConceptReference conceptReference) throws LBParameterException,
            LBInvocationException {
        return nciHistoryService.getDescendants(uri, conceptReference);
    }

    @Override
    public SystemRelease getEarliestBaseline() throws LBInvocationException {
        return nciHistoryService.getEarliestBaseLine(uri);
    }

    @Override
    public NCIChangeEventList getEditActionList(ConceptReference conceptReference,
            CodingSchemeVersion codingSchemeVersion) throws LBParameterException, LBInvocationException {
        try {
            Date date = dateFormat.parse(codingSchemeVersion.getVersion());
            
            return nciHistoryService.getEditActionList(uri, conceptReference, date);
        } catch (ParseException e) {
           throw new LBParameterException(e.getMessage());
        } 
    }

    @Override
    public NCIChangeEventList getEditActionList(ConceptReference conceptReference, Date beginDate, Date endDate)
            throws LBParameterException, LBInvocationException {
        return nciHistoryService.getEditActionList(uri, conceptReference, beginDate, endDate);
    }

    @Override
    public NCIChangeEventList getEditActionList(ConceptReference conceptReference, URI releaseURN)
            throws LBParameterException, LBInvocationException {
        return nciHistoryService.getEditActionList(uri, conceptReference, releaseURN);
    }

    @Override
    public SystemRelease getLatestBaseline() throws LBInvocationException {
        return nciHistoryService.getLatestBaseLine(uri);
    }

    @Override
    public SystemReleaseDetail getSystemRelease(URI releaseURN) throws LBParameterException, LBInvocationException {
        return nciHistoryService.getSystemRelease(uri, releaseURN);
    }

}