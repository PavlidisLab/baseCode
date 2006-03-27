package ubic.basecode.dataStructure;

/**
 * <p>
 * Simple Stack implementation
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
 * @deprecated -- use java.util.List instead.
 */
public class Stack {

   private Object[] stack;
   private int top;
   private final static int DEFAULTCAPACITY = 10000;

   /**
    * Build a stack with the default capacity.
    */
   public Stack() {
      this( DEFAULTCAPACITY );
   }

   /**
    * Build a stack with a given capacity.
    * 
    * @param capacity int
    */
   public Stack( int capacity ) {
      stack = new Object[capacity];
   }

   /**
    * Remove the most recently added item.
    * 
    * @return Object
    */
   public Object pop() {
      if ( isEmpty() ) {
         return null;
      }
      Object topObj = top();
      stack[top--] = null;
      return topObj;

   }

   /**
    * Add an item to the stack.
    * 
    * @param obj Object
    */
   public void push( Object obj ) {
      if ( isFull() ) {
         throw new IndexOutOfBoundsException( "Stack overflow" );
      }
      stack[++top] = obj;
   }

   /**
    * Get the most recently added item, without removing it.
    * 
    * @return Object
    */
   public Object top() {
      if ( isEmpty() ) {
         return null;
      }
      return stack[top];
   }

   /**
    * @return boolean
    */
   public boolean isEmpty() {
      return top == -1;
   }

   /**
    * @return boolean
    */
   public boolean isFull() {
      return top == stack.length - 1;
   }

}