package ubic.basecode.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author luke
 * 
 * <p>Example usage:</p>
 * <code>
 * Collection<String> items;
 * int batchSize = 1000;
 * for ( Collection<String> batch : BatchIterator.batches( items, batchSize ) ) {
 * } 
 * </code>
 * <p>or</p>
 * <code>
 * Collection<String> items;
 * int batchSize = 1000;
 * BatchIterator<String> iterator = new BatchIterator<String>( items, batchSize );
 * while ( iterator.hasNext() ) {
 *     Collection<String> batch = iterator.next();
 * }
 * </code>
 */
public class BatchIterator<E> implements Iterable<Collection<E>>, Iterator<Collection<E>>
{
    private Iterator<E> individualIterator;
    
    private int batchSize;
    
    private int cursor = 0;
    
    /**
     * Returns a BatchIterator over the specified collection.
     */
    public BatchIterator( Collection<E> collection, int batchSize )
    {
        individualIterator = collection.iterator();
        this.batchSize = batchSize;
    }
    
    public static <E> BatchIterator<E> batches( Collection<E> collection, int batchSize )
    {
        return new BatchIterator( collection, batchSize );
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Collection<E>> iterator() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return individualIterator.hasNext();
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Collection<E> next()
    {
        Collection<E> batch = new ArrayList<E>( batchSize );
        while ( batch.size() < batchSize && individualIterator.hasNext() )
            batch.add( individualIterator.next() );
        return batch;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}