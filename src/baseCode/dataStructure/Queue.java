package baseCode.dataStructure;
/**
 * Simple Queue implementation.
 *
 * <p>Copyright (c) 2004</p>
 * <p>Institution: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class Queue {

   private Object[] queue;
   private int front;
   private int back;
   private int currentSize;

   private final static int DEFAULTCAPACITY = 10000;

   public Queue() {
      this( DEFAULTCAPACITY );
   }

   public Queue( int capacity ) {
      queue = new Object[capacity];
      makeEmpty();
   }

   /**
    *
    * @param obj Object
    */
   public void enqueue( Object obj ) {
      if ( isFull() ) {
         throw new IndexOutOfBoundsException( "Attempt to enqueue in a full queue" );
      }
      back = increment( back );
      queue[back] = obj;
      currentSize++;
   }

   /**
    *
    * @return Object
    */
   public Object dequeue() {
      if ( isEmpty() ) {
         return null;
      }
      currentSize--;
      Object f = queue[front];
      queue[front] = null;
      front = increment( front );
      return f;
   }

   /**
    *
    * @return boolean
    */
   public boolean isEmpty() {
      return currentSize == 0;
   }

   /**
    *
    * @return boolean
    */
   public boolean isFull() {
      return queue.length == currentSize;
   }

   /**
    *
    */
   public void makeEmpty() {
      currentSize = 0;
      front = 0;
      back = -1;
   }


   private int increment( int i ) {
      if ( ++i == queue.length ) {
         i = 0;
      }
      return i;
   }
}
