package baseCodeTest.dataStructure;

import junit.framework.*;
import baseCode.dataStructure.*;

/**
 * <p>  </p>
 * <p>  </p>
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class TestStack
    extends TestCase {
   private Stack stack = null;
   private Stack stackShort = null;

   protected void setUp() throws Exception {
      super.setUp();

      /**@todo verify the constructors*/
      stack = new Stack();
      stack.push( new Integer( 1 ) );
      stack.push( new Integer( 2 ) );
      stack.push( new Integer( 3 ) );
      stack.push( new Integer( 4 ) );
      stack.push( new Integer( 5 ) );

      stackShort = new Stack( 3 );
      stackShort.push( new Integer( 1 ) );
   }

   protected void tearDown() throws Exception {
      stack = null;
      super.tearDown();
   }

   public void testPop() {
      Integer expectedReturn = new Integer( 3 );
      stack.pop();
      stack.pop();
      Integer actualReturn = ( Integer ) stack.pop();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testPush() {
      Integer expectedReturn = new Integer( 100 );
      stack.push( new Integer( 101 ) );
      stack.push( new Integer( 102 ) );
      stack.push( new Integer( 103 ) );
      stack.push( expectedReturn );
      Integer actualReturn = ( Integer ) stack.pop();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testTop() {
      Integer expectedReturn = new Integer( 5 );
      Integer actualReturn = ( Integer ) stack.top();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testEmptyPop() {
      stackShort.pop();
      Integer actualReturn = ( Integer ) stackShort.pop();
      Integer expectedReturn = null;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFullPush() {
      try {
         stackShort.push( new Integer( 495 ) );
         stackShort.push( new Integer( 495 ) );
         stackShort.push( new Integer( 495 ) );
         stackShort.push( new Integer( 495 ) );
         stackShort.push( new Integer( 495 ) );
         fail( "Should raise an IndexOutOfBoundsException" );
      }
      catch ( IndexOutOfBoundsException success ) {}

   }

}
