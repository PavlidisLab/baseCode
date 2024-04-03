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
package ubic.basecode.ontology.jena;

import com.hp.hpl.jena.util.iterator.Map1Iterator;
import org.apache.jena.larq.ARQLuceneException;
import org.apache.jena.larq.HitLARQ;
import org.apache.jena.larq.IndexLARQ;
import org.apache.jena.larq.LARQ;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Customization to deal with MultiReader and 'open' while indexing is going on ...? Might not be needed.
 *
 * @author Paul
 */
class SearchIndex extends IndexLARQ {

    public SearchIndex( MultiReader r, Analyzer a ) {
        super( r, a );
    }

    @Override
    public Iterator<HitLARQ> search( String queryString ) {
        try {
            final IndexSearcher s = getIndexSearcher();
            Query query = getLuceneQueryParser().parse( queryString );

            TopDocs topDocs = s.search( query, null, LARQ.NUM_RESULTS );

            return new Map1Iterator<>( object -> new HitLARQ( s, object ), Arrays.asList( topDocs.scoreDocs ).iterator() );
        } catch ( Exception e ) {
            throw new ARQLuceneException( "Error during search for '" + queryString + ";", e );
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
