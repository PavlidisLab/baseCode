/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Example usage:
 * </p>
 * <code>
 * Collection<String> items;
 * int batchSize = 1000;
 * for ( Collection<String> batch : BatchIterator.batches( items, batchSize ) ) {
 * } 
 * </code>
 * <p>
 * or
 * </p>
 * <code>
 * Collection<String> items;
 * int batchSize = 1000;
 * BatchIterator<String> iterator = new BatchIterator<String>( items, batchSize );
 * while ( iterator.hasNext() ) {
 *     Collection<String> batch = iterator.next();
 * }
 * </code>
 * 
 * @author luke
 * @version $Id$
 */
public class BatchIterator<E> implements Iterable<Collection<E>>, Iterator<Collection<E>> {

    /**
     * Returns a BatchIterator over the specified collection. This is a convenience method to simplify the code need to
     * loop over an existing collection.
     * 
     * @param collection the collection over which to iterate
     * @param batchSize the maximum size of each batch returned
     * @return a BatchIterator over the specified collection
     */
    public static <E> BatchIterator<E> batches( Collection<E> collection, int batchSize ) {
        return new BatchIterator<E>( collection, batchSize );
    }

    private int batchSize;

    private Iterator<E> individualIterator;

    /**
     * Returns a BatchIterator over the specified collection.
     * 
     * @param collection the collection over which to iterate
     * @param batchSize the maximum size of each batch returned
     */
    public BatchIterator( Collection<E> collection, int batchSize ) {
        individualIterator = collection.iterator();
        this.batchSize = batchSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return individualIterator.hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Collection<E>> iterator() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public Collection<E> next() {
        if ( !individualIterator.hasNext() ) throw new NoSuchElementException();

        Collection<E> batch = new ArrayList<E>( batchSize );
        while ( batch.size() < batchSize && individualIterator.hasNext() )
            batch.add( individualIterator.next() );
        return batch;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}