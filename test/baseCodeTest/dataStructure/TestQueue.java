package baseCodeTest.dataStructure;

import junit.framework.TestCase;
import baseCode.dataStructure.Queue;

/**
 * <p>
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public class TestQueue extends TestCase {
   private Queue queue = null;
   private Queue shortQueue = null;

   protected void setUp() throws Exception {
      super.setUp();
      queue = new Queue();
      queue.enqueue( new Integer( 1 ) );
      queue.enqueue( new Integer( 2 ) );
      queue.enqueue( new Integer( 3 ) );
      queue.enqueue( new Integer( 4 ) );
      queue.enqueue( new Integer( 5 ) );

      shortQueue = new Queue( 3 );
   }

   protected void tearDown() throws Exception {
      queue = null;
      super.tearDown();
   }

   public void testEnqueue() {
      Integer expectedReturn = new Integer( 101 );
      queue.enqueue( new Integer( 100 ) );
      queue.enqueue( new Integer( 101 ) );
      queue.enqueue( new Integer( 102 ) );
      queue.enqueue( new Integer( 103 ) );
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      Integer actualReturn = ( Integer ) queue.dequeue();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testDequeue() {
      Integer expectedReturn = new Integer( 3 );
      queue.dequeue();
      queue.dequeue();
      Integer actualReturn = ( Integer ) queue.dequeue();
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testEmptyDequeue() {
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      queue.dequeue();
      Integer actualReturn = ( Integer ) queue.dequeue();
      Integer expectedReturn = null;
      assertEquals( "return value", expectedReturn, actualReturn );
   }

   public void testFullEnqueue() {
      try {
         shortQueue.enqueue( new Integer( 495 ) );
         shortQueue.enqueue( new Integer( 495 ) );
         shortQueue.enqueue( new Integer( 495 ) );
         shortQueue.enqueue( new Integer( 495 ) );
         shortQueue.enqueue( new Integer( 495 ) );
         fail( "Should raise an IndexOutOfBoundsException" );
      } catch ( IndexOutOfBoundsException success ) {
         
      }

   }

}