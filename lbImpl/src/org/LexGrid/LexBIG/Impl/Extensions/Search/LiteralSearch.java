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
package org.LexGrid.LexBIG.Impl.Extensions.Search;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.ExtensionDescription;
import org.LexGrid.LexBIG.Extensions.Query.Search;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.lexevs.dao.index.indexer.LuceneLoaderCode;


/**
 * The Class LiteralSearch.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class LiteralSearch extends AbstractLiteralSearch {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3232896063519315405L;
     
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Impl.Extensions.AbstractExtendable#buildExtensionDescription()
     */
    @Override
    protected ExtensionDescription buildExtensionDescription() {
        ExtensionDescription ed = new ExtensionDescription();
        ed.setExtensionBaseClass(Search.class.getName());
        ed.setExtensionClass(LiteralSearch.class.getName());
        ed
        .setDescription("All special characters are taken litterally.");
        ed.setName("literal");
        ed.setVersion("1.0");
        return ed;
    }
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Extensions.Query.Search#buildQuery(java.lang.String)
     */
    public Query doBuildQuery(String searchText) {
        QueryParser queryParser = super.getQueryParser();
        
        try {
            return queryParser.parse(LuceneLoaderCode.LITERAL_PROPERTY_VALUE_FIELD + ":(" + searchText + ")");
        } catch (ParseException e) {
           throw new RuntimeException(e);
        }
    } 
}
