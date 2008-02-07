package ubic.basecode.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * @author luke
 *
 */
public class BatchIteratorTest extends TestCase
{
    private static final Collection<String> items = Arrays.asList( new String[] {
        "alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "theta", "iota", "kappa",
        "lambda", "mu", "nu", "xi", "omicron", "pi", "rho", "sigma", "tau", "upsilon", 
        "phi", "chi", "psi", "omega"
    } );
    
    /**
     * Test method for {@link ca.elmonline.util.BatchIterator#BatchIterator(java.util.Collection, int)}.
     */
    public void testBatchIterator()
    {
        Collection<String> batch;
        BatchIterator<String> iterator = new BatchIterator<String>( items, 10 );
        batch = iterator.next();
        assertEquals( "first batch contains 10 items",
                10, batch.size() );
        batch = iterator.next();
        assertEquals( "second batch contains 10 items",
                10, batch.size() );
        batch = iterator.next();
        assertEquals( "third batch contains 4 items",
                4, batch.size() );
        assertEquals( "no more batches",
                false, iterator.hasNext() );
        try {
            batch = iterator.next();
            fail( "NoSuchElementException not thrown" );
        } catch ( NoSuchElementException e ) {
        }
    }
    
    /**
     * Test method for {@link ca.elmonline.util.BatchIterator#batches(java.util.Collection, int)}.
     */
    public void testBatches()
    {
        int batchCount = 0;
        for ( Collection<String> batch : BatchIterator.batches( items, 10 ) ) {
            if ( ++batchCount < 3 )
                assertEquals( String.format( "batch #%d contains 10 items", batchCount ),
                        10, batch.size() );
            else
                assertEquals( "last batch contains 4 items",
                        4, batch.size() );
        }
        assertEquals( "found 3 batches", 3, batchCount );
    }
    
    public void testConcurrentModificationException()
    {
        Collection<String> modifiableItems = new ArrayList( items );
        try {
            for ( Collection<String> batch : BatchIterator.batches( modifiableItems, 10 ) ) {
                modifiableItems.clear();
            }
            fail( "ConcurrentModificationException not thrown" );
        } catch ( ConcurrentModificationException e ) {
        }
    }
}
