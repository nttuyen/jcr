/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exoplatform.services.jcr.impl.core.query.lucene;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.apache.lucene.search.Query;
import org.exoplatform.services.jcr.access.AccessManager;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.impl.core.SessionDataManager;
import org.exoplatform.services.jcr.impl.core.SessionImpl;


/**
 * <code>SingleColumnQueryResult</code> implements a query result that returns
 * a single column. That is, executes a lucene query.
 */
public class SingleColumnQueryResult extends QueryResultImpl {

    /**
     * The query to execute.
     */
    private final Query query;

    public SingleColumnQueryResult(SearchIndex index,
                                   SessionDataManager itemMgr,
                                   SessionImpl session,
                                   AccessManager accessMgr,
                                   AbstractQueryImpl queryImpl,
                                   Query query,
                                   SpellSuggestion spellSuggestion,
                                   InternalQName[] selectProps,
                                   QPath[] orderProps,
                                   boolean[] orderSpecs,
                                   boolean documentOrder,
                                   long offset,
                                   long limit) throws RepositoryException {
        super(index, itemMgr, session, accessMgr, queryImpl, spellSuggestion,
                selectProps, orderProps, orderSpecs, documentOrder, offset, limit);
        this.query = query;
        // if document order is requested get all results right away
        getResults(docOrder ? Integer.MAX_VALUE : index.getResultFetchSize());
    }

    /**
     * {@inheritDoc}
    * @throws RepositoryException 
     */
    protected MultiColumnQueryHits executeQuery(long resultFetchHint)
            throws IOException, RepositoryException {
        return index.executeQuery(session, queryImpl, query,
                orderProps, orderSpecs, resultFetchHint);
    }

    /**
     * {@inheritDoc}
     */
    protected ExcerptProvider createExcerptProvider() throws IOException {
        return index.createExcerptProvider(query);
    }
}
