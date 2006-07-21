package ubic.basecode;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {
    
    protected abstract void setUp() throws Exception;

    protected abstract void tearDown() throws Exception;
}
