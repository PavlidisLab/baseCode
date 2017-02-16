/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.ontology.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.jena.larq.ARQLuceneException;
import org.apache.jena.larq.HitLARQ;
import org.apache.jena.larq.IndexLARQ;
import org.apache.jena.larq.LARQ;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.Map1Iterator;

/**
 * Customization to deal with MultiReader and 'open' while indexing is going on ...? Might not be needed.
 * 
 * @author Paul
 * @version $Id$
 */
public class SearchIndex extends IndexLARQ {

    public SearchIndex( IndexReader r ) {
        super( r );
    }

    public SearchIndex( MultiReader r, Analyzer a ) {
        super( r, a );
    }

    @SuppressWarnings("resource")
    @Override
    public Iterator<HitLARQ> search( String queryString ) {
        try {
            final IndexSearcher s = getIndexSearcher();
            Query query = getLuceneQueryParser().parse( queryString );

            TopDocs topDocs = s.search( query, ( Filter ) null, LARQ.NUM_RESULTS );

            Map1<ScoreDoc, HitLARQ> converter = new Map1<ScoreDoc, HitLARQ>() {
                @Override
                public HitLARQ map1( ScoreDoc object ) {
                    return new HitLARQ( s, object );
                }
            };
            Iterator<ScoreDoc> iterScoreDoc = Arrays.asList( topDocs.scoreDocs ).iterator();
            Iterator<HitLARQ> iter = new Map1Iterator<ScoreDoc, HitLARQ>( converter, iterScoreDoc );

            return iter;
        } catch ( Exception e ) {
            throw new ARQLuceneException( "search", e );
        }
    }

    private synchronized IndexSearcher getIndexSearcher() throws IOException {
        if ( !reader.isCurrent() ) {
            // this is the problematic line ... multireader cannot be reopened; was IndexReader newReader =
            // IndexReader.openIfChanged(reader, true) ;

            IndexReader newReader = IndexReader.openIfChanged( reader );
            if ( newReader != null ) {
                reader.close();
                reader = newReader;
                searcher = new IndexSearcher( reader );
            }
        }

        return searcher;
    }

}
